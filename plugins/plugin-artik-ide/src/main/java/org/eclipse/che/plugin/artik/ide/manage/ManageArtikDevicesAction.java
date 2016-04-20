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
package org.eclipse.che.plugin.artik.ide.manage;

import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import org.eclipse.che.api.machine.gwt.client.WsAgentState;
import org.eclipse.che.api.machine.gwt.client.events.WsAgentStateEvent;
import org.eclipse.che.api.machine.gwt.client.events.WsAgentStateHandler;
import org.eclipse.che.ide.api.action.Action;
import org.eclipse.che.ide.api.action.ActionEvent;
import org.eclipse.che.ide.ui.loaders.initialization.LoaderPresenter;
import org.eclipse.che.plugin.artik.ide.ArtikLocalizationConstant;
import org.eclipse.che.plugin.artik.ide.ArtikResources;

import static org.eclipse.che.ide.ui.loaders.initialization.LoaderPresenter.State.COMPLETED;

/**
 * Action for calling Manage Artik Devices dialog.
 *
 * @author Ann Shumilova
 */
@Singleton
public class ManageArtikDevicesAction extends Action implements WsAgentStateHandler {

    private final ManageDevicesPresenter presenter;
    private       WsAgentState           wsAgentState;

    @Inject
    public ManageArtikDevicesAction(EventBus eventBus, ManageDevicesPresenter presenter, ArtikLocalizationConstant locale,
                                    ArtikResources resources) {
        super(locale.manageArtikDevicesAction(), locale.manageArtikDevicesAction(), null, resources.artikIcon());
        this.wsAgentState = WsAgentState.STOPPED;
        this.presenter = presenter;
        eventBus.addHandler(WsAgentStateEvent.TYPE, this);
    }

    @Override
    public void update(ActionEvent event) {
        event.getPresentation().setVisible(WsAgentState.STARTED.equals(wsAgentState));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        presenter.edit();
    }

    @Override
    public void onWsAgentStarted(WsAgentStateEvent event) {
        this.wsAgentState = event.getWsAgentState();
    }

    @Override
    public void onWsAgentStopped(WsAgentStateEvent event) {
        this.wsAgentState = event.getWsAgentState();
    }
}
