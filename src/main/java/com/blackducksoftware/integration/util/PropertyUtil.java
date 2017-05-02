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
package com.blackducksoftware.integration.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.blackducksoftware.integration.exception.IntegrationException;

public class PropertyUtil {
    public void setPropertyUsingSetter(final Object instance, final String propertyFieldName, final String propertyValue) throws IntegrationException {
        final String setterName = "set" + StringUtils.capitalize(propertyFieldName);
        final Method[] methods = instance.getClass().getMethods();
        for (final Method method : methods) {
            if (method.getName().equals(setterName)) {
                final Class<?>[] parameters = method.getParameterTypes();
                if (parameters.length == 1) {
                    final String parameterName = parameters[0].getName();
                    try {
                        if ("java.lang.String".equals(parameterName)) {
                            method.invoke(instance, propertyValue);
                        } else if ("int".equals(parameterName)) {
                            method.invoke(instance, NumberUtils.toInt(propertyValue));
                        } else if ("long".equals(parameterName)) {
                            method.invoke(instance, NumberUtils.toLong(propertyValue));
                        } else if ("short".equals(parameterName)) {
                            method.invoke(instance, NumberUtils.toShort(propertyValue));
                        } else if ("double".equals(parameterName)) {
                            method.invoke(instance, NumberUtils.toDouble(propertyValue));
                        } else if ("float".equals(parameterName)) {
                            method.invoke(instance, NumberUtils.toFloat(propertyValue));
                        } else if ("boolean".equals(parameterName)) {
                            method.invoke(instance, Boolean.parseBoolean(propertyValue));
                        } else if ("char".equals(parameterName) && StringUtils.isNotEmpty(propertyValue)) {
                            method.invoke(instance, propertyValue.toCharArray()[0]);
                        }
                    } catch (final InvocationTargetException | IllegalAccessException e) {
                        throw new IntegrationException(String.format("Could not invoke %s with %s: %s", method.getName(), propertyValue, e.getMessage()));
                    }
                }
            }
        }
    }

}
