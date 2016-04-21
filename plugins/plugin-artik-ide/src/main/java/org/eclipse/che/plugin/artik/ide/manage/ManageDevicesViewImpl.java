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

import elemental.events.KeyboardEvent;
import elemental.svg.SVGElement;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.ide.CoreLocalizationConstant;
import org.eclipse.che.ide.api.icon.Icon;
import org.eclipse.che.ide.api.icon.IconRegistry;
import org.eclipse.che.ide.extension.machine.client.command.edit.EditCommandResources;
import org.eclipse.che.ide.ui.Tooltip;
import org.eclipse.che.ide.ui.list.CategoriesList;
import org.eclipse.che.ide.ui.list.Category;
import org.eclipse.che.ide.ui.list.CategoryRenderer;
import org.eclipse.che.ide.ui.window.Window;
import org.eclipse.che.plugin.artik.ide.ArtikLocalizationConstant;
import org.eclipse.che.plugin.artik.ide.ArtikResources;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.gwt.dom.client.Style.Unit.PX;
import static org.eclipse.che.ide.ui.menu.PositionController.HorizontalAlign.MIDDLE;
import static org.eclipse.che.ide.ui.menu.PositionController.VerticalAlign.BOTTOM;

/**
 * @author Vitaliy Guliy
 */
@Singleton
public class ManageDevicesViewImpl extends Window implements ManageDevicesView {

    interface DevicesViewImplUiBinder extends UiBinder<Widget, ManageDevicesViewImpl> {
    }

    private EditCommandResources commandResources;
    private ArtikResources       artikResources;
    private IconRegistry         iconRegistry;

    private ActionDelegate delegate;

    @UiField(provided = true)
    ArtikLocalizationConstant locale;

    @UiField
    SimplePanel devicesPanel;

    private CategoriesList list;

    @UiField
    FlowPanel hintPanel;

    @UiField
    FlowPanel infoPanel;

    @UiField
    FlowPanel propertiesPanel;

    @UiField
    TextBox deviceName;

    @UiField
    TextBox host;

    @UiField
    TextBox port;

    @UiField
    TextBox userName;

    @UiField
    PasswordTextBox password;

    @UiField
    FlowPanel footer;

    private Button closeButton;

    private Button saveButton;
    private Button cancelButton;
    private Button connectButton;

