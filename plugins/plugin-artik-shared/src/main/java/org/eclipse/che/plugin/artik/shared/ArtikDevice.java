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
package org.eclipse.che.plugin.artik.shared;

/**
 * Defines a contract for Artik device.
 *
 * @author Artem Zatsarynnyi
 */
public interface ArtikDevice {

    /** Returns device identifier. */
    String getId();

    /** Returns IP address of the device. */
    String getIPAddress();
}
