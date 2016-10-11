package com.blackducksoftware.integration.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;

public class ResourceUtil {

	public static String getResourceAsString(final Class clazz, final String resourceName, final Charset encoding)
			throws IOException {
		return getResourceAsString(clazz, resourceName, encoding.name());
	}

	public static String getResourceAsString(final Class clazz, final String resourceName, final String encoding)
			throws IOException {
		final InputStream inputStream = clazz.getResourceAsStream(resourceName);
		if (inputStream != null) {
			return IOUtils.toString(inputStream, encoding);
		}
		return null;
	}

}
