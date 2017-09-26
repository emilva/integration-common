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
package com.blackducksoftware.integration.validator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

public class ValidationResultsTest {
    private static final String TEST_MESSAGE_PREFIX = "Test Message ";

    private ValidationResults createTestData(final List<ValidationResultEnum> resultTypeList) {
        final ValidationResults results = new ValidationResults();
        final int count = resultTypeList.size();
        for (int index = 0; index < count; index++) {
            final String message = TEST_MESSAGE_PREFIX + index;

            final ValidationResult result = new ValidationResult(resultTypeList.get(index), message);
            results.addResult(TestField.KEY_0, result);
        }

        return results;
    }

    @Test
    public void testValidationResultConstructor() {
        final Throwable throwable = new RuntimeException();
        final ValidationResult result = new ValidationResult(ValidationResultEnum.ERROR, TEST_MESSAGE_PREFIX, throwable);

        assertNotNull(result);
        assertEquals(result.getResultType(), ValidationResultEnum.ERROR);
        assertEquals(result.getMessage(), TEST_MESSAGE_PREFIX);
        assertEquals(result.getThrowable(), throwable);
    }

    @Test
    public void testValidationResultsConstructor() {
        final ValidationResults result = new ValidationResults();
        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertTrue(result.isSuccess());
    }

    @Test
    public void testAddResult() {
        final List<ValidationResultEnum> items = new ArrayList<>();
        items.add(ValidationResultEnum.WARN);
        items.add(ValidationResultEnum.ERROR);

        final ValidationResults results = createTestData(items);

        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertFalse(results.isSuccess());
        assertTrue(results.hasErrors());
        assertTrue(results.hasWarnings());
    }

    @Test
    public void testWarnings() {
        final List<ValidationResultEnum> items = new ArrayList<>();
        items.add(ValidationResultEnum.WARN);
        items.add(ValidationResultEnum.WARN);
        items.add(ValidationResultEnum.WARN);

        final ValidationResults results = createTestData(items);

        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertFalse(results.isSuccess());
        assertTrue(results.hasWarnings());
    }

    @Test
    public void testErrors() {
        final List<ValidationResultEnum> items = new ArrayList<>();
        items.add(ValidationResultEnum.ERROR);
        items.add(ValidationResultEnum.ERROR);
        items.add(ValidationResultEnum.ERROR);

        final ValidationResults results = createTestData(items);

        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertFalse(results.isSuccess());
        assertTrue(results.hasErrors());
    }

    @Test
    public void testGetResultStringWithEnum() {

        final List<ValidationResultEnum> items = new ArrayList<>();
        items.add(ValidationResultEnum.ERROR);
        items.add(ValidationResultEnum.WARN);
        final ValidationResults results = createTestData(items);

        assertNotNull(results);
        final String anotherMsg = "Test ERROR Message";
        results.addResult(TestField.KEY_0, new ValidationResult(ValidationResultEnum.ERROR, anotherMsg));
        results.addResult(TestField.KEY_0, new ValidationResult(ValidationResultEnum.ERROR, anotherMsg));
        final String warningMessage = TEST_MESSAGE_PREFIX + "WARNING";
        results.addResult(TestField.KEY_1, new ValidationResult(ValidationResultEnum.WARN, warningMessage));
        final String message = results.getResultString(TestField.KEY_1);

        assertTrue(StringUtils.isNotBlank(message));
        assertTrue(StringUtils.contains(message, warningMessage));
    }

    @Test
    public void testGetResultStringEnumInvalidKey() {
        final List<ValidationResultEnum> items = new ArrayList<>();
        items.add(ValidationResultEnum.ERROR);
        items.add(ValidationResultEnum.WARN);
        items.add(ValidationResultEnum.ERROR);

        final ValidationResults results = createTestData(items);

        assertNotNull(results);
        final String message = results.getResultString(TestField.KEY_1);

        assertTrue(StringUtils.isBlank(message));
    }

