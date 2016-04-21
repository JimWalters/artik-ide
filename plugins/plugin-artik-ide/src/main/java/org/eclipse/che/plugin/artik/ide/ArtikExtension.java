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
package org.eclipse.che.plugin.artik.ide;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import org.eclipse.che.api.machine.gwt.client.events.WsAgentStateEvent;
import org.eclipse.che.api.machine.gwt.client.events.WsAgentStateHandler;
import org.eclipse.che.ide.api.action.ActionManager;
import org.eclipse.che.ide.api.action.DefaultActionGroup;
import org.eclipse.che.ide.api.action.IdeActions;
import org.eclipse.che.ide.api.constraints.Constraints;
import org.eclipse.che.ide.api.extension.Extension;
import org.eclipse.che.ide.api.icon.Icon;
import org.eclipse.che.ide.api.icon.IconRegistry;
import org.eclipse.che.plugin.artik.ide.scp.PushToDeviceManager;
import org.eclipse.che.plugin.artik.ide.manage.ManageArtikDevicesAction;

/**
 * @author Dmitry Shnurenko
 */
@Singleton
@Extension(title = "Artik", version = "1.0.0")
public class ArtikExtension {

    @Inject
    public ArtikExtension(EventBus eventBus, final PushToDeviceManager pushToDeviceManager, IconRegistry iconRegistry, ArtikResources artikResources) {
        artikResources.getCss().ensureInjected();

        eventBus.addHandler(WsAgentStateEvent.TYPE, new WsAgentStateHandler() {
            @Override
            public void onWsAgentStarted(WsAgentStateEvent wsAgentStateEvent) {
                pushToDeviceManager.fetchSshMachines();
            }

            @Override
            public void onWsAgentStopped(WsAgentStateEvent wsAgentStateEvent) {

            }
        });

        iconRegistry.registerIcon(new Icon("artik.machine.icon", artikResources.artikIcon()));
    }

    @Inject
    private void prepareActions(ManageArtikDevicesAction manageDevicesAction,
                                ActionManager actionManager, ArtikResources artikResources) {
        artikResources.getCss().ensureInjected();

        actionManager.registerAction("manageArtikDevices", manageDevicesAction);

        DefaultActionGroup centerToolbarGroup = (DefaultActionGroup)actionManager.getAction(IdeActions.GROUP_CENTER_TOOLBAR);
        centerToolbarGroup.add(manageDevicesAction, Constraints.FIRST);
    }
}
