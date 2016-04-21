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
package org.eclipse.che.plugin.artik.ide.ext.help.client.support;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.ide.api.ProductInfoDataProvider;
import org.eclipse.che.ide.api.action.Action;
import org.eclipse.che.ide.api.action.ActionEvent;
import org.vectomatic.dom.svg.ui.SVGResource;

/**
 * Redirect to support window
 *
 * @author Oleksii Orel
 * @author Alexander Andrienko
 */
@Singleton
public class RedirectToSupportAction extends Action {
    private final ProductInfoDataProvider productInfoDataProvider;

    @Inject
    public RedirectToSupportAction(ProductInfoDataProvider productInfoDataProvider,
                                   SupportResources resources) {
        super(productInfoDataProvider.getSupportTitle(), productInfoDataProvider.getSupportTitle(), null, resources.getSupport());
        this.productInfoDataProvider = productInfoDataProvider;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Window.open(productInfoDataProvider.getSupportLink(), "_blank", null);
    }

    public interface SupportResources extends ClientBundle {
        @Source("support.svg")
        SVGResource getSupport();
    }
}
