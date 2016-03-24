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
package org.eclipse.che.plugin.artik.ide;

import com.google.inject.Inject;

import org.eclipse.che.ide.api.action.ActionManager;
import org.eclipse.che.ide.api.action.DefaultActionGroup;
import org.eclipse.che.ide.api.constraints.Constraints;
import org.eclipse.che.ide.api.extension.Extension;
import org.eclipse.che.plugin.artik.ide.action.RegisterArtikDeviceAction;

import static org.eclipse.che.ide.api.action.IdeActions.GROUP_MAIN_MENU;

/**
 * @author Vitalii Parfonov
 *
 * */
@Extension(title = "Samsung Artik")
public class ArtikExtension {

    public static final String ARTIK = "Artik";

    @Inject
    public ArtikExtension(RegisterArtikDeviceAction registerArtikDevice,
                          ArtikResources artikResources,
                          ActionManager actionManager){

        final DefaultActionGroup mainMenu = (DefaultActionGroup)actionManager.getAction(GROUP_MAIN_MENU);

        DefaultActionGroup artikGroup = new DefaultActionGroup(ARTIK, true, actionManager);
        artikGroup.getTemplatePresentation().setDescription("Samsung Artik Devices");
        artikGroup.getTemplatePresentation().setSVGResource(artikResources.artikIcon());
        artikGroup.getTemplatePresentation().setEnabledAndVisible(true);
        actionManager.registerAction(ARTIK, artikGroup);
        mainMenu.add(artikGroup);

        actionManager.registerAction("registerArtikDevice", registerArtikDevice);
        artikGroup.add(registerArtikDevice, Constraints.FIRST);
    }

}
