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

import com.google.inject.Inject;

import org.eclipse.che.ide.api.action.Action;
import org.eclipse.che.ide.api.action.ActionEvent;
import org.eclipse.che.ide.api.app.AppContext;
import org.vectomatic.dom.svg.ui.SVGResource;


/**
 * Artik Action
 *
 * @author Vitalii Parfonov
 */
public abstract class ArtikAction extends Action {

    protected AppContext appContext;

    protected ArtikAction(String text, String description, SVGResource svgIcon) {
        super(text, description, null, svgIcon);
    }

    @Override
    public final void update(ActionEvent e) {
        updateArtikAction(e);
    }

    protected abstract void updateArtikAction(ActionEvent e);

    @Inject
    private void injectAppContext(AppContext appContext) {
        this.appContext = appContext;
    }
}
