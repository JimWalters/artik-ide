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

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.Timer;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

import org.eclipse.che.api.machine.gwt.client.MachineServiceClient;
import org.eclipse.che.api.machine.gwt.client.RecipeServiceClient;
import org.eclipse.che.api.machine.gwt.client.events.WsAgentStateEvent;
import org.eclipse.che.api.machine.gwt.client.events.WsAgentStateHandler;
import org.eclipse.che.api.machine.shared.dto.LimitsDto;
import org.eclipse.che.api.machine.shared.dto.MachineConfigDto;
import org.eclipse.che.api.machine.shared.dto.MachineDto;
import org.eclipse.che.api.machine.shared.dto.MachineSourceDto;
import org.eclipse.che.api.machine.shared.dto.recipe.NewRecipe;
import org.eclipse.che.api.machine.shared.dto.recipe.RecipeDescriptor;
import org.eclipse.che.api.machine.shared.dto.recipe.RecipeUpdate;
import org.eclipse.che.api.promises.client.Operation;
import org.eclipse.che.api.promises.client.OperationException;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.api.promises.client.PromiseError;
import org.eclipse.che.api.workspace.gwt.client.WorkspaceServiceClient;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.api.notification.StatusNotification;
import org.eclipse.che.ide.collections.Jso;
import org.eclipse.che.ide.dto.DtoFactory;
import org.eclipse.che.ide.extension.machine.client.machine.MachineStateEvent;
import org.eclipse.che.ide.ui.dialogs.CancelCallback;
import org.eclipse.che.ide.ui.dialogs.ConfirmCallback;
import org.eclipse.che.ide.ui.dialogs.DialogFactory;
import org.eclipse.che.ide.util.StringUtils;
import org.eclipse.che.ide.util.loging.Log;
import org.eclipse.che.plugin.artik.ide.ArtikLocalizationConstant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.eclipse.che.api.core.model.machine.MachineStatus.RUNNING;

/**
 * Presenter for managing Artik devices.
 *
 * @author Vitaliy Guliy
 * @author Ann Shumilova
 */
public class ManageDevicesPresenter implements ManageDevicesView.ActionDelegate, WsAgentStateHandler {
    public final static String ARTIK_CATEGORY = "artik";
    public final static String SSH_CATEGORY   = "ssh-config";

    private final ManageDevicesView         view;
    private final RecipeServiceClient       recipeServiceClient;
    private final DtoFactory                dtoFactory;
    private final DialogFactory             dialogFactory;
    private final NotificationManager       notificationManager;
    private final ArtikLocalizationConstant locale;
    private final AppContext                appContext;
    private final MachineServiceClient      machineService;
    private final WorkspaceServiceClient    workspaceServiceClient;
    private final EventBus                  eventBus;

    private final List<Device> devices = new ArrayList<>();
    private Device selectedDevice;
    private final Map<String, MachineDto> machinesByNameMap = new HashMap<>();

    private StatusNotification connectNotification;

    @Inject
    public ManageDevicesPresenter(final ManageDevicesView view,
                                  final RecipeServiceClient recipeServiceClient,
                                  final DtoFactory dtoFactory,
                                  final DialogFactory dialogFactory,
                                  final NotificationManager notificationManager,
                                  final ArtikLocalizationConstant locale,
                                  final AppContext appContext,
                                  final MachineServiceClient machineService,
                                  final WorkspaceServiceClient workspaceServiceClient,
                                  final EventBus eventBus) {
        this.view = view;
        this.recipeServiceClient = recipeServiceClient;
        this.dtoFactory = dtoFactory;
        this.dialogFactory = dialogFactory;
        this.notificationManager = notificationManager;
        this.locale = locale;
        this.appContext = appContext;
        this.machineService = machineService;
        this.workspaceServiceClient = workspaceServiceClient;
        this.eventBus = eventBus;
        eventBus.addHandler(WsAgentStateEvent.TYPE, this);

        view.setDelegate(this);
    }

