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
package org.eclipse.che.plugin.machine.artik;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

/**
 * Provides bindings needed for artik machine implementation usage.
 *
 * @author Alexander Garagatyi
 */
public class ArtikMachineModule extends AbstractModule {
    @Override
    protected void configure() {
        Multibinder<org.eclipse.che.api.machine.server.spi.InstanceProvider> machineProviderMultibinder =
                Multibinder.newSetBinder(binder(),
                                         org.eclipse.che.api.machine.server.spi.InstanceProvider.class);
        machineProviderMultibinder.addBinding()
                                  .to(org.eclipse.che.plugin.machine.artik.ArtikMachineInstanceProvider.class);

        Multibinder<org.eclipse.che.api.machine.server.terminal.MachineImplSpecificTerminalLauncher> terminalLaunchers =
                Multibinder.newSetBinder(binder(),
                                         org.eclipse.che.api.machine.server.terminal.MachineImplSpecificTerminalLauncher.class);
        terminalLaunchers.addBinding().to(org.eclipse.che.plugin.machine.artik.ArtikMachineTerminalLauncher.class);
    }
}
