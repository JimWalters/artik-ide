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
package org.eclipse.che.plugin.artik.ide.scp.service;

import com.google.inject.ImplementedBy;

import org.eclipse.che.api.promises.client.Promise;

/**
 * @author Dmitry Shnurenko
 */
@ImplementedBy(PushToDeviceServiceClientImpl.class)
public interface PushToDeviceServiceClient {

    /**
     * Sends request to service to do secure copy file or folder to ssh machine.
     *
     * @param machineId
     *         the machine id on which the file will be copied
     * @param sourceFilePath
     *         path to file which will be copied
     * @param targetPath
     *         path to folder into which file will be copied
     * @return {@link Promise} which contains information about request
     */
    Promise<Void> pushToDevice(String machineId, String sourceFilePath, String targetPath);
}