    /**
     * Opens Manage devices popup.
     */
    public void edit() {
        view.show();
        view.clear();

        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                updateDevices(null);
            }
        });
    }

    /**
     * Fetches all recipes from the server, makes a list of devices and selects specified device.
     */
    private void updateDevices(final String deviceToSelect) {
        devices.clear();
        machinesByNameMap.clear();

        machineService.getMachines(appContext.getWorkspaceId()).then(new Operation<List<MachineDto>>() {
            @Override
            public void apply(List<MachineDto> machineList) throws OperationException {
                for (MachineDto machine : machineList) {
                    machinesByNameMap.put(machine.getConfig().getName(), machine);
                }

                recipeServiceClient.getAllRecipes().then(new Operation<List<RecipeDescriptor>>() {
                    @Override
                    public void apply(List<RecipeDescriptor> recipeList) throws OperationException {
                        for (RecipeDescriptor recipe : recipeList) {
                            // Filter recipe by type SSH_CATEGORY and tag - ARTIK_CATEGORY
                            if (!(SSH_CATEGORY.equalsIgnoreCase(recipe.getType()) && recipe.getTags().contains(ARTIK_CATEGORY))) {
                                continue;
                            }

                            MachineDto machine = machinesByNameMap.get(recipe.getName());

                            final Device device = new Device(recipe.getName(), ARTIK_CATEGORY, recipe);
                            device.setRecipe(recipe);
                            devices.add(device);

                            device.setConnected(isMachineRunning(machine));

                            restoreDevice(device);
                        }

                        view.showDevices(devices);


                        if (deviceToSelect == null && !devices.isEmpty()) {
                            view.selectDevice(devices.get(0));
                            return;
                        }

                        for (Device device : devices) {
                            if (device.getName().equals(deviceToSelect)) {
                                view.selectDevice(device);
                                break;
                            }
                        }
                    }
                });

            }
        });
    }

    /**
     * Determines whether machine is running or not.
     *
     * @return
     *      true for running machine
     */
    private boolean isMachineRunning(MachineDto machine) {
        return machine != null && machine.getStatus() == RUNNING;
    }

    /**
     * Restore device properties from its recipe.
     *
     * @param device
     *          device to restore
     */
    private void restoreDevice(Device device) {
        if (device.getRecipe() == null || !device.getRecipe().getType().equalsIgnoreCase(SSH_CATEGORY)) {
            return;
        }

        try {
            JSONObject json = JSONParser.parseStrict(device.getRecipe().getScript()).isObject();

            if (json.get("host") != null) {
                String host = json.get("host").isString().stringValue();
                device.setHost(host);
            }

            if (json.get("port") != null) {
                String port = json.get("port").isString().stringValue();
                device.setPort(port);
            }

            if (json.get("username") != null) {
                String username = json.get("username").isString().stringValue();
                device.setUserName(username);
            }

            if (json.get("password") != null) {
                String password = json.get("password").isString().stringValue();
                device.setPassword(password);
            }

        } catch (Exception e) {
            Log.error(ManageDevicesPresenter.class, "Unable to parse recipe JSON. " + e.getMessage());
        }
    }

    @Override
    public void onCloseClicked() {
        view.hide();
    }

    @Override
    public void onAddDevice(String category) {
        Device device = new Device("artik_device", ARTIK_CATEGORY);
        device.setHost("");
        device.setPort("22");
        device.setUserName("root");
        device.setPassword("");
        device.setDirty(true);
        device.setConnected(false);
        devices.add(device);

        view.showDevices(devices);
        view.selectDevice(device);
    }

    @Override
    public void onDeviceSelected(Device device) {
        if (device == null) {
            view.showHintPanel();
            return;
        }

        view.showPropertiesPanel();
        view.setDeviceName(device.getName());

        view.setHost(device.getHost());
        view.setPort(device.getPort());
        view.setUserName(device.getUserName());
        view.setPassword(device.getPassword());

        view.selectDeviceName();

        selectedDevice = device;
        updateButtons();
    }

    @Override
    public void onDeviceNameChanged(String value) {
        if (selectedDevice.getName().equals(value)) {
            return;
        }

        selectedDevice.setName(value);
        selectedDevice.setDirty(true);
        updateButtons();
    }

    @Override
    public void onHostChanged(String value) {
        if (selectedDevice.getHost().equals(value)) {
            return;
        }

        selectedDevice.setHost(value);
        selectedDevice.setDirty(true);
        updateButtons();
    }

    @Override
    public void onPortChanged(String value) {
        if (selectedDevice.getPort().equals(value)) {
            return;
        }

        selectedDevice.setPort(value);
        selectedDevice.setDirty(true);
        updateButtons();
    }

    @Override
    public void onUserNameChanged(String value) {
        if (selectedDevice.getUserName().equals(value)) {
            return;
        }

        selectedDevice.setUserName(value);
        selectedDevice.setDirty(true);
        updateButtons();
    }

    @Override
    public void onPasswordChanged(String value) {
        if (selectedDevice.getPassword().equals(value)) {
            return;
        }

        selectedDevice.setPassword(value);
        selectedDevice.setDirty(true);
        updateButtons();
    }

    /**
     * Updates buttons state.
     */
    private void updateButtons() {
        if (selectedDevice == null) {
            return;
        }

        view.enableConnectButton(!selectedDevice.isDirty());

        view.setConnectButtonText(selectedDevice.isConnected() ? "Disconnect" : "Connect");

        view.enableCancelButton(selectedDevice.isDirty());

        if (StringUtils.isNullOrEmpty(view.getDeviceName()) ||
                StringUtils.isNullOrEmpty(view.getHost()) ||
                StringUtils.isNullOrEmpty(view.getPort())) {
            view.enableSaveButton(false);
        } else {
            view.enableSaveButton(selectedDevice.isDirty());
        }
    }

    @Override
    public void onSaveClicked() {
        // Save only SSH type
        if (!ARTIK_CATEGORY.equals(selectedDevice.getType())) {
            return;
        }

        if (selectedDevice.getRecipe() == null) {
            createDevice();
        } else {
            updateDevice();
        }
    }

    /**
     * Creates a new device connection.
     */
    private void createDevice() {
        List<String> tags = new ArrayList<>();
        tags.add(ARTIK_CATEGORY);

        Jso content = Jso.create();
        content.addField("host", selectedDevice.getHost());
        content.addField("port", selectedDevice.getPort());
        content.addField("username", selectedDevice.getUserName());
        content.addField("password", selectedDevice.getPassword());

        NewRecipe newRecipe = dtoFactory.createDto(NewRecipe.class)
                .withName(selectedDevice.getName())
                .withType(SSH_CATEGORY)
                .withScript(content.serialize())
                .withTags(tags);

        Promise<RecipeDescriptor> createRecipe = recipeServiceClient.createRecipe(newRecipe);
        createRecipe.then(new Operation<RecipeDescriptor>() {
            @Override
            public void apply(RecipeDescriptor recipe) throws OperationException {
                onDeviceSaved(recipe);
            }
        });

        createRecipe.catchError(new Operation<PromiseError>() {
            @Override
            public void apply(PromiseError arg) throws OperationException {
               dialogFactory.createMessageDialog("Error", locale.deviceSaveError(), null).show();
            }
        });
    }

    /**
     * Updates as existent device.
     */
    private void updateDevice() {
        Jso content = Jso.create();
        content.addField("host", selectedDevice.getHost());
        content.addField("port", selectedDevice.getPort());
        content.addField("username", selectedDevice.getUserName());
        content.addField("password", selectedDevice.getPassword());

        RecipeUpdate recipeUpdate = dtoFactory.createDto(RecipeUpdate.class)
                .withId(selectedDevice.getRecipe().getId())
                .withName(view.getDeviceName())
                .withType(selectedDevice.getRecipe().getType())
                .withTags(selectedDevice.getRecipe().getTags())
                .withDescription(selectedDevice.getRecipe().getDescription())
                .withPermissions(selectedDevice.getRecipe().getPermissions())
                .withScript(content.serialize());

        Promise<RecipeDescriptor> updateRecipe = recipeServiceClient.updateRecipe(recipeUpdate);
        updateRecipe.then(new Operation<RecipeDescriptor>() {
            @Override
            public void apply(RecipeDescriptor recipe) throws OperationException {
                onDeviceSaved(recipe);
            }
        });

        updateRecipe.catchError(new Operation<PromiseError>() {
            @Override
            public void apply(PromiseError arg) throws OperationException {
                dialogFactory.createMessageDialog("Error", locale.deviceSaveError(), null).show();
            }
        });
    }

    /**
     * Performs actions when device is saved.
     */
    private void onDeviceSaved(RecipeDescriptor recipe) {
        selectedDevice.setRecipe(recipe);
        selectedDevice.setDirty(false);

        view.showDevices(devices);
        view.selectDevice(selectedDevice);

        //updateButtons();
        notificationManager.notify(locale.deviceSaveSuccess(), StatusNotification.Status.SUCCESS, true);
    }

    @Override
    public void onCancelClicked() {
        if (selectedDevice.getRecipe() == null) {
            devices.remove(selectedDevice);
            view.showDevices(devices);

            view.selectDevice(null);
            view.showHintPanel();

            return;
        }

        selectedDevice.setName(selectedDevice.getRecipe().getName());
        restoreDevice(selectedDevice);
        selectedDevice.setDirty(false);
        view.selectDevice(selectedDevice);
    }

    @Override
    public void onConnectClicked() {
        if (selectedDevice == null || selectedDevice.getRecipe() == null) {
            return;
        }

        if (selectedDevice.isConnected()) {
            disconnect();
        } else {
            connect();
        }
    }

    /**
     * Opens a connection to the selected device.
     * Starts a machine based on the selected recipe.
     */
    private void connect() {
        view.setConnectButtonText(null);

        connectNotification = notificationManager.notify(locale.deviceConnectProgress(selectedDevice.getName()), StatusNotification.Status.PROGRESS, true);

        String recipeURL = selectedDevice.getRecipe().getLink("get recipe script").getHref();

        LimitsDto limitsDto = dtoFactory.createDto(LimitsDto.class).withRam(1024);
        MachineSourceDto sourceDto = dtoFactory.createDto(MachineSourceDto.class).withType("ssh-config").withLocation(recipeURL);

        MachineConfigDto configDto = dtoFactory.createDto(MachineConfigDto.class)
                .withDev(false)
                .withName(selectedDevice.getName())
                .withSource(sourceDto)
                .withLimits(limitsDto)
                .withType(ARTIK_CATEGORY);

        Promise<MachineDto> machinePromise = workspaceServiceClient.createMachine(appContext.getWorkspace().getId(), configDto);

        machinePromise.then(new Operation<MachineDto>() {
            @Override
            public void apply(final MachineDto machineDto) throws OperationException {
                eventBus.fireEvent(new MachineStateEvent(machineDto, MachineStateEvent.MachineAction.CREATING));
                ensureMachineIsStarted(machineDto.getId());
            }
        });

        machinePromise.catchError(new Operation<PromiseError>() {
            @Override
            public void apply(PromiseError promiseError) throws OperationException {
                onConnectingFailed();
            }
        });
    }

    /**
     * Ensures machine is started.
     */
    private void ensureMachineIsStarted(final String machineId) {
        machineService.getMachine(machineId).then(new Operation<MachineDto>() {
            @Override
            public void apply(MachineDto machineDto) throws OperationException {
                if (machineDto.getStatus() == RUNNING) {
                    eventBus.fireEvent(new MachineStateEvent(machineDto, MachineStateEvent.MachineAction.RUNNING));
                    onConnected();
                } else {
                    new Timer() {
                        @Override
                        public void run() {
                            ensureMachineIsStarted(machineId);
                        }
                    }.schedule(1000);
                }
            }
        }).catchError(new Operation<PromiseError>() {
            @Override
            public void apply(PromiseError arg) throws OperationException {
                onConnectingFailed();
            }
        });
    }

    /**
     * Displays a notification
     */
    private void onConnected() {
        connectNotification.setTitle(locale.deviceConnectSuccess(selectedDevice.getName()));
        connectNotification.setStatus(StatusNotification.Status.SUCCESS);
        updateDevices(selectedDevice.getName());
    }

    private void onConnectingFailed() {
        connectNotification.setTitle(locale.deviceConnectError(selectedDevice.getName()));
        connectNotification.setStatus(StatusNotification.Status.FAIL);

        view.selectDevice(selectedDevice);
    }

    /**
     * Closes the connection to the selected device.
     * Destroys a machine based on the selected recipe.
     */
    private void disconnect() {
        if (selectedDevice == null || !selectedDevice.isConnected()) {
            return;
        }
        final MachineDto machine = machinesByNameMap.get(selectedDevice.getName());
        disconnect(machine);
    }

    /**
     * Destroys the machine.
     *
     * @param machine
     *          machine to destroy
     */
    private void disconnect(final MachineDto machine) {
        if (machine == null || machine.getStatus() != RUNNING) {
            return;
        }
        view.setConnectButtonText(null);

        machineService.destroyMachine(machine.getId()).then(new Operation<Void>() {
            @Override
            public void apply(Void arg) throws OperationException {
                eventBus.fireEvent(new MachineStateEvent(machine, MachineStateEvent.MachineAction.DESTROYED));
                notificationManager.notify(locale.deviceDisconnectSuccess(selectedDevice.getName()), StatusNotification.Status.SUCCESS, true);
                updateDevices(selectedDevice.getName());
            }
        }).catchError(new Operation<PromiseError>() {
            @Override
            public void apply(PromiseError arg) throws OperationException {
                notificationManager.notify(locale.deviceDisconnectError(selectedDevice.getName()), StatusNotification.Status.FAIL, true);
                updateDevices(selectedDevice.getName());
            }
        });
    }

    @Override
    public void onDeleteDevice(final Device device) {
        dialogFactory.createConfirmDialog("IDE", locale.deviceDeleteConfirm(device.getName()),
                new ConfirmCallback() {
                    @Override
                    public void accepted() {
                        if (device.isConnected()) {
                            disconnectAndDelete(device);
                        } else {
                            deleteDevice(device);
                        }
                    }
                }, new CancelCallback() {
                    @Override
                    public void cancelled() {
                    }
                }).show();
    }

    private void disconnectAndDelete(final Device device) {
        final MachineDto machine = machinesByNameMap.get(device.getName());
        if (machine == null || machine.getStatus() != RUNNING) {
            return;
        }

        machineService.destroyMachine(machine.getId()).then(new Operation<Void>() {
            @Override
            public void apply(Void arg) throws OperationException {
                notificationManager.notify(locale.deviceDisconnectSuccess(device.getName()), StatusNotification.Status.SUCCESS, true);
                new Timer() {
                    @Override
                    public void run() {
                        deleteDevice(device);
                    }
                }.schedule(1000);
            }
        }).catchError(new Operation<PromiseError>() {
            @Override
            public void apply(PromiseError arg) throws OperationException {
                notificationManager.notify(locale.deviceDisconnectError(device.getName()), StatusNotification.Status.FAIL, true);
                updateDevices(device.getName());
            }
        });
    }

    /**
     * Deletes specified  device.
     *
     * @param device
     *          device to delete
     */
    private void deleteDevice(final Device device) {
        Promise<Void> deletePromice = recipeServiceClient.removeRecipe(device.getRecipe().getId());
        deletePromice.then(new Operation<Void>() {
            @Override
            public void apply(Void arg) throws OperationException {
                devices.remove(device);
                view.showDevices(devices);

                view.selectDevice(null);
                view.showHintPanel();

                notificationManager.notify(locale.deviceDeleteSuccess(device.getName()), StatusNotification.Status.SUCCESS, true);
            }
        });

        deletePromice.catchError(new Operation<PromiseError>() {
            @Override
            public void apply(PromiseError arg) throws OperationException {
            dialogFactory.createMessageDialog("Error", locale.deviceDeleteError(device.getName()), null).show();
            }
        });

    }

    @Override
    public void onWsAgentStarted(WsAgentStateEvent event) {
        edit();
    }

    @Override
    public void onWsAgentStopped(WsAgentStateEvent event) {
        //do nothing
    }
}
