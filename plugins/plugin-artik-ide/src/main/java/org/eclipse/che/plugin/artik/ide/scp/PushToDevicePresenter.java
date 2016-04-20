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
package org.eclipse.che.plugin.artik.ide.scp;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * The class contains business logic which allows to choose the destination and to do secure copy files and folders to ssh machine.
 *
 * @author Dmitry Shnurenko
 */
@Singleton
public class PushToDevicePresenter implements PushToDeviceView.ActionDelegate {

    private final PushToDeviceView    view;
    private final PushToDeviceManager scpManager;

    private String sourcePath;

    @Inject
    public PushToDevicePresenter(PushToDeviceView view, PushToDeviceManager scpManager) {
        this.view = view;
        this.view.setDelegate(this);
        this.scpManager = scpManager;
    }

    /**
     * Find all ssh machines and shows dialog which allows select device and set path to which selected file or folder will be copied.
     *
     * @param sourcePath
     *         path to file or folder which will be copied
     */
    public void show(String sourcePath) {
        this.sourcePath = sourcePath;

        view.show(scpManager.getMachineNames());
    }

    public boolean isSshDeviceExist() {
        return scpManager.isSshDeviceExist();
    }

    @Override
    public void onPushToDeviceClicked(String machineName) {
        scpManager.pushToDevice(machineName, sourcePath, view.getTargetPath());

        view.hide();
    }
}
