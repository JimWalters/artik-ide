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
 * I18n constants for the Artik extension.
 *
 * @author Dmitry Shnurenko
 */
public interface ArtikLocalizationConstants extends Messages {

    @Key("push.to.device.description")
    String pushToDeviceDescription();

    @Key("push.to.device")
    String pushToDevice();

    @Key("push.to.device.success")
    String pushToDeviceSuccess(String fileName, String targetPath);

    @Key("push.to.device.fail")
    String pushToDeviceFail(String fileName, String targetPath);

    @Key("choose.device.label")
    String chooseDeviceLabel();

    @Key("target.path")
    String targetPath();

    @Key("choose.target")
    String chooseTarget();

    @Key("cancel.button")
    String cancelButton();
}
