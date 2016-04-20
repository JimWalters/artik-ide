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

import com.google.inject.ImplementedBy;

import org.eclipse.che.ide.api.mvp.View;

import java.util.Set;

/**
 * @author Dmitry Shnurenko
 */
@ImplementedBy(PushToDeviceViewImpl.class)
public interface PushToDeviceView extends View<PushToDeviceView.ActionDelegate> {

    /**
     * Shows dialog window which allows to select device and set target path.
     *
     * @param machines
     *         list of ssh machines
     */
    void show(Set<String> machines);

    /** Returns path to target directory in which file or folder will be copied. */
    String getTargetPath();

    /** Hides dialog */
    void hide();

    interface ActionDelegate {

        /**
         * Sends request to service to push file to ssh machine.
         *
         * @param machineName
         *         ssh machine to which file will be copied.
         */
        void onPushToDeviceClicked(String machineName);
    }
}
