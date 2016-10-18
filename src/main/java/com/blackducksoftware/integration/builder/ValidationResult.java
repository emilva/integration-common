/*******************************************************************************
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
 *******************************************************************************/
package com.blackducksoftware.integration.builder;

import org.apache.commons.lang3.builder.RecursiveToStringStyle;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class ValidationResult {
    private final ValidationResultEnum resultType;

    private final String message;

    private final Throwable throwable;

    public ValidationResult(final ValidationResultEnum resultType, final String message) {
        this.resultType = resultType;
        this.message = message;
        this.throwable = null;
    }

    public ValidationResult(final ValidationResultEnum resultType, final String message, final Throwable throwable) {
        this.resultType = resultType;
        this.message = message;
        this.throwable = throwable;
    }

    public ValidationResultEnum getResultType() {
        return resultType;
    }

    public String getMessage() {
        return message;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, RecursiveToStringStyle.JSON_STYLE);
    }

}
