package com.blackducksoftware.integration.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;

public class ResourceUtil {

	public static InputStream getResourceAsStream(final String resourceName) throws IOException {
		return getResourceAsStream(null, resourceName);
	}

	public static InputStream getResourceAsStream(final Class clazz, final String resourceName) throws IOException {
		if (clazz == null) {
			return Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName);
		}
		return clazz.getResourceAsStream(resourceName);
	}

	public static String getResourceAsString(final String resourceName, final String encoding) throws IOException {
		return getResourceAsString(null, resourceName, Charsets.toCharset(encoding));
	}

	public static String getResourceAsString(final String resourceName, final Charset encoding) throws IOException {
		final InputStream inputStream = getResourceAsStream(resourceName);
		if (inputStream != null) {
			return IOUtils.toString(inputStream, encoding);
		}
		return null;
	}

	public static String getResourceAsString(final Class clazz, final String resourceName, final String encoding)
			throws IOException {
		return getResourceAsString(clazz, resourceName, Charsets.toCharset(encoding));
	}

	public static String getResourceAsString(final Class clazz, final String resourceName, final Charset encoding)
			throws IOException {
		final InputStream inputStream = getResourceAsStream(clazz, resourceName);
		if (inputStream != null) {
			return IOUtils.toString(inputStream, encoding);
		}
		return null;
	}

}
