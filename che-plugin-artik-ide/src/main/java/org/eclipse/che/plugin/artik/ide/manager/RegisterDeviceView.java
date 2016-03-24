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
package org.eclipse.che.plugin.artik.ide.manager;

import org.eclipse.che.ide.api.mvp.View;


/**
 * //
 *
 * @author Vitalii Parfonov
 */
    public interface RegisterDeviceView extends View<RegisterDeviceView.ActionDelegate> {

        /** Show view. */
        void show();

        /** Close view. */
        void close();

        /** Returns device IP */
        String getIpAddress();


        /** Returns recipe URL. */
        String getLogin();

        /** Sets recipe URL. */
        void getPassword();

        /** Sets error hint visibility. */
        void setErrorHint(boolean show);

        /** Sets 'no recipe' hint visibility. */
        void setNoRecipeHint(boolean show);

        /**
         * Sets whether 'Create' button is enabled.
         *
         * @param enabled
         *         <code>true</code> to enable the button,
         *         <code>false</code> to disable it
         */
        void setRegisterButtonState(boolean enabled);

        /** Action handler for the view actions/controls. */
        interface ActionDelegate {

            /** Called when machines name has been changed. */
            void onIpChanged();

            /** Called when 'Create' button has been clicked. */
            void onRegisterClicked();

            /** Called when 'Cancel' button has been clicked. */
            void onCancelClicked();
        }
}
