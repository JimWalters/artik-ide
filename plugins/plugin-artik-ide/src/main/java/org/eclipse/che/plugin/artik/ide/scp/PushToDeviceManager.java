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

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import org.eclipse.che.api.core.model.machine.Machine;
import org.eclipse.che.api.core.model.machine.MachineConfig;
import org.eclipse.che.api.machine.gwt.client.MachineServiceClient;
import org.eclipse.che.api.machine.shared.dto.MachineDto;
import org.eclipse.che.api.promises.client.Operation;
import org.eclipse.che.api.promises.client.OperationException;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.api.promises.client.PromiseError;
import org.eclipse.che.ide.api.action.Action;
import org.eclipse.che.ide.api.action.ActionManager;
import org.eclipse.che.ide.api.action.DefaultActionGroup;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.constraints.Constraints;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.extension.machine.client.machine.MachineStateEvent;
import org.eclipse.che.ide.util.loging.Log;
import org.eclipse.che.plugin.artik.ide.ArtikLocalizationConstants;
import org.eclipse.che.plugin.artik.ide.scp.action.ChooseTargetAction;
import org.eclipse.che.plugin.artik.ide.scp.action.PushToDeviceAction;
import org.eclipse.che.plugin.artik.ide.scp.action.PushToDeviceActionFactory;
import org.eclipse.che.plugin.artik.ide.scp.service.PushToDeviceServiceClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.eclipse.che.api.core.model.machine.MachineStatus.RUNNING;
import static org.eclipse.che.ide.api.notification.StatusNotification.Status.FAIL;
import static org.eclipse.che.ide.api.notification.StatusNotification.Status.SUCCESS;

/**
 * The class which contains business logic to work with secure copy. The business logic reacts on connecting and disconnecting to
 * ssh machine and adds or removes actions in 'Push To Device' action group. Also manager stores all started ssh machines.
 *
 * @author Dmitry Shnurenko
 */
@Singleton
public class PushToDeviceManager implements MachineStateEvent.Handler {
    private final Map<String, String>          sshMachines;
    private final PushToDeviceServiceClient    scpService;
    private final NotificationManager          notificationManager;
    private final ArtikLocalizationConstants   locale;
    private final ActionManager                actionManager;
    private final PushToDeviceActionFactory    pushToDeviceActionFactory;
    private final DefaultActionGroup           pushToDeviceGroup;
    private final MachineServiceClient         machineService;
    private final AppContext                   appContext;
    private final Provider<ChooseTargetAction> chooseTargetActionProvider;

    @Inject
    public PushToDeviceManager(PushToDeviceServiceClient scpService,
                               NotificationManager notificationManager,
                               ArtikLocalizationConstants locale,
                               MachineServiceClient machineService,
                               AppContext appContext,
                               EventBus eventBus,
                               ActionManager actionManager,
                               PushToDeviceActionFactory pushToDeviceActionFactory,
                               Provider<ChooseTargetAction> chooseTargetActionProvider) {
        this.sshMachines = new HashMap<>();
        this.scpService = scpService;
        this.notificationManager = notificationManager;
        this.locale = locale;
        this.actionManager = actionManager;
        this.pushToDeviceActionFactory = pushToDeviceActionFactory;
        this.machineService = machineService;
        this.appContext = appContext;
        this.chooseTargetActionProvider = chooseTargetActionProvider;
        this.pushToDeviceGroup = new DefaultActionGroup("Push To Device", true, actionManager);

        eventBus.addHandler(MachineStateEvent.TYPE, this);
    }

    /** The method fetches all ssh machines from workspace converts them to actions and adds to push to device action group. */
    public void fetchSshMachines() {
        DefaultActionGroup resourceOperationGroup = (DefaultActionGroup)actionManager.getAction("resourceOperation");
        resourceOperationGroup.add(pushToDeviceGroup);

        Promise<List<MachineDto>> machines = machineService.getMachines(appContext.getWorkspaceId());
        machines.then(new Operation<List<MachineDto>>() {
            @Override
            public void apply(List<MachineDto> machines) throws OperationException {
                for (Machine machine : machines) {
                    MachineConfig config = machine.getConfig();
                    if (isArtikOrSsh(machine) && RUNNING.equals(machine.getStatus())) {
                        String machineId = machine.getId();
                        String machineName = config.getName();

                        sshMachines.put(machineName, machineId);

                        addPushToDeviceAction(machineName, machineId);
                    }
                }
                pushToDeviceGroup.addSeparator();
                pushToDeviceGroup.add(chooseTargetActionProvider.get(), Constraints.LAST);
            }
        }).catchError(new Operation<PromiseError>() {
            @Override
            public void apply(PromiseError error) throws OperationException {
                Log.error(getClass(), error.getMessage());
            }
        });
    }

    /**
     * The method send request to service to do secure copy file or folder to ssh machine.
     *
     * @param machineName
     *         name of machine to which file will be copied
     * @param sourcePath
     *         path to file or folder which will be copied
     * @param targetPath
     *         destination path where file or folder will be copied
     */
    public void pushToDevice(String machineName, String sourcePath, final String targetPath) {
        String machineId = sshMachines.get(machineName);

        final String fileName = getFileName(sourcePath);

        Promise<Void> promise = scpService.pushToDevice(machineId, sourcePath, targetPath);
        promise.then(new Operation<Void>() {
            @Override
            public void apply(Void arg) throws OperationException {
                notificationManager.notify(locale.pushToDeviceSuccess(fileName, targetPath), SUCCESS, true);
            }
        }).catchError(new Operation<PromiseError>() {
            @Override
            public void apply(PromiseError error) throws OperationException {
                notificationManager.notify(locale.pushToDeviceFail(fileName, targetPath), FAIL, true);
            }
        });
    }

    /** Returns <code>true</code> if ssh machine exists, <code>false</code> otherwise. */
    public boolean isSshDeviceExist() {
        return !sshMachines.isEmpty();
    }

    /** Returns set of ssh machines' names. */
    public Set<String> getMachineNames() {
        return sshMachines.keySet();
    }

    private String getFileName(String sourcePath) {
        if (Strings.isNullOrEmpty(sourcePath)) {
            throw new IllegalArgumentException("Source path can not be null");
        }

        return sourcePath.substring(sourcePath.lastIndexOf("/") + 1);
    }

    @Override
    public void onMachineCreating(MachineStateEvent event) {
    }

    @Override
    public void onMachineRunning(MachineStateEvent event) {
        Machine machine = event.getMachine();
        if (!isArtikOrSsh(machine)) {
            return;
        }

        String machineName = machine.getConfig().getName();
        String machineId = machine.getId();
        sshMachines.put(machineName, machineId);

        addPushToDeviceAction(machineName, machineId);
    }

    private boolean isArtikOrSsh(Machine machine) {
        String type = machine.getConfig().getType();
        return "ssh".equals(type) || "artik".equals(type);
    }

    private void addPushToDeviceAction(String machineName, String machineId) {
        PushToDeviceAction pushToDeviceAction = pushToDeviceActionFactory.create(machineName);
        actionManager.registerAction(machineId, pushToDeviceAction);

        pushToDeviceGroup.add(pushToDeviceAction, Constraints.FIRST);
    }

    @Override
    public void onMachineDestroyed(MachineStateEvent event) {
        Machine machine = event.getMachine();
        if (!isArtikOrSsh(machine)) {
            return;
        }

        sshMachines.remove(machine.getConfig().getName());

        Action action = actionManager.getAction(machine.getId());
        pushToDeviceGroup.remove(action);
    }
}
