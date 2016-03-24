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
package org.eclipse.che.plugin.artik.server.rest;

import org.eclipse.che.api.core.rest.Service;
import org.eclipse.che.plugin.artik.shared.dto.ArtikDeviceInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.util.Collections;
import java.util.List;

/**
 *
 */
@Path("/artik")
@Singleton
public class ArtikDevicesService extends Service {
  private static final Logger LOG = LoggerFactory.getLogger(ArtikDevicesService.class);


  @GET
  @Path("/discovery")
  public List<ArtikDeviceInfo> artikDeviceInfos(){
    return Collections.emptyList();
  }
}
