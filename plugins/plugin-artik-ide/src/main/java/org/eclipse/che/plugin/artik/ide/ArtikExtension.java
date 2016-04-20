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
package org.eclipse.che.plugin.artik.ide;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import org.eclipse.che.api.machine.gwt.client.events.WsAgentStateEvent;
import org.eclipse.che.api.machine.gwt.client.events.WsAgentStateHandler;
import org.eclipse.che.ide.api.extension.Extension;
import org.eclipse.che.plugin.artik.ide.scp.PushToDeviceManager;

/**
 * @author Dmitry Shnurenko
 */
@Singleton
@Extension(title = "Artik", version = "1.0.0")
public class ArtikExtension {

    @Inject
    public ArtikExtension(EventBus eventBus, final PushToDeviceManager pushToDeviceManager) {
        eventBus.addHandler(WsAgentStateEvent.TYPE, new WsAgentStateHandler() {
            @Override
            public void onWsAgentStarted(WsAgentStateEvent wsAgentStateEvent) {
                pushToDeviceManager.fetchSshMachines();
            }

            @Override
            public void onWsAgentStopped(WsAgentStateEvent wsAgentStateEvent) {

            }
        });
    }
}
