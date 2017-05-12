/*
 * Copyright (C) 2017 Black Duck Software Inc.
 * http://www.blackducksoftware.com/
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Black Duck Software ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Black Duck Software.
 */
package com.blackducksoftware.integration.certificate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.apache.commons.lang3.StringUtils;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.log.IntLogger;

public class CertificateHandler {

    private final IntLogger logger;

    public CertificateHandler(final IntLogger intLogger) {
        logger = intLogger;
    }

    public void retrieveAndImportHttpsCertificate(final URL url) throws IntegrationException {
        retrieveAndImportHttpsCertificate(url, null);
    }

    public void retrieveAndImportHttpsCertificate(final URL url, final String optionalKeyStorePass) throws IntegrationException {
        final File certificate = new File("temporaryCertificate.txt");
        try {
            certificate.createNewFile();
            retrieveAndSaveHttpsCertificate(url, certificate);
            importHttpsCertificateFromFile(url, certificate, optionalKeyStorePass);
        } catch (final IntegrationException e) {
            throw e;
        } catch (final Exception e) {
            throw new IntegrationException(e.getMessage(), e);
        } finally {
            certificate.delete();
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
            if (StringUtils.isNotBlank(errorOutput)) {
                logger.warn(errorOutput);
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

    public void importHttpsCertificateFromFile(final URL url, final File certificate) throws IntegrationException {
        importHttpsCertificateFromFile(url, certificate, null);
    }

    public void importHttpsCertificateFromFile(final URL url, final File certificate, String optionalKeyStorePass) throws IntegrationException {
        logger.info(String.format("Importing the certificate from %s", certificate.getAbsolutePath()));
        final String javaHome = System.getProperty("java.home");
        File jssecacerts = new File(javaHome);
        jssecacerts = new File(jssecacerts, "lib");
        jssecacerts = new File(jssecacerts, "security");
        jssecacerts = new File(jssecacerts, "jssecacerts");
        final String keyStore = jssecacerts.getAbsolutePath();
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
            if (StringUtils.isNotBlank(output)) {
                if (exitCode != 0) {
                    logger.error(output);
                } else {
                    logger.info(output);
                }
            }
            if (StringUtils.isNotBlank(errorOutput)) {
                logger.warn(errorOutput);
            }
            logger.debug(String.format("Exit code %d", exitCode));
            if (exitCode != 0) {
                throw new IntegrationException(String.format("Failed to import the certificate into %s", keyStore));
            }
        } catch (final Exception e) {
            throw new IntegrationException(e);
        }
    }

    private String readInputStream(final InputStream stream) throws IOException {
        try (final BufferedReader outputReader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            final StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = outputReader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append(System.lineSeparator());
            }
            return stringBuilder.toString();
        }
    }

    private String getServerHost(final URL url) {
        String serverHost = url.getHost();
        if (url.getPort() > 0) {
            serverHost += ":" + url.getPort();
        }
        return serverHost;
    }

}
