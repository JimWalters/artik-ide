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
package org.eclipse.che.plugin.artik.ide.ext.help.client.about;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.ide.api.action.Action;
import org.eclipse.che.ide.api.action.ActionEvent;

/**
 * Action for showing About application information.
 *
 * @author Ann Shumilova
 * @author Oleksii Orel
 */
@Singleton
public class ShowAboutAction extends Action {

    private final AboutPresenter       presenter;

    @Inject
    public ShowAboutAction(AboutPresenter presenter,
                           AboutLocalizationConstant locale,
                           AboutResources resources) {
        super(locale.aboutControlTitle(), "Show about application", null, resources.about());
        this.presenter = presenter;
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        presenter.showAbout();
    }

}
