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
