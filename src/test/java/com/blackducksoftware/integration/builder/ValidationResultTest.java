/*
 * Copyright (C) 2016 Black Duck Software Inc.
 * http://www.blackducksoftware.com/
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Black Duck Software ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Black Duck Software.
 */
package com.blackducksoftware.integration.builder;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ValidationResultTest {
    @Test
    public void testNonNullThrowableIsInString() {
        final ValidationResult validationResult = new ValidationResult(ValidationResultEnum.WARN, "Lookout, there's a TIGER BEHIND YOU!!!",
                new Exception("nobody wants to be eaten by a tiger"));
        final String validationResultString = validationResult.toString();
        assertFalse(validationResultString.contains("null"));
        assertTrue(validationResultString.contains("Lookout, there's a TIGER BEHIND YOU!!!"));
        assertTrue(validationResultString.contains("nobody wants to be eaten by a tiger"));
    }

    @Test
    public void testNullThrowableIsNotInString() {
        final ValidationResult validationResult = new ValidationResult(ValidationResultEnum.ERROR,
                "You did not heed the warning...you have been eaten by a tiger. Shame.");
        final String validationResultString = validationResult.toString();
        assertFalse(validationResultString.contains("null"));
        assertTrue(validationResultString.contains("You did not heed the warning...you have been eaten by a tiger. Shame."));
    }

}
