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
package org.eclipse.che.plugin.machine.artik.discovery;

import com.jayway.restassured.response.Response;

import org.eclipse.che.dto.server.DtoFactory;
import org.eclipse.che.plugin.artik.shared.dto.ArtikDeviceDto;
import org.everrest.assured.EverrestJetty;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.testng.MockitoTestNGListener;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.List;

import static com.jayway.restassured.RestAssured.given;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

/** @author Artem Zatsarynnyi */
@Listeners(value = {EverrestJetty.class, MockitoTestNGListener.class})
public class DeviceDiscoveryServiceTest {

    @Mock
    private DeviceDiscoverer       discoverer;
    @InjectMocks
    private DeviceDiscoveryService service;

    private static <T> List<T> unwrapDtoList(Response response, Class<T> dtoClass) {
        return DtoFactory.getInstance().createListDtoFromJson(response.body().print(), dtoClass)
                         .stream()
                         .collect(toList());
    }

    @Test
    public void shouldGetDiscoveredDevices() throws Exception {
        final String deviceId1 = "018f0d93fe94";
        final String deviceId2 = "01b52fd3fe94";
        final String deviceId3 = "01h6f303fe94";

        final ArtikDeviceImpl device1 = new ArtikDeviceImpl(deviceId1, "172.19.20.110");
        final ArtikDeviceImpl device2 = new ArtikDeviceImpl(deviceId2, "172.19.20.111");
        final ArtikDeviceImpl device3 = new ArtikDeviceImpl(deviceId3, "172.19.20.112");

        when(discoverer.getDeviceInfo(deviceId1)).thenReturn(device1);
        when(discoverer.getDeviceInfo(deviceId2)).thenReturn(device2);
        when(discoverer.getDeviceInfo(deviceId3)).thenReturn(device3);

        when(discoverer.discover()).thenReturn(asList(deviceId1, deviceId2, deviceId3));

        Response response = given().when().get("/discovery/test-workspace");

        assertEquals(response.getStatusCode(), 200);

        List<ArtikDeviceDto> devices = unwrapDtoList(response, ArtikDeviceDto.class);
        assertEquals(devices.stream()
                            .map(ArtikDeviceImpl::new)
                            .collect(toList()), asList(device1, device2, device3));
    }
}
