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

import org.eclipse.che.api.core.ServerException;
import org.eclipse.che.api.core.util.CommandLine;
import org.eclipse.che.api.core.util.CompositeLineConsumer;
import org.eclipse.che.api.core.util.LineConsumer;
import org.eclipse.che.api.core.util.ListLineConsumer;
import org.eclipse.che.api.core.util.ProcessUtil;
import org.eclipse.che.plugin.artik.shared.ArtikDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The class provides discovering of Artik devices
 * which are connected to the adb server over USB interface.
 *
 * @author Artem Zatsarynnyi
 */
@Singleton
public class DeviceDiscoverer {

    private static final Logger LOG = LoggerFactory.getLogger(DeviceDiscoverer.class);

    /**
     * Discovers the connected Artik devices.
     *
     * @return the list of the discovered devices
     * @throws ServerException
     *         if any error occurs while discovering process
     */
    public List<String> discover() throws ServerException {
        final CommandLine commandLine = new CommandLine("adb", "devices");
        final ListLineConsumer lineConsumer = new ListLineConsumer();
        executeCommand(commandLine, lineConsumer);

        List<String> devices = new ArrayList<>();
        for (String line : lineConsumer.getLines()) {
            final int i = line.indexOf("\tdevice");
            if (i > -1) {
                devices.add(line.substring(0, i));
            }
        }

        return devices;
    }

    /**
     * Returns information about the connected device with the specified identifier.
     *
     * @param deviceId
     *         identifier of the device to get info
     * @return info about connected device
     * @throws ServerException
     *         if any error occurs while getting device information
     */
    public ArtikDevice getDeviceInfo(String deviceId) throws ServerException {
        Optional<String> eth0IPAddress = getDeviceIPv4Address(deviceId, "eth0");
        if (eth0IPAddress.isPresent()) {
            return new ArtikDeviceImpl(deviceId, eth0IPAddress.get());
        } else {
            Optional<String> wlan0IPAddress = getDeviceIPv4Address(deviceId, "wlan0");
            if (wlan0IPAddress.isPresent()) {
                return new ArtikDeviceImpl(deviceId, wlan0IPAddress.get());
            }
        }

        throw new ServerException("Unable to get IP address of device " + deviceId);
    }

    /** Returns the IPv4 address assigned to the specified network interface. */
    private static Optional<String> getDeviceIPv4Address(String deviceId, String interfaceName) throws ServerException {
        final CommandLine commandLine = new CommandLine("adb", "-s", deviceId, "shell", "ifconfig", interfaceName);
        final ListLineConsumer lineConsumer = new ListLineConsumer();
        executeCommand(commandLine, lineConsumer);

        final String ipV4ConfigLine = lineConsumer.getLines().get(1);
        if (ipV4ConfigLine.startsWith("        inet ")) {
            final String ipV4Address = ipV4ConfigLine.trim().split("\\s+")[1];
            return Optional.of(ipV4Address);
        }

        return Optional.empty();
    }

    private static void executeCommand(CommandLine commandLine, LineConsumer lineConsumer) throws ServerException {
        ProcessBuilder pb = new ProcessBuilder(commandLine.toShellCommand());

        try (LineConsumer consumer = new CompositeLineConsumer(lineConsumer)) {
            Process process;
            try {
                process = ProcessUtil.execute(pb, consumer);
            } catch (IOException e) {
                LOG.error("Process starting failed", e);
                throw new ServerException(String.format("Unable to execute command: %s", commandLine.toString()));
            }

            try {
                process.waitFor();
                if (process.exitValue() != 0) {
                    LOG.debug(String.format("Command execution failed: %s", commandLine.toString()));
                    throw new ServerException(String.format("Command execution failed: %s", commandLine.toString()));
                } else {
                    LOG.debug(String.format("Command execution successful: %s", commandLine.toString()));
                }
            } catch (InterruptedException e) {
                Thread.interrupted();
            }
        } catch (IOException e) {
            LOG.error("An error occurred while trying to close the LineConsumer", e);
        }
    }
}
