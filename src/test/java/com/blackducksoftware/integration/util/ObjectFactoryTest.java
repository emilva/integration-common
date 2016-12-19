package com.blackducksoftware.integration.util;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.blackducksoftware.integration.builder.ValidationResult;
import com.blackducksoftware.integration.builder.ValidationResultEnum;
import com.blackducksoftware.integration.exception.IntegrationException;

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
