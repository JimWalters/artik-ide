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
import org.eclipse.che.api.machine.shared.dto.event.MachineStatusEvent;
import org.eclipse.che.api.machine.shared.dto.recipe.NewRecipe;
import org.eclipse.che.api.machine.shared.dto.recipe.RecipeDescriptor;
import org.eclipse.che.api.machine.shared.dto.recipe.RecipeUpdate;
import org.eclipse.che.api.promises.client.Operation;
import org.eclipse.che.api.promises.client.OperationException;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.api.promises.client.PromiseError;
import org.eclipse.che.api.workspace.gwt.client.WorkspaceServiceClient;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.dialogs.CancelCallback;
import org.eclipse.che.ide.api.dialogs.ConfirmCallback;
import org.eclipse.che.ide.api.dialogs.DialogFactory;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.api.notification.StatusNotification;
import org.eclipse.che.ide.collections.Jso;
import org.eclipse.che.ide.dto.DtoFactory;
import org.eclipse.che.ide.extension.machine.client.machine.MachineStateEvent;
import org.eclipse.che.ide.rest.DtoUnmarshallerFactory;
import org.eclipse.che.ide.util.StringUtils;
import org.eclipse.che.ide.util.loging.Log;
import org.eclipse.che.ide.websocket.MessageBusProvider;
import org.eclipse.che.ide.websocket.rest.SubscriptionHandler;
import org.eclipse.che.plugin.artik.ide.ArtikLocalizationConstant;
import org.eclipse.che.plugin.artik.ide.discovery.DeviceDiscoveryServiceClient;
import org.eclipse.che.plugin.artik.shared.dto.ArtikDeviceDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.eclipse.che.api.core.model.machine.MachineStatus.CREATING;
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
    public final static String DEFAULT_NAME   = "artik_device";
    public final static String VALID_NAME     = "[\\w-]*";

    private final ManageDevicesView            view;
    private final RecipeServiceClient          recipeServiceClient;
    private final DtoFactory                   dtoFactory;
    private final DtoUnmarshallerFactory       dtoUnmarshallerFactory;
    private final DialogFactory                dialogFactory;
    private final NotificationManager          notificationManager;
    private final ArtikLocalizationConstant    locale;
    private final AppContext                   appContext;
    private final MachineServiceClient         machineService;
    private final WorkspaceServiceClient       workspaceServiceClient;
    private final DeviceDiscoveryServiceClient deviceDiscoveryService;
    private final EventBus                     eventBus;
    private final MessageBusProvider           messageBusProvider;

    private final List<Device> devices = new ArrayList<>();
    private Device selectedDevice;
    private final Map<String, MachineDto> machines = new HashMap<>();

    private StatusNotification connectNotification;

    private Map<String, SubscriptionHandler<MachineStatusEvent>> subscriptions = new HashMap<>();

    @Inject
    public ManageDevicesPresenter(final ManageDevicesView view,
                                  final RecipeServiceClient recipeServiceClient,
                                  final DtoFactory dtoFactory,
                                  final DtoUnmarshallerFactory dtoUnmarshallerFactory,
                                  final DialogFactory dialogFactory,
                                  final NotificationManager notificationManager,
                                  final ArtikLocalizationConstant locale,
                                  final AppContext appContext,
                                  final MachineServiceClient machineService,
                                  final WorkspaceServiceClient workspaceServiceClient,
                                  final DeviceDiscoveryServiceClient deviceDiscoveryService,
                                  final EventBus eventBus,
                                  final MessageBusProvider messageBusProvider) {
        this.view = view;
        this.recipeServiceClient = recipeServiceClient;
        this.dtoFactory = dtoFactory;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.dialogFactory = dialogFactory;
        this.notificationManager = notificationManager;
        this.locale = locale;
        this.appContext = appContext;
        this.machineService = machineService;
        this.workspaceServiceClient = workspaceServiceClient;
        this.deviceDiscoveryService = deviceDiscoveryService;
        this.eventBus = eventBus;
        this.messageBusProvider = messageBusProvider;

        eventBus.addHandler(WsAgentStateEvent.TYPE, this);

        view.setDelegate(this);
    }

    /**
     * Opens Manage devices popup.
     */
    public void edit() {
        view.show();
        view.clear();

        discoverDevices();

        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                updateDevices(null);
            }
        });
    }

    /**
     * Discovers connected devices.
     */
    private void discoverDevices() {
        Promise<List<ArtikDeviceDto>> promise = deviceDiscoveryService.getDevices();

        promise.then(new Operation<List<ArtikDeviceDto>>() {
            @Override
            public void apply(List<ArtikDeviceDto> devices) throws OperationException {
                List<String> hosts = new ArrayList<String>();
                for (ArtikDeviceDto device : devices) {
                    hosts.add(device.getIPAddress());
                }

                view.setHosts(hosts);
            }
        });

        promise.catchError(new Operation<PromiseError>() {
            @Override
            public void apply(PromiseError e) throws OperationException {
                view.setHosts(null);
                Log.error(ManageDevicesPresenter.class, "Failed to discover devices. " + e.getMessage());
            }
        });
    }

    /**
     * Fetches all recipes from the server, makes a list of devices and selects specified device.
     */
    private void updateDevices(final String deviceToSelect) {
        devices.clear();
        machines.clear();

        machineService.getMachines(appContext.getWorkspaceId()).then(new Operation<List<MachineDto>>() {
            @Override
            public void apply(List<MachineDto> machineList) throws OperationException {
                for (MachineDto machine : machineList) {
                    machines.put(machine.getConfig().getName(), machine);
                }

                recipeServiceClient.getAllRecipes().then(new Operation<List<RecipeDescriptor>>() {
                    @Override
                    public void apply(List<RecipeDescriptor> recipeList) throws OperationException {
                        for (RecipeDescriptor recipe : recipeList) {
                            // Filter recipe by type SSH_CATEGORY and tag - ARTIK_CATEGORY
                            if (!(SSH_CATEGORY.equalsIgnoreCase(recipe.getType()) && recipe.getTags().contains(ARTIK_CATEGORY))) {
                                continue;
                            }

                            MachineDto machine = machines.get(recipe.getName());

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
        this.selectedDevice = null;
        String deviceName = generateDeviceName();
        Device device = new Device(deviceName, ARTIK_CATEGORY);
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

    /**
     *  Generates device name base on existing ones.
     *
     * @return generated name
     */
    private String generateDeviceName() {
        int i = 1;
        while (isDeviceNameExists(DEFAULT_NAME + "_" + i)) {
            i++;
        }
        return DEFAULT_NAME + "_" + i;
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

        view.setConnectButtonText(selectedDevice.isConnected() ? "Disconnect" : "Connect");

        view.enableCancelButton(selectedDevice.isDirty());

        view.enableEditing(!selectedDevice.isConnected());

        if (selectedDevice.isConnected()) {
            view.enableConnectButton(true);
            return;
        }

        boolean deviceAlreadyExists = isDeviceNameExists(view.getDeviceName());
        boolean deviceNameValid = !StringUtils.isNullOrEmpty(view.getDeviceName()) && view.getDeviceName().matches(VALID_NAME);

        boolean isNotValid = !deviceNameValid ||
                          StringUtils.isNullOrEmpty(view.getHost()) ||
                          StringUtils.isNullOrEmpty(view.getPort()) || deviceAlreadyExists;
        view.enableConnectButton(!isNotValid);

        // check host is not empty
        if (view.getHost().isEmpty()) {
            view.markHostInvalid();
        } else {
            view.unmarkHost();
        }

        // check port is not empty
        if (view.getPort().isEmpty()) {
            view.markPortInvalid();
        } else {
            view.unmarkPort();
        }

        // check device name is not empty and doesn't exist
        if (!deviceNameValid || deviceAlreadyExists) {
            view.markDeviceNameInvalid();
        } else {
            view.unmarkDeviceName();
        }
    }

    /**
     * Checks device name on existence.
     *
     * @param deviceName name of the device to check on existence
     * @return boolean <code>true</code> id name already exists
     */
    private boolean isDeviceNameExists(String deviceName) {
        for (Device device : devices) {
            if (device != selectedDevice && device.getName().equals(deviceName)) {
                return true;
            }
        }
        return false;
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

        connect();
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
        if (selectedDevice == null) {
            return;
        }

        if (selectedDevice.isConnected()) {
            disconnect();
        } else {
            saveDeviceChanges();
        }
    }

    private void saveDeviceChanges() {
        // Save only Artik type
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
     * Opens a connection to the selected device.
     * Starts a machine based on the selected recipe.
     */
    private void connect() {
        subscribeToMachineChannel(selectedDevice.getName());

        view.setConnectButtonText(null);

        connectNotification = notificationManager.notify(locale.deviceConnectProgress(selectedDevice.getName()), StatusNotification.Status.PROGRESS,
                                                         StatusNotification.DisplayMode.FLOAT_MODE);

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
            }
        });

        machinePromise.catchError(new Operation<PromiseError>() {
            @Override
            public void apply(PromiseError promiseError) throws OperationException {
                unsubscribeFromMachineChannel(selectedDevice.getName());
                onConnectingFailed(null);
            }
        });
    }

    /**
     * Closes the connection to the selected device.
     * Destroys a machine based on the selected recipe.
     */
    private void disconnect() {
        if (selectedDevice == null || !selectedDevice.isConnected()) {
            return;
        }
        MachineDto machine = machines.get(selectedDevice.getName());
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
                notificationManager.notify(locale.deviceDisconnectSuccess(selectedDevice.getName()), StatusNotification.Status.SUCCESS,
                                           StatusNotification.DisplayMode.FLOAT_MODE);
                updateDevices(selectedDevice.getName());
            }
        }).catchError(new Operation<PromiseError>() {
            @Override
            public void apply(PromiseError arg) throws OperationException {
                notificationManager.notify(locale.deviceDisconnectError(selectedDevice.getName()), StatusNotification.Status.FAIL,
                                           StatusNotification.DisplayMode.FLOAT_MODE);
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
        final MachineDto machine = machines.get(device.getName());
        if (machine == null || machine.getStatus() != RUNNING) {
            return;
        }

        machineService.destroyMachine(machine.getId()).then(new Operation<Void>() {
            @Override
            public void apply(Void arg) throws OperationException {
                notificationManager.notify(locale.deviceDisconnectSuccess(device.getName()), StatusNotification.Status.SUCCESS,
                                           StatusNotification.DisplayMode.FLOAT_MODE);
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
                notificationManager.notify(locale.deviceDisconnectError(device.getName()), StatusNotification.Status.FAIL,
                                           StatusNotification.DisplayMode.FLOAT_MODE);
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

                notificationManager.notify(locale.deviceDeleteSuccess(device.getName()), StatusNotification.Status.SUCCESS,
                                           StatusNotification.DisplayMode.FLOAT_MODE);
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
       checkArtikMachineExists();
    }

    private void checkArtikMachineExists() {
        recipeServiceClient.getAllRecipes().then(new Operation<List<RecipeDescriptor>>() {
            @Override
            public void apply(List<RecipeDescriptor> recipeList) throws OperationException {
                boolean noArtikMachine = true;

                for (RecipeDescriptor recipe : recipeList) {
                    // Filter recipe by type SSH_CATEGORY and tag - ARTIK_CATEGORY
                    if ((SSH_CATEGORY.equalsIgnoreCase(recipe.getType()) && recipe.getTags().contains(ARTIK_CATEGORY))) {
                        noArtikMachine = false;
                        break;
                    }
                }

                if (noArtikMachine) {
                    edit();
                }
            }
        });
    }

    @Override
    public void onWsAgentStopped(WsAgentStateEvent event) {
        //do nothing
    }

    /**
     * Subscribes to the websocket channel and starts listening machine status events.
     *
     * @param machineName
     *          mane of the machine to subscribe
     */
    private void subscribeToMachineChannel(final String machineName) {
        String channel = "machine:status:" + appContext.getWorkspace().getId() + ':' + machineName;

        if (subscriptions.containsKey(channel)) {
            return;
        }

        SubscriptionHandler<MachineStatusEvent> statusHandler = new SubscriptionHandler<MachineStatusEvent>(
                dtoUnmarshallerFactory.newWSUnmarshaller(MachineStatusEvent.class)) {
            @Override
            protected void onMessageReceived(MachineStatusEvent event) {
                if (MachineStatusEvent.EventType.RUNNING == event.getEventType()) {
                    onConnected(event.getMachineId());
                } else if (MachineStatusEvent.EventType.ERROR == event.getEventType()) {
                    unsubscribeFromMachineChannel(event.getMachineName());
                    onConnectingFailed(event.getError());
                }
            }

            @Override
            protected void onErrorReceived(Throwable exception) {
                Log.error(ManageDevicesPresenter.class, exception.getMessage());
            }
        };

        try {
            messageBusProvider.getMessageBus().subscribe(channel, statusHandler);
            subscriptions.put(channel, statusHandler);
        } catch (Exception e) {
            Log.error(ManageDevicesPresenter.class, e.getMessage());
        }
    }

    /**
     * Ensures machine is started.
     */
    private void onConnected(final String machineId) {
        // There is a little bug in machine service on the server side.
        // The machine info is updated with a little delay after running a machine.
        // Using timer must fix the problem.
        new Timer() {
            @Override
            public void run() {
                machineService.getMachine(machineId).then(new Operation<MachineDto>() {
                    @Override
                    public void apply(MachineDto machineDto) throws OperationException {
                        if (machineDto.getStatus() == RUNNING) {
                            eventBus.fireEvent(new MachineStateEvent(machineDto, MachineStateEvent.MachineAction.RUNNING));
                            connectNotification.setTitle(locale.deviceConnectSuccess(machineDto.getConfig().getName()));
                            connectNotification.setStatus(StatusNotification.Status.SUCCESS);
                            updateDevices(machineDto.getConfig().getName());
                        } else if (machineDto.getStatus() == CREATING) {
                            onConnected(machineId);
                        } else {
                            onConnectingFailed(null);
                        }
                    }
                }).catchError(new Operation<PromiseError>() {
                    @Override
                    public void apply(PromiseError arg) throws OperationException {
                        onConnectingFailed(null);
                    }
                });
            }
        }.schedule(1000);
    }

    /**
     * Handles connecting error and displays an error message.
     *
     * @param reason
     *          a reason to be attached to the error message
     */
    private void onConnectingFailed(String reason) {
        connectNotification.setTitle(locale.deviceConnectError(selectedDevice.getName()));
        if (reason != null) {
            connectNotification.setContent(reason);
        }

        connectNotification.setStatus(StatusNotification.Status.FAIL);

        view.selectDevice(selectedDevice);
    }

    /**
     * Removes the subscription from the websocket channel and stops listening machine status events.
     *
     * @param machineName
     *          name of the machine to unsubscribe
     */
    private void unsubscribeFromMachineChannel(String machineName) {
        String channel = "machine:status:" + appContext.getWorkspace().getId() + ':' + machineName;

        SubscriptionHandler<MachineStatusEvent> statusHandler = subscriptions.remove(channel);
        if (statusHandler != null) {
            try {
                messageBusProvider.getMessageBus().unsubscribe(channel, statusHandler);
            } catch (Exception e) {
                Log.error(getClass(), e.getMessage());
            }
        }
    }

}
