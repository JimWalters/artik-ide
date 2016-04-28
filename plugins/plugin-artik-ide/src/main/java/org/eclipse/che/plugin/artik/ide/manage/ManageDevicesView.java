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
package org.eclipse.che.plugin.artik.ide.manage;

import com.google.inject.ImplementedBy;

import org.eclipse.che.ide.api.mvp.View;

import java.util.List;

/**
 * View to managing Artik devices.
 *
 * @author Vitaliy Guliy
 * @author Ann Shumilova
 *
 */
@ImplementedBy(ManageDevicesViewImpl.class)
public interface ManageDevicesView extends View<ManageDevicesView.ActionDelegate> {

    /**
     * Shows manage devices dialog.
     */
    void show();

    /**
     * Hides Manage devices dialog.
     */
    void hide();

    /**
     * Resets the view to its default value.
     */
    void clear();

    /**
     * Shows a list of available devices.
     *
     * @param devices
     */
    void showDevices(List<Device> devices);

    /**
     * Selects a device in the list,
     *
     * @param device
     *         device to select
     */
    void selectDevice(Device device);


    void showHintPanel();

    void showInfoPanel();

    void showPropertiesPanel();


    /**
     * Sets device name.
     *
     * @param deviceName
     *          device name
     */
    void setDeviceName(String deviceName);

    /**
     * Returns value of device name field.
     *
     * @return
     *          device name field value
     */
    String getDeviceName();

    /**
     * Sets SSH host value.
     *
     * @param host
     *          host value
     */
    void setHost(String host);

    /**
     * Sets the list of discovered hosts
     *
     * @param hosts
     *          available hosts
     */
    void setHosts(List<String> hosts);

    /**
     * Returns value of host field.
     *
     * @return
     *          value of host field
     */
    String getHost();

    /**
     * Sets SSH port value.
     *
     * @param port
     *          port value
     */
    void setPort(String port);

    /**
     * Returns value of port field.
     *
     * @return
     *          value of port field
     */
    String getPort();

    /**
     * Sets SSH user name.
     *
     * @param userName
     *          user name
     */
    void setUserName(String userName);

    /**
     * Returns value of user name field.
     *
     * @return
     *         value of user name field
     */
    String getUserName();

    /**
     * Sets SSH password.
     *
     * @param password
     *          password
     */
    void setPassword(String password);

    /**
     * Returns value of password field.
     *
     * @return
     *          value of password field
     */
    String getPassword();

    /**
     * Enables or disables Cancel button.
     *
     * @param enable
     *          enabled state
     */
    void enableCancelButton(boolean enable);

    /**
     * Enables or disables Connect button.
     * @param enable
     */
    void enableConnectButton(boolean enable);

    /**
     * Enables or disables the editing of inputs.
     * @param enable
     */
    void enableEditing(boolean enable);


    /**
     * Changes the text of Connect button.
     *
     * @param text
     *          new text
     */
    void setConnectButtonText(String text);

    /**
     * Focuses and selects all the text in the Name field.
     */
    void selectDeviceName();

    /**
     * Adds error mark to device name field.
     */
    void markDeviceNameInvalid();

    /**
     * Removes error mark from device name field.
     */
    void unmarkDeviceName();

    /**
     * Adds error mark to host field.
     */
    void markHostInvalid();

    /**
     * Removes error mark from host field.
     */
    void unmarkHost();

    /**
     * Adds error mark to port field.
     */
    void markPortInvalid();

    /**
     * Removes error mark from port field.
     */
    void unmarkPort();


    interface ActionDelegate {

        // Perform actions when clicking Close button
        void onCloseClicked();

        // Perform actions when clicking Add device button
        void onAddDevice(String category);

        // Is called when device is deleted
        void onDeleteDevice(Device device);

        // Perform actions when selecting a device
        void onDeviceSelected(Device device);

        void onDeviceNameChanged(String value);

        void onHostChanged(String value);

        void onPortChanged(String value);

        void onUserNameChanged(String value);

        void onPasswordChanged(String value);

        void onCancelClicked();

        void onConnectClicked();

    }

}
