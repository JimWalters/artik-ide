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
package org.eclipse.che.plugin.machine.artik.discovery;

import org.eclipse.che.api.core.ServerException;
import org.eclipse.che.api.core.rest.Service;
import org.eclipse.che.dto.server.DtoFactory;
import org.eclipse.che.plugin.artik.shared.ArtikDevice;
import org.eclipse.che.plugin.artik.shared.dto.ArtikDeviceDto;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.ArrayList;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * Artik device discovery service.
 *
 * @author Artem Zatsarynnyi
 */
@Path("/discovery/{ws-id}")
public class DeviceDiscoveryService extends Service {

    private final DeviceDiscoverer deviceDiscoverer;

    @Inject
    public DeviceDiscoveryService(DeviceDiscoverer deviceDiscoverer) {
        this.deviceDiscoverer = deviceDiscoverer;
    }

    @GET
    @Produces(APPLICATION_JSON)
    public List<ArtikDeviceDto> getDevices() throws ServerException {
        List<ArtikDeviceDto> devices = new ArrayList<>();

        for (String deviceId : deviceDiscoverer.discover()) {
            final ArtikDevice device = deviceDiscoverer.getDeviceInfo(deviceId);

            devices.add(DtoFactory.newDto(ArtikDeviceDto.class)
                                  .withId(device.getId())
                                  .withIPAddress(device.getIPAddress()));
        }

        return devices;
    }
}
