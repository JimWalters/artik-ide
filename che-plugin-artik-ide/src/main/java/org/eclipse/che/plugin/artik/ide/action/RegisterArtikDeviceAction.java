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
package org.eclipse.che.plugin.artik.ide.action;

import org.eclipse.che.ide.api.action.ActionEvent;
import org.eclipse.che.plugin.artik.ide.ArtikLocalizationConstant;
import org.eclipse.che.plugin.artik.ide.ArtikResources;
import org.eclipse.che.plugin.artik.ide.manager.RegisterDeviceView;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * //
 *
 * @author Vitalii Parfonov
 */
@Singleton
public class RegisterArtikDeviceAction extends ArtikAction {


    private final RegisterDeviceView registerDeviceView;
    private final ArtikLocalizationConstant artikLocalizationConstant;

    @Inject
    protected RegisterArtikDeviceAction(RegisterDeviceView registerDeviceView,
                                        ArtikLocalizationConstant artikLocalizationConstant,
                                        ArtikResources artikResources) {
        super(artikLocalizationConstant.registerArtikDeviceTitle(),
              artikLocalizationConstant.registerArtikDeviceDescription(),
              artikResources.artikIcon());
        this.registerDeviceView = registerDeviceView;
        this.artikLocalizationConstant = artikLocalizationConstant;
    }

    @Override
    protected void updateArtikAction(ActionEvent e) {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
      registerDeviceView.show();
    }
}