    @Inject
    public ManageDevicesViewImpl(org.eclipse.che.ide.Resources resources,
                                 ArtikLocalizationConstant locale,
                                 ArtikResources artikResources,
                                 CoreLocalizationConstant coreLocale,
                                 EditCommandResources commandResources,
                                 IconRegistry iconRegistry,
                                 DevicesViewImplUiBinder uiBinder) {
        this.locale = locale;
        this.artikResources = artikResources;
        this.commandResources = commandResources;
        this.iconRegistry = iconRegistry;

        setWidget(uiBinder.createAndBindUi(this));
        getWidget().getElement().getStyle().setPadding(0, Style.Unit.PX);
        setTitle(locale.manageArtikDevicesViewTitle());

        list = new CategoriesList(resources);
        list.addDomHandler(new KeyDownHandler() {
            @Override
            public void onKeyDown(KeyDownEvent event) {
                switch (event.getNativeKeyCode()) {
                    case KeyboardEvent.KeyCode.INSERT:
                        break;
                    case KeyboardEvent.KeyCode.DELETE:
                        break;
                }
            }
        }, KeyDownEvent.getType());
        devicesPanel.add(list);

        closeButton = createButton(coreLocale.close(), "devices.button.close",
                                   new ClickHandler() {
                                       @Override
                                       public void onClick(ClickEvent event) {
                                           delegate.onCloseClicked();
                                       }
                                   });
        addButtonToFooter(closeButton);

        saveButton = createButton(coreLocale.save(), "devices.button.save", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                delegate.onSaveClicked();
            }
        });
        saveButton.addStyleName(this.resources.windowCss().primaryButton());
        footer.add(saveButton);

        cancelButton = createButton(coreLocale.cancel(), "devices.button.cancel", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                delegate.onCancelClicked();
            }
        });
        footer.add(cancelButton);

        connectButton = createButton("Connect", "devices.button.connect", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                delegate.onConnectClicked();
            }
        });
        connectButton.addStyleName(this.resources.windowCss().primaryButton());
        connectButton.addStyleName(resources.Css().buttonLoader());
        footer.add(connectButton);

        deviceName.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent keyUpEvent) {
                delegate.onDeviceNameChanged(deviceName.getValue());
            }
        });

        host.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent keyUpEvent) {
                delegate.onHostChanged(host.getValue());
            }
        });

        port.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent keyUpEvent) {
                delegate.onPortChanged(port.getValue());
            }
        });

        userName.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent keyUpEvent) {
                delegate.onUserNameChanged(userName.getValue());
            }
        });

        password.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent keyUpEvent) {
                delegate.onPasswordChanged(password.getValue());
            }
        });
    }

    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    public void clear() {
        list.clear();

        hintPanel.setVisible(true);
        infoPanel.setVisible(false);
        propertiesPanel.setVisible(false);
    }

    @Override
    public void showHintPanel() {
        hintPanel.setVisible(true);
        infoPanel.setVisible(false);
        propertiesPanel.setVisible(false);
    }

    @Override
    public void showInfoPanel() {
        hintPanel.setVisible(false);
        infoPanel.setVisible(true);
        propertiesPanel.setVisible(false);
    }

    @Override
    public void showPropertiesPanel() {
        hintPanel.setVisible(false);
        infoPanel.setVisible(false);
        propertiesPanel.setVisible(true);
    }

    @Override
    public void showDevices(List<Device> devices) {
        Collections.sort(devices, new Comparator<Device>() {
            @Override
            public int compare(Device device1, Device device2) {
                return device1.getName().compareTo(device2.getName());
            }
        });

        HashMap<String, List<Device>> categories = new HashMap<>();
        for (Device device : devices) {
            List<Device> categoryDevices = categories.get(device.getType());
            if (categoryDevices == null) {
                categoryDevices = new ArrayList<>();
                categories.put(device.getType(), categoryDevices);
            }
            categoryDevices.add(device);
        }

        List<Category<?>> categoriesList = new ArrayList<>();
        for (Map.Entry<String, List<Device>> entry : categories.entrySet()) {
            categoriesList.add(new Category<>(entry.getKey(), categoriesRenderer, entry.getValue(), categoriesEventDelegate));
        }

        ensureSSHCategoryExists(categoriesList);

        list.clear();
        list.render(categoriesList);
    }

    @Override
    public void selectDevice(Device device) {
        list.selectElement(device);
    }

    private void ensureSSHCategoryExists(List<Category<?>> categoriesList) {
        for (Category<?> category : categoriesList) {
            if (ManageDevicesPresenter.ARTIK_CATEGORY.equalsIgnoreCase(category.getTitle())) {
                return;
            }
        }

        categoriesList.add(
                new Category<>(ManageDevicesPresenter.ARTIK_CATEGORY, categoriesRenderer, new ArrayList<Device>(), categoriesEventDelegate));
    }

    private SpanElement renderCategoryHeader(final Category<Device> category) {
        SpanElement categoryHeaderElement = Document.get().createSpanElement();
        categoryHeaderElement.setClassName(commandResources.getCss().categoryHeader());
        SpanElement iconElement = Document.get().createSpanElement();
        iconElement.appendChild(artikResources.artikIcon().getSvg().getElement());
        categoryHeaderElement.appendChild(iconElement);

        SpanElement textElement = Document.get().createSpanElement();
        categoryHeaderElement.appendChild(textElement);
        textElement.setInnerText(category.getTitle());

        if (ManageDevicesPresenter.ARTIK_CATEGORY.equalsIgnoreCase(category.getTitle())) {
            // Add button to create a device
            SpanElement buttonElement = Document.get().createSpanElement();
            buttonElement.appendChild(commandResources.addCommandButton().getSvg().getElement());
            categoryHeaderElement.appendChild(buttonElement);

            Event.sinkEvents(buttonElement, Event.ONCLICK);
            Event.setEventListener(buttonElement, new EventListener() {
                @Override
                public void onBrowserEvent(Event event) {
                    event.stopPropagation();
                    event.preventDefault();
                    delegate.onAddDevice(category.getTitle());
                }
            });
        } else {
            // Add empty span for properly aligning items
            categoryHeaderElement.appendChild(Document.get().createSpanElement());
        }

        return categoryHeaderElement;
    }

    private final CategoryRenderer<Device> categoriesRenderer =
            new CategoryRenderer<Device>() {
                @Override
                public void renderElement(Element element, final Device device) {
                    element.setInnerText(device.getName());
                    element.getStyle().setPaddingLeft(54, PX);
                    element.addClassName(commandResources.getCss().categorySubElementHeader());
                    element.setId("device-" + device.getName());

                    if (device.getRecipe() == null) {
                        element.getStyle().setProperty("color", "gray");
                    } else {
                        if (device.isConnected()) {
                            DivElement running = Document.get().createDivElement();
                            running.setClassName(commandResources.getCss().running());
                            element.appendChild(running);

                            Tooltip.create((elemental.dom.Element)running,
                                           BOTTOM,
                                           MIDDLE,
                                           "Connected");
                        }

                        SpanElement categorySubElement = Document.get().createSpanElement();
                        categorySubElement.setClassName(commandResources.getCss().buttonArea());
                        element.appendChild(categorySubElement);

                        SpanElement removeCommandButtonElement = Document.get().createSpanElement();
                        categorySubElement.appendChild(removeCommandButtonElement);
                        removeCommandButtonElement.appendChild(commandResources.removeCommandButton().getSvg().getElement());
                        Event.sinkEvents(removeCommandButtonElement, Event.ONCLICK);
                        Event.setEventListener(removeCommandButtonElement, new EventListener() {
                            @Override
                            public void onBrowserEvent(Event event) {
                                if (Event.ONCLICK == event.getTypeInt()) {
                                    event.stopPropagation();
                                    event.preventDefault();
                                    delegate.onDeleteDevice(device);
                                }
                            }
                        });
                    }
                }

                @Override
                public SpanElement renderCategory(Category<Device> category) {
                    return renderCategoryHeader(category);
                }
            };

    private final Category.CategoryEventDelegate<Device> categoriesEventDelegate =
            new Category.CategoryEventDelegate<Device>() {
                @Override
                public void onListItemClicked(Element listItemBase, Device device) {
                    delegate.onDeviceSelected(device);
                }
            };

    @Override
    public void setDeviceName(String deviceName) {
        this.deviceName.setValue(deviceName);
    }

    @Override
    public String getDeviceName() {
        return deviceName.getValue();
    }

    @Override
    public void setHost(String host) {
        this.host.setValue(host);
    }

    @Override
    public String getHost() {
        return host.getValue();
    }

    @Override
    public void setPort(String port) {
        this.port.setValue(port);
    }

    @Override
    public String getPort() {
        return port.getValue();
    }

    @Override
    public void setUserName(String userName) {
        this.userName.setValue(userName);
    }

    @Override
    public String getUserName() {
        return userName.getValue();
    }

    @Override
    public void setPassword(String password) {
        this.password.setValue(password);
    }

    @Override
    public String getPassword() {
        return password.getValue();
    }

    @Override
    public void enableSaveButton(boolean enable) {
        saveButton.setEnabled(enable);
    }

    @Override
    public void enableCancelButton(boolean enable) {
        cancelButton.setEnabled(enable);
    }

    @Override
    public void enableConnectButton(boolean enable) {
        connectButton.setEnabled(enable);
    }

    @Override
    public void setConnectButtonText(String title) {
        if (title == null || title.isEmpty()) {
            connectButton.setText("");
            connectButton.setHTML("<i></i>");
        } else {
            connectButton.setText(title);
        }
    }

    @Override
    public void selectDeviceName() {
        deviceName.setFocus(true);
        deviceName.selectAll();
    }

}
