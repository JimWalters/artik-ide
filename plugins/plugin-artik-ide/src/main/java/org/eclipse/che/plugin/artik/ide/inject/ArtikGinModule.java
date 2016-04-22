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
package org.eclipse.che.plugin.artik.ide.inject;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.inject.client.assistedinject.GinFactoryModuleBuilder;
import com.google.inject.Singleton;

import org.eclipse.che.ide.api.extension.ExtensionGinModule;
import org.eclipse.che.plugin.artik.ide.discovery.DeviceDiscoveryServiceClient;
import org.eclipse.che.plugin.artik.ide.discovery.DeviceDiscoveryServiceClientImpl;
import org.eclipse.che.plugin.artik.ide.scp.action.PushToDeviceActionFactory;

/**
 * Gin module for Artik extension.
 *
 * @author Vitalii Parfonov
 * @author Artem Zatsarynnyi
 */
@ExtensionGinModule
public class ArtikGinModule extends AbstractGinModule {

    @Override
    protected void configure() {
        install(new GinFactoryModuleBuilder().build(PushToDeviceActionFactory.class));
        bind(DeviceDiscoveryServiceClient.class).to(DeviceDiscoveryServiceClientImpl.class).in(Singleton.class);
    }
}
