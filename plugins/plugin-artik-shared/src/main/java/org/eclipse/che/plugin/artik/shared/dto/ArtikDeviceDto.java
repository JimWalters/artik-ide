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
package org.eclipse.che.plugin.artik.shared.dto;

import org.eclipse.che.dto.shared.DTO;
import org.eclipse.che.plugin.artik.shared.ArtikDevice;

/**
 * DTO for {@link ArtikDevice}.
 *
 * @author Artem Zatsarynnyi
 */
@DTO
public interface ArtikDeviceDto extends ArtikDevice {

    void setId(String id);

    ArtikDeviceDto withId(String id);

    void setIPAddress(String ip);

    ArtikDeviceDto withIPAddress(String ip);
}
