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

import org.eclipse.che.plugin.artik.shared.ArtikDevice;

import java.util.Objects;

/**
 * Data object for {@link ArtikDevice}.
 *
 * @author Artem Zatsarynnyi
 */
public class ArtikDeviceImpl implements ArtikDevice {

    private final String id;
    private final String ipAddress;

    public ArtikDeviceImpl(String id, String ipAddress) {
        this.id = id;
        this.ipAddress = ipAddress;
    }

    public ArtikDeviceImpl(ArtikDevice device) {
        this(device.getId(), device.getIPAddress());
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getIPAddress() {
        return ipAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ArtikDeviceImpl)) return false;
        ArtikDeviceImpl device = (ArtikDeviceImpl)o;
        return Objects.equals(id, device.id) &&
               Objects.equals(ipAddress, device.ipAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, ipAddress);
    }
}
