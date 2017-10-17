package com.blackducksoftware.integration.log;

public class SilentLogger extends IntLogger {
    @Override
    public void alwaysLog(final String arg0) {
    }

    @Override
    public void debug(final String arg0, final Throwable arg1) {
    }

    @Override
    public void debug(final String arg0) {
    }

    @Override
    public void error(final String arg0, final Throwable arg1) {
    }

    @Override
    public void error(final String arg0) {
    }

    @Override
    public void error(final Throwable arg0) {
    }

    @Override
    public LogLevel getLogLevel() {
        return LogLevel.OFF;
    }

    @Override
    public void info(final String arg0) {
    }

    @Override
    public void setLogLevel(final LogLevel arg0) {
    }

    @Override
    public void trace(final String arg0, final Throwable arg1) {
    }

    @Override
    public void trace(final String arg0) {
    }

    @Override
    public void warn(final String arg0) {
    }

}
