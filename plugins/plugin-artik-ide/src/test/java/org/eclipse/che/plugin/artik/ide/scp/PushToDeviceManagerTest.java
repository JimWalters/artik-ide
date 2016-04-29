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
package org.eclipse.che.plugin.artik.ide.scp;

import com.google.inject.Provider;
import com.google.web.bindery.event.shared.EventBus;

import org.eclipse.che.api.machine.gwt.client.MachineServiceClient;
import org.eclipse.che.api.machine.shared.dto.MachineConfigDto;
import org.eclipse.che.api.machine.shared.dto.MachineDto;
import org.eclipse.che.api.promises.client.Operation;
import org.eclipse.che.api.promises.client.OperationException;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.api.promises.client.PromiseError;
import org.eclipse.che.ide.api.action.ActionManager;
import org.eclipse.che.ide.api.action.DefaultActionGroup;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.api.notification.StatusNotification;
import org.eclipse.che.ide.extension.machine.client.machine.MachineStateEvent;
import org.eclipse.che.plugin.artik.ide.ArtikLocalizationConstants;
import org.eclipse.che.plugin.artik.ide.scp.action.ChooseTargetAction;
import org.eclipse.che.plugin.artik.ide.scp.action.PushToDeviceAction;
import org.eclipse.che.plugin.artik.ide.scp.action.PushToDeviceActionFactory;
import org.eclipse.che.plugin.artik.ide.scp.service.PushToDeviceServiceClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.eclipse.che.api.core.model.machine.MachineStatus.RUNNING;
import static org.eclipse.che.ide.api.notification.StatusNotification.Status.FAIL;
import static org.eclipse.che.ide.api.notification.StatusNotification.Status.SUCCESS;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Dmitry Shnurenko
 */
@RunWith(MockitoJUnitRunner.class)
public class PushToDeviceManagerTest {
    //constructor mocks
    @Mock
    private PushToDeviceServiceClient    scpService;
    @Mock
    private NotificationManager          notificationManager;
    @Mock
    private ArtikLocalizationConstants   locale;
    @Mock
    private ActionManager                actionManager;
    @Mock
    private PushToDeviceActionFactory    pushToDeviceActionFactory;
    @Mock
    private EventBus                     eventBus;
    @Mock
    private MachineServiceClient         machineService;
    @Mock
    private AppContext                   appContext;
    @Mock
    private Provider<ChooseTargetAction> chooseTargetActionProvider;

    //additional mocks
    @Mock
    private Promise<List<MachineDto>> machinesPromise;
    @Mock
    private MachineDto                machineDto;
    @Mock
    private MachineConfigDto          configDto;
    @Mock
    private PushToDeviceAction        pushToDeviceAction;
    @Mock
    private Promise<Void>             voidPromise;
    @Mock
    private MachineStateEvent         machineStateEvent;

    @Captor
    private ArgumentCaptor<Operation<List<MachineDto>>> machinesListCaptor;
    @Captor
    private ArgumentCaptor<Operation<Void>>             voidCaptor;
    @Captor
    private ArgumentCaptor<Operation<PromiseError>>     errorCaptor;

    @InjectMocks
    private PushToDeviceManager pushToDeviceManager;

    @Before
    public void setUp() {
        when(chooseTargetActionProvider.get()).thenReturn(mock(ChooseTargetAction.class));
        when(actionManager.getAction("resourceOperation")).thenReturn(new DefaultActionGroup(actionManager));

        when(machineService.getMachines(anyString())).thenReturn(machinesPromise);
        when(machinesPromise.then(Matchers.<Operation<List<MachineDto>>>anyObject())).thenReturn(machinesPromise);

        when(machineDto.getConfig()).thenReturn(configDto);
        when(configDto.getType()).thenReturn("ssh");
        when(machineDto.getStatus()).thenReturn(RUNNING);
        when(machineDto.getId()).thenReturn("id");
        when(configDto.getName()).thenReturn("name");

        when(pushToDeviceActionFactory.create(anyString())).thenReturn(pushToDeviceAction);

        pushToDeviceManager.fetchSshMachines();
    }

