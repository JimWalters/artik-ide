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
package org.eclipse.che.plugin.artik.ide.service;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.eclipse.che.api.machine.gwt.client.WsAgentStateController;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.api.workspace.shared.dto.ProjectConfigDto;
import org.eclipse.che.ide.MimeType;
import org.eclipse.che.ide.dto.DtoFactory;
import org.eclipse.che.ide.rest.AsyncRequestFactory;
import org.eclipse.che.ide.rest.DtoUnmarshallerFactory;
import org.eclipse.che.ide.rest.HTTPHeader;
import org.eclipse.che.ide.ui.loaders.request.LoaderFactory;
import org.eclipse.che.plugin.artik.shared.dto.ArtikDeviceInfo;

import java.util.List;

/**
 *
 */
public class ArtikDeviceServiceClientImpl implements ArtikDeviceServiceClient {

    private LoaderFactory          loaderFactory;
    private AsyncRequestFactory    asyncRequestFactory;
    private DtoFactory             dtoFactory;
    private DtoUnmarshallerFactory dtoUnmarshaller;
    private final String extPath;


    @Inject
    protected ArtikDeviceServiceClientImpl(LoaderFactory loaderFactory,
                                           AsyncRequestFactory asyncRequestFactory,
                                           DtoFactory dtoFactory,
                                           DtoUnmarshallerFactory dtoUnmarshaller,
                                           @Named("cheExtensionPath") String extPath) {
        this.loaderFactory = loaderFactory;
        this.asyncRequestFactory = asyncRequestFactory;
        this.dtoFactory = dtoFactory;
        this.dtoUnmarshaller = dtoUnmarshaller;
        this.extPath = extPath;
    }


    @Override
    public Promise<List<ArtikDeviceInfo>> getArtikDevices() {
        final String requestUrl = extPath + "/artik/";
        return asyncRequestFactory.createGetRequest(requestUrl)
                                  .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON)
                                  .loader(loaderFactory.newLoader("Getting Artik's..."))
                                  .send(dtoUnmarshaller.newListUnmarshaller(ArtikDeviceInfo.class));
    }

    @Override
    public Promise<ArtikDeviceInfo> registerArtikDevices(ArtikDeviceInfo artikDevice) {
        return null;
    }

    @Override
    public Promise<ArtikDeviceInfo> unregisterArtikDevices(String ip) {
        return null;
    }
}
