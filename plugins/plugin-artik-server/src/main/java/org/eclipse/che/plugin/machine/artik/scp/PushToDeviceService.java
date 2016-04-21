/*******************************************************************************
 * Copyright (c) 2016 Samsung Electronics Co., Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - Initial implementation
 *   Samsung Electronics Co., Ltd. - Initial implementation
 *******************************************************************************/
package org.eclipse.che.plugin.machine.artik.scp;

import io.swagger.annotations.ApiOperation;

import com.google.common.annotations.Beta;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import org.eclipse.che.api.core.ApiException;
import org.eclipse.che.api.core.ServerException;
import org.eclipse.che.api.core.model.machine.Machine;
import org.eclipse.che.api.core.model.machine.MachineSource;
import org.eclipse.che.api.core.rest.HttpJsonRequestFactory;
import org.eclipse.che.api.core.rest.HttpJsonResponse;
import org.eclipse.che.api.core.rest.Service;
import org.eclipse.che.api.core.util.CommandLine;
import org.eclipse.che.api.core.util.ProcessUtil;
import org.eclipse.che.api.machine.server.MachineService;
import org.eclipse.che.api.machine.server.recipe.RecipeService;
import org.eclipse.che.api.machine.shared.dto.MachineDto;
import org.eclipse.che.api.machine.shared.dto.recipe.RecipeDescriptor;
import org.eclipse.che.commons.json.JsonHelper;
import org.eclipse.che.commons.json.JsonParseException;
import org.everrest.core.impl.provider.json.JsonValue;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * The service allows to copy files and folders to ssh machines using secure copy protocol.
 *
 * @author Dmitry Shnurenko
 */
@Path("/scp/{ws-id}")
@Singleton
@Beta
public class PushToDeviceService extends Service {
    private static final String DEFAULT_SSH_PORT          = "22";
    private static final String DEFAULT_PROJECTS_LOCATION = "/projects";

    @Inject
    @Named("api.endpoint")
    private String                 apiEndpoint;
    @Inject
    private HttpJsonRequestFactory httpJsonRequestFactory;

    /**
     * Copies file or folder to ssh machine using secure copy protocol. It is necessary to install 'sshpass' to machine from which
     * file or folder will be copied.
     *
     * @param machineId
     *         the machine id on which the file will be copied
     * @param sourcePath
     *         path to file which will be copied
     * @param targetPath
     *         path to folder into which file will be copied
     * @return response with status 204 if no error happened during copying
     * @throws ServerException
     *         thrown when some error happened during sending request to other services or if error occurs during execution
     *         of process
     * @throws IOException
     *         thrown when some error occurs during reading from process output
     * @throws JsonParseException
     *         thrown when some error occurs during parsing machine recipe script
     */
    @POST
    @ApiOperation(value = "Copy file or folder to ssh machine")
    public Response pushToDevice(@QueryParam("machine_id") String machineId,
                                 @QueryParam("source_file") String sourcePath,
                                 @QueryParam("target_path") String targetPath) throws ServerException, IOException, JsonParseException {
        Machine machine = getMachineById(machineId);
        RecipeDescriptor recipe = getRecipe(machine);
        String script = recipe.getScript();
        CommandLine commandLine = createCommand(script, sourcePath, targetPath);

        return launchProcess(commandLine);
    }

    private MachineDto getMachineById(String machineId) throws ServerException {
        final String href = UriBuilder.fromUri(apiEndpoint)
                                      .path(MachineService.class)
                                      .path(MachineService.class, "getMachineById")
                                      .build(machineId).toString();

        return sendRequest(href).asDto(MachineDto.class);
    }

    private HttpJsonResponse sendRequest(String href) throws ServerException {
        try {
            return httpJsonRequestFactory.fromUrl(href).useGetMethod().request();
        } catch (IOException | ApiException e) {
            throw new ServerException(e.getMessage());
        }
    }

    private RecipeDescriptor getRecipe(Machine machine) throws ServerException {
        MachineSource source = machine.getConfig().getSource();
        String location = source.getLocation();
        String recipeId = getRecipeId(location);

        final String href = UriBuilder.fromUri(apiEndpoint)
                                      .path(RecipeService.class)
                                      .path(RecipeService.class, "getRecipe")
                                      .build(recipeId).toString();

        return sendRequest(href).asDto(RecipeDescriptor.class);
    }

    private String getRecipeId(String location) {
        location = location.substring(0, location.lastIndexOf("/"));

        return location.substring(location.lastIndexOf("/") + 1);
    }

    private CommandLine createCommand(String script, String sourcePath, String targetPath) throws JsonParseException {
        JsonValue jsonScript = JsonHelper.parseJson(script);

        String host = jsonScript.getElement("host").getStringValue();
        String port = jsonScript.getElement("port").getStringValue();
        String username = jsonScript.getElement("username").getStringValue();
        String password = jsonScript.getElement("password").getStringValue();

        String filePath = DEFAULT_PROJECTS_LOCATION + sourcePath;

        CommandLine commandLine = new CommandLine("sshpass -p", password);
        commandLine.add("scp -o StrictHostKeyChecking=no")
                   .add(Files.isDirectory(Paths.get(filePath)) ? "-r " : "", filePath)
                   .add(username + '@' + host + ':' + (port.equals(DEFAULT_SSH_PORT) ? "" : port + ':') + targetPath);
        return commandLine;
    }

    private Response launchProcess(CommandLine commandLine) throws IOException, ServerException {
        final Process process = Runtime.getRuntime().exec(commandLine.toString());
        try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream(), "UTF-8"))) {
            process.waitFor();

            Object[] errors = errorReader.lines().toArray();
            if (errors.length > 0) {
                String error = Arrays.stream(errors).map(Object::toString).collect(Collectors.joining(" "));
                if (isItHostAddedWarningMessage(error)) {
                    launchProcess(commandLine);
                } else {
                    throw new ServerException("Some error happened during executing command. The reason: " + error);
                }
            }
            return Response.status(204).build();
        } catch (InterruptedException exception) {
            Thread.interrupted();
            ProcessUtil.kill(process);

            throw new ServerException(exception);
        }
    }

    private boolean isItHostAddedWarningMessage(String error) {
        return error.startsWith("Warning: Permanently added") && error.endsWith("to the list of known hosts.");
    }
}
