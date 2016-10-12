package com.blackducksoftware.integration.hub.util;

import static org.junit.Assert.assertNotNull;

import java.nio.charset.StandardCharsets;

import org.junit.Test;

import com.blackducksoftware.integration.util.ResourceUtil;

public class ResourceUtilTest {

	private static final String TEST_RESOURCE = "test-resource.txt";

	@Test
	public void testReturnsValues() throws Exception {
		assertNotNull(ResourceUtil.getResourceAsStream(TEST_RESOURCE));
		assertNotNull(ResourceUtil.getResourceAsStream(getClass(), TEST_RESOURCE));
		assertNotNull(ResourceUtil.getResourceAsString(TEST_RESOURCE, StandardCharsets.UTF_8));
		assertNotNull(ResourceUtil.getResourceAsString(TEST_RESOURCE, "UTF-8"));
		assertNotNull(ResourceUtil.getResourceAsString(getClass(), TEST_RESOURCE, StandardCharsets.UTF_8));
		assertNotNull(ResourceUtil.getResourceAsString(getClass(), TEST_RESOURCE, "UTF-8"));
	}

}
