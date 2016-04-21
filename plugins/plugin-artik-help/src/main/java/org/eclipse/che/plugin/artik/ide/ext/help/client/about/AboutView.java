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
package org.eclipse.che.plugin.artik.ide.ext.help.client.about;

import org.eclipse.che.ide.api.mvp.View;

/**
 * View for displaying About information.
 *
 * @author Ann Shumilova
 * @author Oleksii Orel
 */
public interface AboutView extends View<AboutView.ActionDelegate> {

    interface ActionDelegate {

        /**
         * Performs any support appropriate in response to the user having pressed the OK button
         */
        void onOkClicked();
    }

    /** Close view. */
    void close();

    /** Show About dialog. */
    void showDialog();

    /**
     * Set application's version value.
     *
     * @param version
     */
    void setVersion(String version);

    /**
     * Set application's revision value.
     *
     * @param revision
     */
    void setRevision(String revision);

    /**
     * Set application's build time value.
     *
     * @param time
     */
    void setTime(String time);
}
