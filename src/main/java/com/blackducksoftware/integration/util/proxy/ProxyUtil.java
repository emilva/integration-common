/*
 * Copyright (C) 2016 Black Duck Software Inc.
 * http://www.blackducksoftware.com/
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Black Duck Software ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Black Duck Software.
 */
package com.blackducksoftware.integration.util.proxy;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class ProxyUtil {

    /**
     * Checks the list of user defined host names that should be connected to
     * directly and not go through the proxy. If the hostToMatch matches any of
     * these hose names then this method returns true.
     *
     */
    public static boolean shouldIgnoreHost(final String hostToMatch, final List<Pattern> ignoredProxyHostPatterns) {
        if (StringUtils.isBlank(hostToMatch) || ignoredProxyHostPatterns == null
                || ignoredProxyHostPatterns.isEmpty()) {
            return false;
        }

        for (final Pattern ignoredProxyHostPattern : ignoredProxyHostPatterns) {
            final Matcher m = ignoredProxyHostPattern.matcher(hostToMatch);
            return m.find();
        }
        return false;
    }

    public static List<Pattern> getIgnoredProxyHostPatterns(String ignoredProxyHosts) {
        final List<Pattern> ignoredProxyHostPatterns = new ArrayList<>();
        if (StringUtils.isNotBlank(ignoredProxyHosts)) {
            String[] ignoreHosts = null;
            if (ignoredProxyHosts.contains(",")) {
                ignoreHosts = ignoredProxyHosts.split(",");
                for (final String ignoreHost : ignoreHosts) {
                    final Pattern pattern = Pattern.compile(ignoreHost.trim());
                    ignoredProxyHostPatterns.add(pattern);
                }
            } else {
                final Pattern pattern = Pattern.compile(ignoredProxyHosts);
                ignoredProxyHostPatterns.add(pattern);
            }
        }
        return ignoredProxyHostPatterns;
    }

}
