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

import org.apache.commons.lang3.StringUtils;

public abstract class AbstractValidator {
    public abstract ValidationResults assertValid();

    protected int stringToInteger(final String integer) throws IllegalArgumentException {
        final String integerString = StringUtils.trimToNull(integer);
        if (integerString != null) {
            try {
                return Integer.valueOf(integerString);
            } catch (final NumberFormatException e) {
                throw new IllegalArgumentException("The String : " + integer + " , is not an Integer.", e);
            }
        } else {
            throw new IllegalArgumentException("The String : " + integer + " , is not an Integer.");
        }
    }
}
