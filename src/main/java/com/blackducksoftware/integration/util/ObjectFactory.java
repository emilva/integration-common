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
