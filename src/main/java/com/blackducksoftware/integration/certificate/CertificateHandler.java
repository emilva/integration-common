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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStore.PasswordProtection;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.blackducksoftware.integration.exception.IntegrationCertificateException;
import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.log.IntLogger;

public class CertificateHandler {
    private final IntLogger logger;

    private File javaHomeOverride;

    public CertificateHandler(final IntLogger intLogger) {
        logger = intLogger;
    }

    public CertificateHandler(final IntLogger intLogger, final File javaHomeOverride) {
        this(intLogger);
        this.javaHomeOverride = javaHomeOverride;
    }

    public void retrieveAndImportHttpsCertificate(final URL url) throws IntegrationException {
        try {
            final Certificate certificate = retrieveHttpsCertificateFromURL(url);
            if (certificate == null) {
                throw new IntegrationCertificateException(String.format("Could not retrieve the Certificate from %s", url));
            }
            importHttpsCertificate(url, certificate);
        } catch (final IntegrationException e) {
            throw e;
        } catch (final Exception e) {
            throw new IntegrationException(e.getMessage(), e);
        }
    }

    public Certificate retrieveHttpsCertificateFromURL(final URL url) throws IntegrationException {
        logger.info(String.format("Retrieving the certificate from %s", url));
        Certificate certificate = null;
        try {
            final SSLContext sslCtx = SSLContext.getInstance("TLS");
            sslCtx.init(null, getTrustManagers(), null);
            final HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setHostnameVerifier(getHostNameVerifier());
            connection.setSSLSocketFactory(sslCtx.getSocketFactory());
            if (connection.getResponseCode() == 200) {
                final Certificate[] certificates = connection.getServerCertificates();
                if (certificates != null && certificates.length > 0) {
                    certificate = certificates[0];
                }
            }
            connection.disconnect();
        } catch (

        final Exception e) {
            throw new IntegrationException(e);
        }
        return certificate;
    }

    public Certificate retrieveHttpsCertificateFromTrustStore(final URL url) throws IntegrationException {
        final File trustStore = getTrustStore();
        final String trustStorePath = trustStore.getAbsolutePath();
        logger.info(String.format("Removing the certificate from %s", trustStorePath));
        try {
            final KeyStore keyStore = getKeyStore(trustStore);
            if (keyStore.containsAlias(url.getHost())) {
                return keyStore.getCertificate(url.getHost());
            }
        } catch (final Exception e) {
            throw new IntegrationException(e);
        }
        return null;
    }

    public void importHttpsCertificate(final URL url, final Certificate certificate) throws IntegrationException {
        final File trustStore = getTrustStore();
        final String trustStorePath = trustStore.getAbsolutePath();
        logger.info(String.format("Importing the certificate from %s into keystore %s", url.getHost(), trustStorePath));
        try {
            final KeyStore keyStore = getKeyStore(trustStore);
            keyStore.setCertificateEntry(url.getHost(), certificate);
            try (OutputStream stream = new BufferedOutputStream(new FileOutputStream(trustStore))) {
                keyStore.store(stream, getKeyStorePassword());
            }
        } catch (final Exception e) {
            throw new IntegrationException(e);
        }
    }

    public void removeHttpsCertificate(final URL url) throws IntegrationException {
        final File trustStore = getTrustStore();
        final String trustStorePath = trustStore.getAbsolutePath();
        logger.info(String.format("Removing the certificate from %s", trustStorePath));
        try {
            final KeyStore keyStore = getKeyStore(trustStore);
            if (keyStore.containsAlias(url.getHost())) {
                keyStore.deleteEntry(url.getHost());
                try (OutputStream stream = new BufferedOutputStream(new FileOutputStream(trustStore))) {
                    keyStore.store(stream, getKeyStorePassword());
                }
            }
        } catch (final Exception e) {
            throw new IntegrationException(e);
        }
    }

    public boolean isCertificateInTrustStore(final URL url) throws IntegrationException {
        final File trustStore = getTrustStore();
        if (!trustStore.isFile()) {
            return false;
        }
        final String jssecacertsPath = trustStore.getAbsolutePath();
        logger.info(String.format("Checking for alias %s in keystore %s", url.getHost(), jssecacertsPath));
        try {
            final KeyStore keyStore = getKeyStore(trustStore);
            return keyStore.containsAlias(url.getHost());
        } catch (final Exception e) {
            throw new IntegrationException(e);
        }
    }

    private KeyStore getKeyStore(final File trustStore) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
        // trustStore must be an existing file and it must not be empty,
        // otherwise we create a new empty keystore
        if (trustStore.isFile() && trustStore.length() > 0) {
            final PasswordProtection protection = new PasswordProtection(getKeyStorePassword());
            return KeyStore.Builder.newInstance(getTrustStoreType(), null, trustStore, protection).getKeyStore();
        }
        final KeyStore keyStore = KeyStore.getInstance(getTrustStoreType());
        keyStore.load(null, null);
        try (OutputStream stream = new BufferedOutputStream(new FileOutputStream(trustStore))) {
            // to create a valid empty keystore file
            keyStore.store(stream, getKeyStorePassword());
        }
        return keyStore;
    }

    private String getTrustStoreType() {
        return System.getProperty("javax.net.ssl.trustStoreType", KeyStore.getDefaultType());
    }

    private char[] getKeyStorePassword() {
        return System.getProperty("javax.net.ssl.trustStorePassword", "changeit").toCharArray();
    }

    private File getTrustStore() {
        File trustStore;
        if (javaHomeOverride != null) {
            trustStore = resolveTrustStoreFile(javaHomeOverride);
        } else {
            trustStore = new File(System.getProperty("javax.net.ssl.trustStore", ""));
            if (!trustStore.isFile()) {
                final File javaHome = new File(System.getProperty("java.home"));
                trustStore = resolveTrustStoreFile(javaHome);
            }
        }

        return trustStore;
    }

    private File resolveTrustStoreFile(final File javaHome) {
        // first check for jssecacerts
        File trustStoreFile = new File(javaHome, "lib");
        trustStoreFile = new File(trustStoreFile, "security");
        trustStoreFile = new File(trustStoreFile, "jssecacerts");

        // if we can't find jssecacerts, look for cacerts
        if (!trustStoreFile.isFile()) {
            trustStoreFile = new File(javaHome, "lib");
            trustStoreFile = new File(trustStoreFile, "security");
            trustStoreFile = new File(trustStoreFile, "cacerts");
        }

        return trustStoreFile;
    }

    private TrustManager[] getTrustManagers() {
        return new TrustManager[] { new X509TrustManager() {
            private X509Certificate[] accepted;

            @Override
            public void checkClientTrusted(final java.security.cert.X509Certificate[] xcs, final String string) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(final java.security.cert.X509Certificate[] xcs, final String string) throws CertificateException {
                accepted = xcs;
            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return accepted;
            }
        } };
    }

    private HostnameVerifier getHostNameVerifier() {
        return new HostnameVerifier() {
            @Override
            public boolean verify(final String string, final SSLSession ssls) {
                return true;
            }
        };
    }

}
