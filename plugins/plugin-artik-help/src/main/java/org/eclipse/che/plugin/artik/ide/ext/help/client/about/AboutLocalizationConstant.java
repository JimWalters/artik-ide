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

import com.google.gwt.i18n.client.Messages;


/**
 * Localization for About dialog.
 *
 * @author Ann Shumilova
 * @author Oleksii Orel
 */
public interface AboutLocalizationConstant extends Messages {
    @Key("about.version")
    String aboutVersion();

    @Key("about.revision")
    String aboutRevision();

    @Key("about.buildtime")
    String aboutBuildTime();

    @Key("about.control.title")
    String aboutControlTitle();

    /* Buttons */
    @Key("ok")
    String ok();
}
