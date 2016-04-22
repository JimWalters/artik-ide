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

import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.plugin.artik.shared.dto.ArtikDeviceDto;

import java.util.List;

/**
 * Client for device discovery service.
 *
 * @author Artem Zatsarynnyi
 */
public interface DeviceDiscoveryServiceClient {

    /**
     * Ask server to discover the connected devices.
     *
     * @return a promise that resolves to the list of {@link ArtikDeviceDto}, or rejects with an error
     */
    Promise<List<ArtikDeviceDto>> getDevices();
}
