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
package org.eclipse.che.plugin.machine.artik;

import org.eclipse.che.api.machine.server.terminal.WebsocketTerminalFilesPathProvider;
import org.eclipse.che.plugin.machine.ssh.SshMachineImplTerminalLauncher;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * author Alexander Garagatyi
 */
public class ArtikMachineTerminalLauncher extends SshMachineImplTerminalLauncher {
    @Inject
    public ArtikMachineTerminalLauncher(@Named(TERMINAL_LAUNCH_COMMAND_PROPERTY) String runTerminalCommand,
                                        @Named(TERMINAL_LOCATION_PROPERTY) String terminalLocation,
                                        WebsocketTerminalFilesPathProvider terminalPathProvider) {
        super(runTerminalCommand, terminalLocation, terminalPathProvider);
    }

    @Override
    public String getMachineType() {
        return "artik";
    }
}
