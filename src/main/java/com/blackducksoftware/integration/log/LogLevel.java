/**
 * Integration Common
 *
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
 */
package com.blackducksoftware.integration.log;

import org.apache.commons.lang3.StringUtils;

/**
 * The declared order of the LogLevels are important - your set level is loggable as are all the levels to the left.
 * For example, if you set your LogLevel to INFO, INFO will be loggable, as will the levels to its left, WARN and ERROR.
 * As there are no levels to the left of OFF, nothing will be logged when that level is set.
 */
public enum LogLevel {
    OFF, ERROR, WARN, INFO, DEBUG, TRACE;

    /**
     * @deprecated
     *             Please use the instance method isLoggable(LogLevel logLevel) instead.
     */
    @Deprecated
    public static boolean isLoggable(final LogLevel logger, final LogLevel message) {
        return logger.isLoggable(message);
    }

    public static LogLevel fromString(final String level) {
        if (StringUtils.isNotBlank(level)) {
            try {
                return LogLevel.valueOf(level.toUpperCase());
            } catch (final IllegalArgumentException e) {
            }
        }
        return LogLevel.INFO;
    }

    /**
     * Will return true if logLevel is loggable for this logLevel, false otherwise.
     */
    public boolean isLoggable(final LogLevel logLevel) {
        return this.compareTo(logLevel) >= 0;
    }

}
