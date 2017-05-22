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

import org.apache.commons.lang3.StringUtils;

import com.blackducksoftware.integration.exception.IntegrationCertificateException;
import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.log.IntLogger;

public class CertificateHandler {
    private final IntLogger logger;

    private String keyStoreType = KeyStore.getDefaultType();

    private char[] keyStorePass = { 'c', 'h', 'a', 'n', 'g', 'e', 'i', 't' };

    public CertificateHandler(final IntLogger intLogger) {
        this(intLogger, null, null);
    }

    public CertificateHandler(final IntLogger intLogger, final String keyStoreType, final String keyStorePass) {
        logger = intLogger;
        if (StringUtils.isNotBlank(keyStoreType)) {
            this.keyStoreType = keyStoreType;
        }
        if (StringUtils.isNotBlank(keyStorePass)) {
            this.keyStorePass = keyStorePass.toCharArray();
        }
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
        final String serverHost = getServerHost(url);
        logger.info(String.format("Retrieving the certificate from %s", serverHost));
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

    public void importHttpsCertificate(final URL url, final Certificate certificate)
            throws IntegrationException {
        final File jssecacerts = getJssecacerts(getJavaHome());
        final String jssecacertsPath = jssecacerts.getAbsolutePath();
        logger.info(String.format("Importing the certificate from %s into keystore %s", url.getHost(), jssecacertsPath));
        try {
            final KeyStore keyStore = getKeyStore(jssecacerts);
            keyStore.setCertificateEntry(url.getHost(), certificate);
            try (OutputStream stream = new BufferedOutputStream(new FileOutputStream(jssecacerts))) {
                keyStore.store(stream, keyStorePass);
            }
        } catch (final Exception e) {
            throw new IntegrationException(e);
        }
    }

    public void removeHttpsCertificate(final URL url) throws IntegrationException {
        final File jssecacerts = getJssecacerts(getJavaHome());
        final String jssecacertsPath = jssecacerts.getAbsolutePath();
        logger.info(String.format("Removing the certificate from %s", jssecacertsPath));
        try {
            final KeyStore keyStore = getKeyStore(jssecacerts);
            if (keyStore.containsAlias(url.getHost())) {
                keyStore.deleteEntry(url.getHost());
                try (OutputStream stream = new BufferedOutputStream(new FileOutputStream(jssecacerts))) {
                    keyStore.store(stream, keyStorePass);
                }
            }
        } catch (final Exception e) {
            throw new IntegrationException(e);
        }
    }

    public boolean isCertificateInKeystore(final URL url) throws IntegrationException {
        final File jssecacerts = getJssecacerts(getJavaHome());
        if (!jssecacerts.exists()) {
            return false;
        }
        final String jssecacertsPath = jssecacerts.getAbsolutePath();
        logger.info(String.format("Checking for alias %s in keystore %s", url.getHost(), jssecacertsPath));
        try {
            final KeyStore keyStore = getKeyStore(jssecacerts);
            return keyStore.containsAlias(url.getHost());
        } catch (final Exception e) {
            throw new IntegrationException(e);
        }
    }

    public Certificate getHttpsCertificateFromKeyStore(final URL url)
            throws IntegrationException {
        final File jssecacerts = getJssecacerts(getJavaHome());
        final String jssecacertsPath = jssecacerts.getAbsolutePath();
        logger.info(String.format("Removing the certificate from %s", jssecacertsPath));
        try {
            final KeyStore keyStore = getKeyStore(jssecacerts);
            if (keyStore.containsAlias(url.getHost())) {
                return keyStore.getCertificate(url.getHost());
            }
        } catch (final Exception e) {
            throw new IntegrationException(e);
        }
        return null;
    }

    private KeyStore getKeyStore(final File jssecacerts)
            throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
        if (jssecacerts.exists()) {
            final PasswordProtection protection = new PasswordProtection(keyStorePass);
            return KeyStore.Builder.newInstance(keyStoreType, null, jssecacerts, protection).getKeyStore();
        }
        final KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(null, null);
        try (OutputStream stream = new BufferedOutputStream(new FileOutputStream(jssecacerts))) {
            // to create a valid empty keystore file
            keyStore.store(stream, keyStorePass);
        }
        return keyStore;
    }

    private String getServerHost(final URL url) {
        String serverHost = url.getHost();
        if (url.getPort() > 0) {
            serverHost += ":" + url.getPort();
        }
        return serverHost;
    }

    private String getJavaHome() {
        return System.getProperty("java.home");
    }

    private File getJssecacerts(final String javaHome) {
        File jssecacerts = new File(javaHome);
        jssecacerts = new File(jssecacerts, "lib");
        jssecacerts = new File(jssecacerts, "security");
        jssecacerts = new File(jssecacerts, "jssecacerts");
        return jssecacerts;
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