    @Test
    public void allSshMachinesShouldBeAddedToPushToDeviceActionGroup() throws OperationException {
        verify(actionManager).getAction("resourceOperation");
        verify(eventBus).addHandler(MachineStateEvent.TYPE, pushToDeviceManager);

        verify(machinesPromise).then(machinesListCaptor.capture());
        machinesListCaptor.getValue().apply(Arrays.asList(machineDto));

        verify(pushToDeviceActionFactory).create("name");
        verify(actionManager).registerAction("id", pushToDeviceAction);

        assertThat(pushToDeviceManager.getMachineNames().contains("name"), is(true));
        assertThat(pushToDeviceManager.getMachineNames().size(), is(equalTo(1)));
    }

    @Test
    public void fileOrFolderShouldBePushedToDeviceSuccess() throws OperationException {
        pushToDevice();

        verify(voidPromise).then(voidCaptor.capture());
        voidCaptor.getValue().apply(null);

        verify(locale).pushToDeviceSuccess("file", "/destination/path");
        verify(notificationManager).notify(anyString(), eq(SUCCESS), eq(StatusNotification.DisplayMode.FLOAT_MODE));
    }

    private void pushToDevice() throws OperationException {
        when(scpService.pushToDevice(anyString(), anyString(), anyString())).thenReturn(voidPromise);
        when(voidPromise.then(Matchers.<Operation<Void>>anyObject())).thenReturn(voidPromise);
        when(voidPromise.catchError(Matchers.<Operation<PromiseError>>anyObject())).thenReturn(voidPromise);

        verify(machinesPromise).then(machinesListCaptor.capture());
        machinesListCaptor.getValue().apply(Arrays.asList(machineDto));

        pushToDeviceManager.pushToDevice("name", "path/to/file", "/destination/path");

        verify(scpService).pushToDevice("id", "path/to/file", "/destination/path");
    }

    @Test
    public void fileOrFolderShouldBePushedToDeviceFail() throws OperationException {
        pushToDevice();

        verify(voidPromise).catchError(errorCaptor.capture());
        errorCaptor.getValue().apply(mock(PromiseError.class));

        verify(locale).pushToDeviceFail("file", "/destination/path");
        verify(notificationManager).notify(anyString(), eq(FAIL), eq(StatusNotification.DisplayMode.FLOAT_MODE));
    }

    @Test
    public void trueValueShouldBeReturnWhenSshDicesExist() throws OperationException {
        verify(machinesPromise).then(machinesListCaptor.capture());
        machinesListCaptor.getValue().apply(Arrays.asList(machineDto));

        assertThat(pushToDeviceManager.isSshDeviceExist(), is(true));
    }

    @Test
    public void deviceActionShouldBeAddedToDeviceActionGroupWhenUserConnectsToSshMachine() throws OperationException {
        when(machineStateEvent.getMachine()).thenReturn(machineDto);

        assertThat(pushToDeviceManager.isSshDeviceExist(), is(false));

        pushToDeviceManager.onMachineRunning(machineStateEvent);

        assertThat(pushToDeviceManager.isSshDeviceExist(), is(true));
    }

    @Test
    public void deviceActionShouldBeRemovedToDeviceActionGroupWhenUserConnectsToSshMachine() throws OperationException {
        when(machineStateEvent.getMachine()).thenReturn(machineDto);

        verify(machinesPromise).then(machinesListCaptor.capture());
        machinesListCaptor.getValue().apply(Arrays.asList(machineDto));

        assertThat(pushToDeviceManager.isSshDeviceExist(), is(true));

        pushToDeviceManager.onMachineDestroyed(machineStateEvent);

        assertThat(pushToDeviceManager.isSshDeviceExist(), is(false));
    }
}
