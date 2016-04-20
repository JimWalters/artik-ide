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
package org.eclipse.che.plugin.artik.ide.scp.action;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.ide.api.action.AbstractPerspectiveAction;
import org.eclipse.che.ide.api.action.ActionEvent;
import org.eclipse.che.ide.api.project.node.HasStorablePath;
import org.eclipse.che.ide.api.selection.Selection;
import org.eclipse.che.ide.api.selection.SelectionAgent;
import org.eclipse.che.plugin.artik.ide.ArtikLocalizationConstants;
import org.eclipse.che.plugin.artik.ide.scp.PushToDevicePresenter;

import javax.validation.constraints.NotNull;
import java.util.Arrays;

/**
 * The action which shows dialog window on which we can choose target ssh machine and destination path to copy file or folder to that
 * machine.
 *
 * @author Dmitry Shnurenko
 */
@Singleton
public class ChooseTargetAction extends AbstractPerspectiveAction {

    private final PushToDevicePresenter presenter;
    private final SelectionAgent        selectionAgent;

    @Inject
    public ChooseTargetAction(ArtikLocalizationConstants locale, PushToDevicePresenter presenter, SelectionAgent selectionAgent) {
        super(Arrays.asList("Project Perspective"), locale.chooseTarget(), locale.pushToDeviceDescription(), null, null);

        this.presenter = presenter;
        this.selectionAgent = selectionAgent;
    }

    @Override
    public void updateInPerspective(@NotNull ActionEvent event) {
        event.getPresentation().setEnabledAndVisible(presenter.isSshDeviceExist());
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

    @Override
    public void actionPerformed(ActionEvent event) {
        presenter.show(getSelectedElementPath());
    }
}
