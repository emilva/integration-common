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
