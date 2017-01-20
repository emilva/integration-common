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
package com.blackducksoftware.integration.hub.logging;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.blackducksoftware.integration.log.LogLevel;
import com.blackducksoftware.integration.test.TestLogger;
import com.blackducksoftware.integration.util.CIEnvironmentVariables;

public class IntLoggerTest {

    @Test
    public void testSetLogLevelWithVariables() {
        final TestLogger logger = new TestLogger();
        final CIEnvironmentVariables variables = new CIEnvironmentVariables();
        logger.setLogLevel(variables);
        assertEquals(LogLevel.INFO, logger.getLogLevel());

        variables.put("HUB_LOG_LEVEL", "FAKE");
        logger.setLogLevel(variables);
        assertEquals(LogLevel.INFO, logger.getLogLevel());

        variables.put("HUB_LOG_LEVEL", "error");
        logger.setLogLevel(variables);
        assertEquals(LogLevel.ERROR, logger.getLogLevel());

        variables.put("HUB_LOG_LEVEL", "erRor");
        logger.setLogLevel(variables);
        assertEquals(LogLevel.ERROR, logger.getLogLevel());

        variables.put("HUB_LOG_LEVEL", "OFF");
        logger.setLogLevel(variables);
        assertEquals(LogLevel.OFF, logger.getLogLevel());

        variables.put("HUB_LOG_LEVEL", "ERROR");
        logger.setLogLevel(variables);
        assertEquals(LogLevel.ERROR, logger.getLogLevel());

        variables.put("HUB_LOG_LEVEL", "WARN");
        logger.setLogLevel(variables);
        assertEquals(LogLevel.WARN, logger.getLogLevel());

        variables.put("HUB_LOG_LEVEL", "INFO");
        logger.setLogLevel(variables);
        assertEquals(LogLevel.INFO, logger.getLogLevel());

        variables.put("HUB_LOG_LEVEL", "DEBUG");
        logger.setLogLevel(variables);
        assertEquals(LogLevel.DEBUG, logger.getLogLevel());

        variables.put("HUB_LOG_LEVEL", "TRACE");
        logger.setLogLevel(variables);
        assertEquals(LogLevel.TRACE, logger.getLogLevel());
    }

}