    @Test
    public void testGetConstructedObject() {
        final List<ValidationResultEnum> items = new ArrayList<>();
        items.add(ValidationResultEnum.ERROR);
        items.add(ValidationResultEnum.WARN);
        items.add(ValidationResultEnum.ERROR);

        final ValidationResults results = createTestData(items);

        assertNotNull(results);
    }

    @Test
    public void testValidationStatus() {
        List<ValidationResultEnum> items = new ArrayList<>();
        items.add(ValidationResultEnum.ERROR);
        items.add(ValidationResultEnum.ERROR);
        items.add(ValidationResultEnum.ERROR);
        items.add(ValidationResultEnum.ERROR);

        ValidationResults results = createTestData(items);

        assertNotNull(results);
        Set<ValidationResultEnum> status = results.getValidationStatus();

        assertEquals(status.size(), 1);

        items = new ArrayList<>();
        items.add(ValidationResultEnum.ERROR);
        items.add(ValidationResultEnum.ERROR);
        items.add(ValidationResultEnum.WARN);
        items.add(ValidationResultEnum.WARN);
        items.add(ValidationResultEnum.WARN);
        items.add(ValidationResultEnum.ERROR);

        results = createTestData(items);
        assertNotNull(results);
        status = results.getValidationStatus();

        assertEquals(status.size(), 2);
        assertTrue(status.contains(ValidationResultEnum.ERROR));
        assertTrue(status.contains(ValidationResultEnum.WARN));
    }

    @Test
    public void testStringOutput() {
        final String expected = "HUBURL = ERROR,Can not reach this server,java.io.IOException: Can not reach this server , ERROR,File does not exist,java.io.IOException: File does not exist" + System.lineSeparator()
                + "SCANTARGET = ERROR,File does not exist,java.io.IOException: File does not exist";

        final ValidationResults results = new ValidationResults();
        final ValidationResult result = new ValidationResult(ValidationResultEnum.ERROR, "Can not reach this server", new IOException("Can not reach this server"));
        final ValidationResult result2 = new ValidationResult(ValidationResultEnum.ERROR, "File does not exist", new IOException("File does not exist"));
        results.addResult(TestField.HUBURL, result);
        results.addResult(TestField.HUBURL, result);
        results.addResult(TestField.HUBURL, result2);
        results.addResult(TestField.SCANTARGET, result2);

        final String e = results.getAllResultString();
        System.out.println(expected);
        System.out.println();
        System.out.println(e);
        assertEquals(expected, results.getAllResultString());

    }

    @Test
    public void testNonNullThrowableIsInString() {
        final ValidationResult validationResult = new ValidationResult(ValidationResultEnum.WARN, "Lookout, there's a TIGER BEHIND YOU!!!", new Exception("nobody wants to be eaten by a tiger"));
        final String validationResultString = validationResult.toString();
        assertFalse(validationResultString.contains("null"));
        assertTrue(validationResultString.contains("Lookout, there's a TIGER BEHIND YOU!!!"));
        assertTrue(validationResultString.contains("nobody wants to be eaten by a tiger"));
    }

    @Test
    public void testNullThrowableIsNotInString() {
        final ValidationResult validationResult = new ValidationResult(ValidationResultEnum.ERROR, "You did not heed the warning...you have been eaten by a tiger. Shame.");
        final String validationResultString = validationResult.toString();
        assertFalse(validationResultString.contains("null"));
        assertTrue(validationResultString.contains("You did not heed the warning...you have been eaten by a tiger. Shame."));
    }

    private static enum TestField implements FieldEnum {
        HUBURL("HubURL"),
        SCANTARGET("ScanTarget"),
        KEY_0("Key0"),
        KEY_1("Key1");

        private final String key;

        private TestField(final String key) {
            this.key = key;
        }

        @Override
        public String getKey() {
            return key;
        }
    }
}
