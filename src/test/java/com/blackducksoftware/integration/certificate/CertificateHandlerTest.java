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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URL;
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
import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.log.IntLogger;
import com.blackducksoftware.integration.log.LogLevel;
import com.blackducksoftware.integration.log.PrintStreamIntLogger;

public class CertificateHandlerTest {
    private static final IntLogger logger = new PrintStreamIntLogger(System.out, LogLevel.TRACE);

    private static final CertificateHandler CERT_HANDLER = getCertificateHandler(logger, null);

    private static URL url;

    private static Certificate originalCertificate;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @BeforeClass
    public static void init() throws Exception {
        final String urlString = System.getProperty("hub.https.url");
        // assumeTrue expects the condition to be true, if it is not then it
        // skips the test
        Assume.assumeTrue(StringUtils.isNotBlank(urlString));
        url = new URL(urlString);
        try {
            final boolean isCertificateInKeystore = CERT_HANDLER.isCertificateInTrustStore(url);
            if (isCertificateInKeystore) {
                originalCertificate = CERT_HANDLER.retrieveHttpsCertificateFromTrustStore(url);
                CERT_HANDLER.removeHttpsCertificate(url);
            } else {
                logger.error(String.format("Certificate for %s is not in the keystore.", url.getHost()));
            }
        } catch (final IntegrationException e) {
            logger.error(e.getMessage());
        }
    }

    @AfterClass
    public static void tearDown() throws Exception {
        if (originalCertificate != null) {
            CERT_HANDLER.importHttpsCertificate(url, originalCertificate);
        }
    }

    @Test
    public void testCertificateRetrieval() throws Exception {
        final CertificateHandler certificateHandler = getCertificateHandler(logger, null);
        final Certificate output = certificateHandler.retrieveHttpsCertificateFromURL(url);
        assertNotNull(output);
    }

    @Test
    public void testRetrieveAndImportHttpsCertificate() throws Exception {
        final CertificateHandler certificateHandler = getCertificateHandler(logger, null);
        certificateHandler.retrieveAndImportHttpsCertificate(url);
        assertTrue(certificateHandler.isCertificateInTrustStore(url));
        assertNotNull(certificateHandler.retrieveHttpsCertificateFromTrustStore(url));
        certificateHandler.removeHttpsCertificate(url);
        assertFalse(certificateHandler.isCertificateInTrustStore(url));
    }

    @Test
    public void testKeystoreSetBySystemProperty() throws Exception {
        final File tmpTrustStore = folder.newFile();
        assertTrue(tmpTrustStore.length() == 0);
        try {
            System.setProperty("javax.net.ssl.trustStore", tmpTrustStore.getAbsolutePath());
            final CertificateHandler certificateHandler = getCertificateHandler(logger, null);
            certificateHandler.retrieveAndImportHttpsCertificate(url);
            assertTrue(certificateHandler.isCertificateInTrustStore(url));
            assertNotNull(certificateHandler.retrieveHttpsCertificateFromTrustStore(url));
            assertTrue(tmpTrustStore.isFile());
            assertTrue(tmpTrustStore.length() > 0);
        } finally {
            if (tmpTrustStore.exists()) {
                tmpTrustStore.delete();
            }
        }
    }

    @Test
    public void testRetrieveAndImportHttpsCertificateForSpecificJavaHome() throws Exception {
        final String javaHomeToManipulate = System.getProperty("JAVA_TO_MANIPULATE");
        Assume.assumeTrue(StringUtils.isNotBlank(javaHomeToManipulate));

        final CertificateHandler certificateHandlerDefault = getCertificateHandler(logger, null);
        final CertificateHandler certificateHandler = getCertificateHandler(logger, new File(javaHomeToManipulate));

        Certificate original = null;
        if (certificateHandler.isCertificateInTrustStore(url)) {
            original = certificateHandler.retrieveHttpsCertificateFromTrustStore(url);
            certificateHandler.removeHttpsCertificate(url);
        }

        try {
            assertFalse(certificateHandler.isCertificateInTrustStore(url));
            assertFalse(certificateHandlerDefault.isCertificateInTrustStore(url));

            certificateHandler.retrieveAndImportHttpsCertificate(url);

            assertTrue(certificateHandler.isCertificateInTrustStore(url));
            assertFalse(certificateHandlerDefault.isCertificateInTrustStore(url));

            certificateHandler.removeHttpsCertificate(url);
        } finally {
            if (original != null) {
                certificateHandler.importHttpsCertificate(url, original);
            }
        }
    }

    private static CertificateHandler getCertificateHandler(final IntLogger logger, final File javaToManipulate) throws RuntimeException {
        final CertificateHandler certificateHandler = new CertificateHandler(logger, javaToManipulate) {
            @Override
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

        };
        return certificateHandler;
    }

}
