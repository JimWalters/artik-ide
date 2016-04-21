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
package org.eclipse.che.plugin.artik.ide.info.client;

import com.google.inject.Singleton;

import org.eclipse.che.ide.api.ProductInfoDataProvider;
import org.vectomatic.dom.svg.ui.SVGResource;

import javax.inject.Inject;

/**
 * Implementation of {@link ProductInfoDataProvider}
 *
 * @author Oleksii Orel
 */
@Singleton
public class ArtikProductInfoDataProvider implements ProductInfoDataProvider {

    private final ArtikLocalizationConstant locale;
    private final ArtikResources            resources;

    @Inject
    public ArtikProductInfoDataProvider(ArtikLocalizationConstant locale, ArtikResources resources) {
        this.locale = locale;
        this.resources = resources;
    }

    @Override
    public String getName() {
        return locale.getProductName();
    }

    @Override
    public String getSupportLink() {
        return locale.getSupportLink();
    }

    @Override
    public String getDocumentTitle() {
        return locale.cheTabTitle();
    }

    @Override
    public String getDocumentTitle(String workspaceName) {
        return locale.cheTabTitle(workspaceName);
    }

    @Override
    public SVGResource getLogo() {
        return resources.logo();
    }

    @Override
    public String getSupportTitle() {
        return locale.supportTitle();
    }
}
