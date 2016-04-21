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

import org.eclipse.che.plugin.machine.ssh.SshMachineFactory;
import org.eclipse.che.plugin.machine.ssh.SshMachineInstanceProvider;

import javax.inject.Inject;
import java.io.IOException;

/**
 * Implementation of Artik device as SSH machine.
 *
 * @author Alexander Garagatyi
 */
public class ArtikMachineInstanceProvider extends SshMachineInstanceProvider {
    @Inject
    public ArtikMachineInstanceProvider(SshMachineFactory sshMachineFactory) throws IOException {
        super(sshMachineFactory);
    }

    @Override
    public String getType() {
        return "artik";
    }
}
