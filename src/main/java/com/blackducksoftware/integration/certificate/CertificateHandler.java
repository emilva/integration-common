/**
 * Integration Common
 *
 * Copyright (C) 2017 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.blackducksoftware.integration.certificate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.log.IntLogger;

public class CertificateHandler {

    private final IntLogger logger;

    public CertificateHandler(final IntLogger intLogger) {
        logger = intLogger;
    }

    public void retrieveAndImportHttpsCertificate(final URL url, final String optionalKeyStorePass) throws IntegrationException {
        File certificate = null;
        try {
            certificate = File.createTempFile("temporaryCertificate", ".tmp");
            retrieveAndSaveHttpsCertificate(url, certificate);
            importHttpsCertificateFromFile(url, certificate, optionalKeyStorePass);
        } catch (final IntegrationException e) {
            throw e;
        } catch (final Exception e) {
            throw new IntegrationException(e.getMessage(), e);
        } finally {
            if (certificate.exists()) {
                certificate.delete();
            }
        }
    }

    public File retrieveAndSaveHttpsCertificate(final URL url, final File temporaryCertificateFile) throws IntegrationException {
        try {
            final String output = retrieveHttpsCertificateFromURL(url);
            if (output.contains("BEGIN CERTIFICATE")) {
                try (final OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(temporaryCertificateFile), StandardCharsets.UTF_8);) {
                    writer.write(output);
                }
            } else {
                // didn't contain the expected certificate output
                logger.warn(output);
            }
        } catch (final IntegrationException e) {
            throw e;
        } catch (final Exception e) {
            throw new IntegrationException(e.getMessage(), e);
        }
        return temporaryCertificateFile;
    }

    public String retrieveHttpsCertificateFromURL(final URL url) throws IntegrationException {
        final String serverHost = getServerHost(url);
        logger.info(String.format("Retrieving the certificate from %s", serverHost));
        final String[] command = { "keytool", "-printcert", "-rfc", "-sslserver", url.getHost() + ":" + url.getPort() };
        String output = "";
        try {
            final ProcessBuilder processBuilder = new ProcessBuilder(command);
            final Process proc = processBuilder.start();
            final int exitCode = proc.waitFor();
            output = readInputStream(proc.getInputStream());
            final String errorOutput = readInputStream(proc.getErrorStream());
            // destroy() will cleanup the process resources, including the streams
            proc.destroy();
            if (StringUtils.isNotBlank(errorOutput)) {
                if (exitCode != 0) {
                    logger.error(errorOutput);
                } else {
                    logger.info(errorOutput);
                }
            }
            logger.debug(String.format("Exit code %d", exitCode));
            if (exitCode != 0) {
                throw new IntegrationException(String.format("Failed to retrieve the certificate from %s. %s", serverHost, output));
            }
        } catch (final Exception e) {
            throw new IntegrationException(e);
        }
        return output;
    }

    public void importHttpsCertificateFromFile(final URL url, final File certificate, String optionalKeyStorePass) throws IntegrationException {
        final String javaHome = System.getProperty("java.home");
        File jssecacerts = new File(javaHome);
        jssecacerts = new File(jssecacerts, "lib");
        jssecacerts = new File(jssecacerts, "security");
        jssecacerts = new File(jssecacerts, "jssecacerts");
        final String keyStore = jssecacerts.getAbsolutePath();
        logger.info(String.format("Importing the certificate from %s into keystore %s", certificate.getAbsolutePath(), keyStore));
        if (StringUtils.isBlank(optionalKeyStorePass)) {
            optionalKeyStorePass = "changeit";
        }
        final String[] command = { "keytool", "-importcert", "-keystore", keyStore, "-storepass", optionalKeyStorePass, "-alias", url.getHost(), "-noprompt",
                "-file", certificate.getAbsolutePath() };
        try {
            final ProcessBuilder processBuilder = new ProcessBuilder(command);
            final Process proc = processBuilder.start();
            final int exitCode = proc.waitFor();
            final String output = readInputStream(proc.getInputStream());
            final String errorOutput = readInputStream(proc.getErrorStream());
            // destroy() will cleanup the process resources, including the streams
            proc.destroy();
            if (StringUtils.isNotBlank(output)) {
                if (exitCode != 0) {
                    logger.error(output);
                } else {
                    logger.info(output);
                }
            }
            if (StringUtils.isNotBlank(errorOutput)) {
                if (exitCode != 0) {
                    logger.error(errorOutput);
                } else {
                    logger.info(errorOutput);
                }
            }
            logger.debug(String.format("Exit code %d", exitCode));
            if (exitCode != 0) {
                throw new IntegrationException(String.format("Failed to import the certificate into %s", keyStore));
            }
        } catch (final Exception e) {
            throw new IntegrationException(e);
        }
    }

    public void removeHttpsCertificate(final URL url, String optionalKeyStorePass) throws IntegrationException {
        final String javaHome = System.getProperty("java.home");
        File jssecacerts = new File(javaHome);
        jssecacerts = new File(jssecacerts, "lib");
        jssecacerts = new File(jssecacerts, "security");
        jssecacerts = new File(jssecacerts, "jssecacerts");
        final String keyStore = jssecacerts.getAbsolutePath();
        logger.info(String.format("Removing the certificate from %s", keyStore));
        if (StringUtils.isBlank(optionalKeyStorePass)) {
            optionalKeyStorePass = "changeit";
        }
        final String[] command = { "keytool", "-delete", "-keystore", keyStore, "-storepass", optionalKeyStorePass, "-alias", url.getHost(), "-noprompt" };
        try {
            final ProcessBuilder processBuilder = new ProcessBuilder(command);
            final Process proc = processBuilder.start();
            final int exitCode = proc.waitFor();
            final String output = readInputStream(proc.getInputStream());
            final String errorOutput = readInputStream(proc.getErrorStream());
            // destroy() will cleanup the process resources, including the streams
            proc.destroy();
            if (StringUtils.isNotBlank(output)) {
                logger.info(output);
            }
            if (StringUtils.isNotBlank(errorOutput)) {
                logger.warn(errorOutput);
            }
            logger.debug(String.format("Exit code %d", exitCode));
            if (proc.exitValue() != 0) {
                throw new IntegrationException(String.format("Failed to remove the certificate from %s", keyStore));
            }
        } catch (final Exception e) {
            throw new IntegrationException(e);
        }
    }

    public boolean isCertificateInKeystore(final URL url, String optionalKeyStorePass) throws IntegrationException {
        final String javaHome = System.getProperty("java.home");
        File jssecacerts = new File(javaHome);
        jssecacerts = new File(jssecacerts, "lib");
        jssecacerts = new File(jssecacerts, "security");
        jssecacerts = new File(jssecacerts, "jssecacerts");
        if (!jssecacerts.exists()) {
            return false;
        }
        final String keyStore = jssecacerts.getAbsolutePath();
        logger.info(String.format("Removing the certificate from %s", keyStore));
        if (StringUtils.isBlank(optionalKeyStorePass)) {
            optionalKeyStorePass = "changeit";
        }
        final String[] command = { "keytool", "-list", "-keystore", keyStore, "-storepass", optionalKeyStorePass, "-alias", url.getHost(), "-noprompt" };
        boolean certificateIsInKeystore = false;
        try {
            final ProcessBuilder processBuilder = new ProcessBuilder(command);
            final Process proc = processBuilder.start();
            final int exitCode = proc.waitFor();
            final String output = readInputStream(proc.getInputStream());
            final String errorOutput = readInputStream(proc.getErrorStream());
            // destroy() will cleanup the process resources, including the streams
            proc.destroy();
            if (StringUtils.isNotBlank(output)) {
                if (exitCode != 0) {
                    if (output.contains("Alias <" + url.getHost() + "> does not exist")) {
                        return false;
                    }
                    logger.error(output);
                } else {
                    certificateIsInKeystore = true;
                }
            }
            if (StringUtils.isNotBlank(errorOutput)) {
                if (exitCode != 0) {
                    logger.error(output);
                } else {
                    certificateIsInKeystore = true;
                }
            }
            logger.debug(String.format("Exit code %d", exitCode));
            if (proc.exitValue() != 0 && !output.contains("Alias <" + url.getHost() + "> does not exist")) {
                throw new IntegrationException(String.format("Failed to run command '%s'", StringUtils.join(command, " ")));
            }
        } catch (final Exception e) {
            throw new IntegrationException(e);
        }
        return certificateIsInKeystore;
    }

    private String readInputStream(final InputStream stream) throws IOException {
        return IOUtils.toString(stream, StandardCharsets.UTF_8);
    }

    private String getServerHost(final URL url) {
        String serverHost = url.getHost();
        if (url.getPort() > 0) {
            serverHost += ":" + url.getPort();
        }
        return serverHost;
    }

}
