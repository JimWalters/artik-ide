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
package org.eclipse.che.plugin.artik.ide;

import com.google.gwt.i18n.client.Messages;

/**
 * Localization constants. Interface to represent the constants defined in resource bundle:
 * 'ArtikLocalizationConstant.properties'.
 *
 * @author Vitalii PArfonov
 */
public interface ArtikLocalizationConstant extends Messages {

    @Key("artik.plugin.register.device.title")
    @DefaultMessage("Register Device")
    String registerArtikDeviceTitle();

    @Key("artik.plugin.register.device.description")
    @DefaultMessage("Register New Device")
    String registerArtikDeviceDescription();

    @Key("artik.plugin.register.device.ip.address")
    @DefaultMessage("Artik Device IP:")
    String ipAddress();

    @Key("artik.plugin.register.device.login")
    @DefaultMessage("login")
    String registerArtikDeviceLogin();

    @Key("artik.plugin.register.device.password")
    @DefaultMessage("password")
    String registerArtikDevicePassword();

    @Key("artik.plugin.register.device.register")
    @DefaultMessage("register")
    String viewRegisterDeviceButton();

    @Key("artik.plugin.cancel")
    @DefaultMessage("cancel")
    String cancelButton();
}
