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
package org.eclipse.che.plugin.artik.ide.discovery;

import com.google.inject.Inject;

import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.machine.DevMachine;
import org.eclipse.che.ide.rest.AsyncRequestFactory;
import org.eclipse.che.ide.rest.DtoUnmarshallerFactory;
import org.eclipse.che.ide.ui.loaders.request.LoaderFactory;
import org.eclipse.che.plugin.artik.shared.dto.ArtikDeviceDto;

import java.util.List;

/**
 * Implementation of {@link DeviceDiscoveryServiceClient}.
 *
 * @author Artem Zatsarynnyi
 */
public class DeviceDiscoveryServiceClientImpl implements DeviceDiscoveryServiceClient {

    private final AsyncRequestFactory    asyncRequestFactory;
    private final LoaderFactory          loaderFactory;
    private final AppContext             appContext;
    private final DtoUnmarshallerFactory dtoUnmarshallerFactory;

    @Inject
    public DeviceDiscoveryServiceClientImpl(AsyncRequestFactory asyncRequestFactory,
                                            LoaderFactory loaderFactory,
                                            AppContext appContext,
                                            DtoUnmarshallerFactory dtoUnmarshallerFactory) {
        this.asyncRequestFactory = asyncRequestFactory;
        this.loaderFactory = loaderFactory;
        this.appContext = appContext;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
    }

    @Override
    public Promise<List<ArtikDeviceDto>> getDevices() {
        final DevMachine devMachine = appContext.getDevMachine();
        final String url = devMachine.getWsAgentBaseUrl() + "/discovery/" + appContext.getWorkspaceId();

        return asyncRequestFactory.createGetRequest(url)
                                  .loader(loaderFactory.newLoader("Discovering the connected devices..."))
                                  .send(dtoUnmarshallerFactory.newListUnmarshaller(ArtikDeviceDto.class));
    }
}
