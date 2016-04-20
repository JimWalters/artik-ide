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
package org.eclipse.che.plugin.artik.ide.scp;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Set;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

/**
 * @author Dmitry Shnurenko
 */
@RunWith(MockitoJUnitRunner.class)
public class PushToDevicePresenterTest {
    @Mock
    private PushToDeviceView    view;
    @Mock
    private PushToDeviceManager scpManager;

    @InjectMocks
    private PushToDevicePresenter presenter;

    @Test
    public void actionDelegateShouldBeSet() {
        verify(view).setDelegate(presenter);
    }

    @Test
    public void dialogWithSshMachinesShouldBeShown() {
        presenter.show("path");

        verify(scpManager).getMachineNames();
        verify(view).show(Matchers.<Set<String>>anyObject());
    }

    @Test
    public void whetherExistSshMachineShouldBeReturned() {
        presenter.isSshDeviceExist();

        verify(scpManager).isSshDeviceExist();
    }

    @Test
    public void fileOrFolderShouldBePushedToDeviceWhenUserClickPushToDeviceButton() {
        presenter.show("path");

        presenter.onPushToDeviceClicked("name");

        verify(view).getTargetPath();
        verify(scpManager).pushToDevice(eq("name"), eq("path"), anyString());
        verify(view).hide();
    }
}
