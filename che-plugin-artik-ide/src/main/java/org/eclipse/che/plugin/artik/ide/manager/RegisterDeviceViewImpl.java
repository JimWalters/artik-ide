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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import org.eclipse.che.ide.ui.window.Window;
import org.eclipse.che.ide.util.loging.Log;
import org.eclipse.che.plugin.artik.ide.ArtikLocalizationConstant;

/**
 * //
 *
 * @author Vitalii Parfonov
 */
public class RegisterDeviceViewImpl extends Window implements RegisterDeviceView {

    private Button createButton;
    private Button cancelButton;

    interface RegisterDeviceViewImplUiBinder extends UiBinder<Widget, RegisterDeviceViewImpl> {
    }

    private static final RegisterDeviceViewImplUiBinder UI_BINDER = GWT.create(RegisterDeviceViewImplUiBinder.class);


    @UiField(provided = true)
    ArtikLocalizationConstant localizationConstant;
    @UiField
    TextBox                   ipAddress;
    @UiField
    TextBox                   login;
    @UiField
    TextBox                   password;


    @Inject
    public RegisterDeviceViewImpl(ArtikLocalizationConstant localizationConstant) {
        this.localizationConstant = localizationConstant;
        Log.info(getClass(), localizationConstant.registerArtikDeviceDescription());
        setWidget(UI_BINDER.createAndBindUi(this));
        setTitle(localizationConstant.registerArtikDeviceTitle());
        createFooterButtons();
    }

    private void createFooterButtons() {
        createButton = createButton(localizationConstant.viewRegisterDeviceButton(), "window-register-device-register",
                                    new ClickHandler() {
                                        @Override
                                        public void onClick(ClickEvent event) {
                                            //delegate.onCreateClicked();
                                        }
                                    });

        cancelButton = createButton(localizationConstant.cancelButton(), "window-register-device-cancel",
                                    new ClickHandler() {
                                        @Override
                                        public void onClick(ClickEvent event) {
//                                            delegate.onCancelClicked();
                                        }
                                    });

        addButtonToFooter(createButton);
        addButtonToFooter(cancelButton);
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void close() {

    }

    @Override
    public String getIpAddress() {
        return null;
    }

    @Override
    public String getLogin() {
        return null;
    }

    @Override
    public void getPassword() {

    }

    @Override
    public void setErrorHint(boolean show) {

    }

    @Override
    public void setNoRecipeHint(boolean show) {

    }

    @Override
    public void setRegisterButtonState(boolean enabled) {

    }

    @Override
    public void setDelegate(ActionDelegate delegate) {

    }

    @Override
    public Widget asWidget() {
        return null;
    }
}
