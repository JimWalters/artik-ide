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
package org.eclipse.che.plugin.artik.ide.scp.action;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import org.eclipse.che.ide.api.action.Action;
import org.eclipse.che.ide.api.action.ActionEvent;
import org.eclipse.che.ide.api.project.node.HasStorablePath;
import org.eclipse.che.ide.api.selection.Selection;
import org.eclipse.che.ide.api.selection.SelectionAgent;
import org.eclipse.che.plugin.artik.ide.ArtikLocalizationConstants;
import org.eclipse.che.plugin.artik.ide.scp.PushToDeviceManager;

/**
 * The class which represents action to copy file or folder to ssh machine. By default all files and folders will be copied to
 * '/root' directory. To change destination path you have to call {@link ChooseTargetAction}.
 *
 * @author Dmitry Shnurenko
 */
public class PushToDeviceAction extends Action {

    private final SelectionAgent      selectionAgent;
    private final PushToDeviceManager scpManager;
    private final String              machineName;

    @Inject
    public PushToDeviceAction(SelectionAgent selectionAgent,
                              ArtikLocalizationConstants locale,
                              PushToDeviceManager scpManager,
                              @Assisted String machineName) {
        super(machineName, locale.pushToDeviceDescription(), null, null);

        this.selectionAgent = selectionAgent;
        this.machineName = machineName;
        this.scpManager = scpManager;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        scpManager.pushToDevice(machineName, getSelectedElementPath(), "/root");
    }

    private String getSelectedElementPath() {
        Selection<?> selection = selectionAgent.getSelection();
        if (selection.isEmpty()) {
            return "";
        }

        Object selectedElement = selection.getHeadElement();

        if (selectedElement instanceof HasStorablePath) {
            return ((HasStorablePath)selectedElement).getStorablePath();
        }

        return "";
    }
}
