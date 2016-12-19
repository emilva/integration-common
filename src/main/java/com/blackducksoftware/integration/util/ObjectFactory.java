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

import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.reflect.FieldUtils;

import com.blackducksoftware.integration.exception.IntegrationException;

public class ObjectFactory {
    public static final ObjectFactory INSTANCE = new ObjectFactory();

    public <T> T createPopulatedInstance(final Class<T> clazz, final Map<String, Object> objectProperties) throws IntegrationException {
        T instance = null;
        try {
            instance = clazz.newInstance();
            for (final Entry<String, Object> entry : objectProperties.entrySet()) {
                FieldUtils.writeField(instance, entry.getKey(), entry.getValue(), true);
            }
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IntegrationException(String.format("Couldn't create the instance: ", e.getMessage()));
        }
        return instance;
    }

}
