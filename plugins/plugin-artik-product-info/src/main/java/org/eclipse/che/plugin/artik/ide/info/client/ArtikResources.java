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

import com.google.gwt.resources.client.ClientBundle;

import org.vectomatic.dom.svg.ui.SVGResource;

/**
 * Artik extension resources.
 *
 * @author Oleksii Orel
 */
public interface ArtikResources extends ClientBundle {
    @Source("logo/artik_logo.svg")
    SVGResource logo();
}
