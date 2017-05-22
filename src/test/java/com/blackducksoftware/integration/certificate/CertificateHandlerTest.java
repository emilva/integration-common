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

import java.net.URL;
import java.security.cert.Certificate;

import org.apache.commons.lang3.StringUtils;
import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.log.IntLogger;
import com.blackducksoftware.integration.log.LogLevel;
import com.blackducksoftware.integration.log.PrintStreamIntLogger;

public class CertificateHandlerTest {

    private static final IntLogger logger = new PrintStreamIntLogger(System.out, LogLevel.TRACE);

    private static final CertificateHandler CERT_HANDLER = new CertificateHandler(logger);

    private static URL url;

    private static Certificate originalCertificate;

    @BeforeClass
    public static void init() throws Exception {
        final String urlString = System.getProperty("HTTPS_URL");
        // assumeTrue expects the condition to be true, if it is not then it skips the test
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
        final CertificateHandler certificateHandler = new CertificateHandler(logger);
        final Certificate output = certificateHandler.retrieveHttpsCertificateFromURL(url);
        assertNotNull(output);
    }

    @Test
    public void testRetrieveAndImportHttpsCertificate() throws Exception {
        final CertificateHandler certificateHandler = new CertificateHandler(logger);
        certificateHandler.retrieveAndImportHttpsCertificate(url);
        assertTrue(certificateHandler.isCertificateInTrustStore(url));
        assertNotNull(certificateHandler.retrieveHttpsCertificateFromTrustStore(url));
        certificateHandler.removeHttpsCertificate(url);
        assertFalse(certificateHandler.isCertificateInTrustStore(url));
    }

}
