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
package com.blackducksoftware.integration.log;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class LogLevelTest {
    @Test
    public void testNothingIsLoggableIfLevelIsOff() {
        assertFalse(LogLevel.OFF.isLoggable(LogLevel.ERROR));
        assertFalse(LogLevel.OFF.isLoggable(LogLevel.WARN));
        assertFalse(LogLevel.OFF.isLoggable(LogLevel.INFO));
        assertFalse(LogLevel.OFF.isLoggable(LogLevel.DEBUG));
        assertFalse(LogLevel.OFF.isLoggable(LogLevel.TRACE));
    }

    @Test
    public void testIsLoggableIfLevelIsError() {
        assertTrue(LogLevel.ERROR.isLoggable(LogLevel.ERROR));
        assertFalse(LogLevel.ERROR.isLoggable(LogLevel.WARN));
        assertFalse(LogLevel.ERROR.isLoggable(LogLevel.INFO));
        assertFalse(LogLevel.ERROR.isLoggable(LogLevel.DEBUG));
        assertFalse(LogLevel.ERROR.isLoggable(LogLevel.TRACE));
    }

    @Test
    public void testIsLoggableIfLevelIsWarn() {
        assertTrue(LogLevel.WARN.isLoggable(LogLevel.ERROR));
        assertTrue(LogLevel.WARN.isLoggable(LogLevel.WARN));
        assertFalse(LogLevel.WARN.isLoggable(LogLevel.INFO));
        assertFalse(LogLevel.WARN.isLoggable(LogLevel.DEBUG));
        assertFalse(LogLevel.WARN.isLoggable(LogLevel.TRACE));
    }

    @Test
    public void testIsLoggableIfLevelIsInfo() {
        assertTrue(LogLevel.INFO.isLoggable(LogLevel.ERROR));
        assertTrue(LogLevel.INFO.isLoggable(LogLevel.WARN));
        assertTrue(LogLevel.INFO.isLoggable(LogLevel.INFO));
        assertFalse(LogLevel.INFO.isLoggable(LogLevel.DEBUG));
        assertFalse(LogLevel.INFO.isLoggable(LogLevel.TRACE));
    }

    @Test
    public void testIsLoggableIfLevelIsDebug() {
        assertTrue(LogLevel.DEBUG.isLoggable(LogLevel.ERROR));
        assertTrue(LogLevel.DEBUG.isLoggable(LogLevel.WARN));
        assertTrue(LogLevel.DEBUG.isLoggable(LogLevel.INFO));
        assertTrue(LogLevel.DEBUG.isLoggable(LogLevel.DEBUG));
        assertFalse(LogLevel.DEBUG.isLoggable(LogLevel.TRACE));
    }

    @Test
    public void testIsLoggableIfLevelIsTrace() {
        assertTrue(LogLevel.TRACE.isLoggable(LogLevel.ERROR));
        assertTrue(LogLevel.TRACE.isLoggable(LogLevel.WARN));
        assertTrue(LogLevel.TRACE.isLoggable(LogLevel.INFO));
        assertTrue(LogLevel.TRACE.isLoggable(LogLevel.DEBUG));
        assertTrue(LogLevel.TRACE.isLoggable(LogLevel.TRACE));
    }

}
