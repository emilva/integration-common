package com.blackducksoftware.integration.util;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;

public class ExcludedIncludedFilter {
    private final Set<String> excludedSet;

    private final Set<String> includedSet;

    /**
     * Provide a comma-separated list of names to exclude and/or a comma-separated list of names to include. Exclusion
     * rules always win.
     */
    public ExcludedIncludedFilter(final String toExclude, final String toInclude) {
        excludedSet = createSetFromString(toExclude);
        includedSet = createSetFromString(toInclude);
    }

    public boolean shouldInclude(final String itemName) {
        if (excludedSet.contains(itemName)) {
            return false;
        }

        if (includedSet.size() > 0 && !includedSet.contains(itemName)) {
            return false;
        }

        return true;
    }

    private Set<String> createSetFromString(final String s) {
        final Set<String> set = new HashSet<>();
        final StringTokenizer stringTokenizer = new StringTokenizer(s, ",");
        while (stringTokenizer.hasMoreTokens()) {
            set.add(StringUtils.trimToEmpty(stringTokenizer.nextToken()));
        }
        return set;
    }

}
