/*******************************************************************************
 * Copyright (C) 2016 Black Duck Software, Inc.
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
 *******************************************************************************/
package com.blackducksoftware.integration.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;

public class ResourceUtil {
    public static InputStream getResourceAsStream(final Class<?> clazz, final String resourceName) throws IOException {
        return clazz.getClassLoader().getResourceAsStream(resourceName);
    }

    public static InputStream getResourceAsStream(final String resourceName) throws IOException {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName);
    }

    public static String getResourceAsString(final String resourceName, final String encoding) throws IOException {
        return getResourceAsString(resourceName, Charsets.toCharset(encoding));
    }

    public static String getResourceAsString(final String resourceName, final Charset encoding) throws IOException {
        final InputStream inputStream = getResourceAsStream(resourceName);
        if (inputStream != null) {
            return IOUtils.toString(inputStream, encoding);
        }
        return null;
    }

    public static String getResourceAsString(final Class<?> clazz, final String resourceName, final String encoding)
            throws IOException {
        return getResourceAsString(clazz, resourceName, Charsets.toCharset(encoding));
    }

    public static String getResourceAsString(final Class<?> clazz, final String resourceName, final Charset encoding)
            throws IOException {
        final InputStream inputStream = getResourceAsStream(clazz, resourceName);
        if (inputStream != null) {
            return IOUtils.toString(inputStream, encoding);
        }
        return null;
    }

}
