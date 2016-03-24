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
package org.eclipse.che.plugin.artik.ide.inject;

import com.google.gwt.inject.client.AbstractGinModule;
import org.eclipse.che.ide.api.extension.ExtensionGinModule;
import org.eclipse.che.plugin.artik.ide.action.RegisterArtikDeviceAction;
import org.eclipse.che.plugin.artik.ide.manager.RegisterDeviceView;
import org.eclipse.che.plugin.artik.ide.manager.RegisterDeviceViewImpl;
import org.eclipse.che.plugin.artik.ide.service.ArtikDeviceServiceClient;
import org.eclipse.che.plugin.artik.ide.service.ArtikDeviceServiceClientImpl;

/**
 * @author Vitalii Parfonov
 */
@ExtensionGinModule
public class ArtikGinModule extends AbstractGinModule {

    /** {@inheritDoc} */
    @Override
    protected void configure() {
        bind(ArtikDeviceServiceClient.class).to(ArtikDeviceServiceClientImpl.class);
        bind(RegisterDeviceView.class).to(RegisterDeviceViewImpl.class);
    }


}
