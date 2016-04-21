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
package org.eclipse.che.plugin.artik.ide.scp;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.ide.ui.window.Window;
import org.eclipse.che.plugin.artik.ide.ArtikLocalizationConstants;

import java.util.Set;

/**
 * @author Dmitry Shnurenko
 */
@Singleton
public class PushToDeviceViewImpl extends Window implements PushToDeviceView {
    interface PushToDeviceViewImplUiBinder extends UiBinder<Widget, PushToDeviceViewImpl> {
    }

    private static final PushToDeviceViewImplUiBinder UI_BINDER = GWT.create(PushToDeviceViewImplUiBinder.class);

    private ActionDelegate delegate;

    @UiField
    ListBox listBox;
    @UiField
    TextBox textBox;
    @UiField(provided = true)
    final ArtikLocalizationConstants locale;

    @Inject
    public PushToDeviceViewImpl(ArtikLocalizationConstants locale) {
        this.locale = locale;
        setWidget(UI_BINDER.createAndBindUi(this));

        setHideOnEscapeEnabled(true);
        setTitle(locale.pushToDevice());

        Button pushButton = createButton(locale.pushToDevice(), "push-to-device-apply", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                String machineName = listBox.getValue(listBox.getSelectedIndex());
                delegate.onPushToDeviceClicked(machineName);
            }
        });
        Button cancelButton = createButton(locale.cancelButton(), "push-to-device-cancel", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                hide();
            }
        });

        addButtonToFooter(pushButton);
        addButtonToFooter(cancelButton);
    }

    @Override
    public void show(Set<String> machineNames) {
        listBox.clear();
        textBox.setText("/root");

        for (String machineName : machineNames) {
            listBox.addItem(machineName);
        }

        show();
    }

    @Override
    public String getTargetPath() {
        return textBox.getText();
    }

    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }
}
