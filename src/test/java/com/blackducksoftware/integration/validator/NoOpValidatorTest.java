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
package com.blackducksoftware.integration.validator;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class NoOpValidatorTest {

    @Test
    public void testNoOpValidator() throws Exception {
        final NoOpValidator validator = new NoOpValidator();
        final ValidationResults results = validator.assertValid();
        assertTrue(results.isSuccess());
    }
}
