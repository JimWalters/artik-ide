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

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import org.eclipse.che.api.machine.server.exception.MachineException;
import org.eclipse.che.api.machine.server.spi.Instance;
import org.eclipse.che.api.machine.server.terminal.WebsocketTerminalFilesPathProvider;
import org.eclipse.che.plugin.machine.ssh.SshMachineImplTerminalLauncher;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * author Alexander Garagatyi
 */
public class ArtikMachineTerminalLauncher extends SshMachineImplTerminalLauncher {
    private static final Logger LOG = getLogger(ArtikMachineTerminalLauncher.class);

    private final ExecutorService executor;

    @Inject
    public ArtikMachineTerminalLauncher(@Named(TERMINAL_LAUNCH_COMMAND_PROPERTY) String runTerminalCommand,
                                        @Named(TERMINAL_LOCATION_PROPERTY) String terminalLocation,
                                        WebsocketTerminalFilesPathProvider terminalPathProvider) {
        super(runTerminalCommand, terminalLocation, terminalPathProvider);
        this.executor = Executors.newCachedThreadPool(new ThreadFactoryBuilder().setNameFormat("MachineTerminalLauncher-%d")
                                                                                .setDaemon(true)
                                                                                .build());
    }

    @Override
    public void launchTerminal(Instance machine) throws MachineException {
        executor.execute(() -> {
            try {
                super.launchTerminal(machine);
            } catch (MachineException e) {
                LOG.error(e.getLocalizedMessage(), e);
            }
        });
    }

    @Override
    public String getMachineType() {
        return "artik";
    }
}
