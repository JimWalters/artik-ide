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
package org.eclipse.che.plugin.artik.ide;

import com.google.gwt.i18n.client.Messages;

public interface ArtikLocalizationConstant extends Messages {

    @Key("manage.artik.devices.action")
    String manageArtikDevicesAction();

    @Key("manage.artik.devices.view.title")
    String manageArtikDevicesViewTitle();

    @Key("manage.artik.devices.view.device.definition")
    String manageArtikDevicesViewDeviceDefiinition();

    @Key("manage.artik.devices.view.new.device.hint")
    String manageArtikDevicesViewNewDeviceHint();

    @Key("manage.artik.devices.view.name")
    String manageArtikDevicesViewName();

    @Key("manage.artik.devices.view.host")
    String manageArtikDevicesViewHost();

    @Key("manage.artik.devices.view.port")
    String manageArtikDevicesViewPort();

    @Key("manage.artik.devices.view.login")
    String manageArtikDevicesViewLogin();

    @Key("manage.artik.devices.view.password")
    String manageArtikDevicesViewPassword();

    @Key("device.save.success")
    String deviceSaveSuccess();

    @Key("device.save.error")
    String deviceSaveError();

    @Key("device.connect.progress")
    String deviceConnectProgress(String device);

    @Key("device.connect.success")
    String deviceConnectSuccess(String device);

    @Key("device.connect.error")
    String deviceConnectError(String device);

    @Key("device.disconnect.success")
    String deviceDisconnectSuccess(String device);

    @Key("device.disconnect.error")
    String deviceDisconnectError(String device);

    @Key("device.delete.confirm")
    String deviceDeleteConfirm(String device);

    @Key("device.delete.success")
    String deviceDeleteSuccess(String device);

    @Key("device.delete.error")
    String deviceDeleteError(String device);
}
