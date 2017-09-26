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

import java.io.PrintStream;

public class PrintStreamIntLogger extends IntLogger {
    private final PrintStream printStream;
    private LogLevel logLevel;

    public PrintStreamIntLogger(final PrintStream printStream, final LogLevel logLevel) {
        this.printStream = printStream;
        this.logLevel = logLevel;
    }

    @Override
    public void alwaysLog(final String txt) {
        printStream.println(txt);
    }

    @Override
    public void info(final String txt) {
        if (logLevel.isLoggable(LogLevel.INFO)) {
            printStream.println("INFO: " + txt);
        }
    }

    @Override
    public void error(final Throwable t) {
        if (logLevel.isLoggable(LogLevel.ERROR)) {
            t.printStackTrace(printStream);
        }
    }

    @Override
    public void error(final String txt, final Throwable t) {
        if (logLevel.isLoggable(LogLevel.ERROR)) {
            printStream.println("ERROR: " + txt);
            t.printStackTrace(printStream);
        }
    }

    @Override
    public void error(final String txt) {
        if (logLevel.isLoggable(LogLevel.ERROR)) {
            printStream.println("ERROR: " + txt);
        }
    }

    @Override
    public void warn(final String txt) {
        if (logLevel.isLoggable(LogLevel.WARN)) {
            printStream.println("WARN: " + txt);
        }
    }

    @Override
    public void trace(final String txt) {
        if (logLevel.isLoggable(LogLevel.TRACE)) {
            printStream.println("TRACE: " + txt);
        }
    }

    @Override
    public void trace(final String txt, final Throwable t) {
        if (logLevel.isLoggable(LogLevel.TRACE)) {
            printStream.println("TRACE: " + txt);
            t.printStackTrace(printStream);
        }
    }

    @Override
    public void debug(final String txt) {
        if (logLevel.isLoggable(LogLevel.DEBUG)) {
            printStream.println("DEBUG: " + txt);
        }
    }

    @Override
    public void debug(final String txt, final Throwable t) {
        if (logLevel.isLoggable(LogLevel.DEBUG)) {
            printStream.println("DEBUG: " + txt);
            t.printStackTrace(printStream);
        }
    }

    @Override
    public void setLogLevel(final LogLevel logLevel) {
        this.logLevel = logLevel;
    }

    @Override
    public LogLevel getLogLevel() {
        return logLevel;
    }

}
