/*******************************************************************************
 * Copyright (c) 2012-2016 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.plugin.artik.ide.scp.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.api.machine.gwt.client.DevMachine;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.rest.AsyncRequestFactory;
import org.eclipse.che.ide.ui.loaders.request.LoaderFactory;

/**
 * @author Dmitry Shnurenko
 */
@Singleton
public class PushToDeviceServiceClientImpl implements PushToDeviceServiceClient {

    private final AsyncRequestFactory asyncRequestFactory;
    private final LoaderFactory       loaderFactory;
    private final AppContext          appContext;

    @Inject
    public PushToDeviceServiceClientImpl(AsyncRequestFactory asyncRequestFactory,
                                         LoaderFactory loaderFactory,
                                         AppContext appContext) {
        this.asyncRequestFactory = asyncRequestFactory;
        this.loaderFactory = loaderFactory;
        this.appContext = appContext;
    }

    @Override
    public Promise<Void> pushToDevice(String machineId, String sourceFile, String targetPath) {
        DevMachine devMachine = appContext.getDevMachine();

        String pathToScpService = devMachine.getWsAgentBaseUrl() + "/scp/" + appContext.getWorkspaceId();
        final String url = pathToScpService + "?machine_id=" + machineId + "&source_file=" + sourceFile + "&target_path=" + targetPath;

        return asyncRequestFactory.createPostRequest(url, null)
                                  .loader(loaderFactory.newLoader("Pushing to device..."))
                                  .send();
    }
}
