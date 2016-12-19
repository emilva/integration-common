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
package com.blackducksoftware.integration.util;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.validator.ValidationResult;
import com.blackducksoftware.integration.validator.ValidationResultEnum;

public class ObjectFactoryTest {
    @Test
    public void testCreatingAnObject() throws IntegrationException {
        final Map<String, Object> objectProperties = new HashMap<>();
        objectProperties.put("resultType", ValidationResultEnum.WARN);
        objectProperties.put("message", "A test message supplied by reflection.");

        final ObjectFactory objectFactory = new ObjectFactory();
        final ValidationResult validationResult = objectFactory.createPopulatedInstance(ValidationResult.class, objectProperties);
        assertEquals("A test message supplied by reflection.", validationResult.getMessage());
        assertEquals(ValidationResultEnum.WARN, validationResult.getResultType());
        assertEquals(null, validationResult.getThrowable());
    }

}
