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

import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.plugin.artik.shared.dto.ArtikDeviceInfo;

import java.util.List;

/**
 *
 */
public interface ArtikDeviceServiceClient {

    Promise<List<ArtikDeviceInfo>> getArtikDevices();

    Promise<ArtikDeviceInfo> registerArtikDevices(ArtikDeviceInfo artikDevice);

    Promise<ArtikDeviceInfo> unregisterArtikDevices(String ip);

}
