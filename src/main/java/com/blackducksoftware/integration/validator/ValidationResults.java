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
package com.blackducksoftware.integration.validator;

import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

public class ValidationResults {

    private final Map<Object, Set<String>> resultMap = new LinkedHashMap<>();

    private final Set<ValidationResultEnum> status = EnumSet.noneOf(ValidationResultEnum.class);

    public void addAllResults(final Map<Object, Set<ValidationResult>> results) {
        for (final Entry<Object, Set<ValidationResult>> entry : results.entrySet()) {
            for (final ValidationResult result : entry.getValue()) {
                // This will prevent duplication
                addResult(entry.getKey(), result);
            }
        }
    }

    public void addAllResultsStrings(final Map<Object, Set<String>> results, Set<ValidationResultEnum> newStatusSet) {
        for (final Entry<Object, Set<String>> entry : results.entrySet()) {
            for (final String result : entry.getValue()) {
                // This will prevent duplication
                addResult(entry.getKey(), result);
            }
        }
        status.addAll(newStatusSet);
    }

    public void addResult(final Object fieldKey, final ValidationResult result) {
        final String newResult = result.toString().trim();
        addResult(fieldKey, newResult, result.getResultType());
    }

    public void addResult(final Object fieldKey, final String newResult, ValidationResultEnum newStatus) {
        addResult(fieldKey, newResult);
        status.add(newStatus);
    }

    private void addResult(final Object fieldKey, final String newResult) {
        final Set<String> resultStrings;
        if (resultMap.containsKey(fieldKey)) {
            resultStrings = resultMap.get(fieldKey);

            if (!resultStrings.contains(newResult)) {
                resultStrings.add(newResult);
                resultMap.put(fieldKey, resultStrings);
            }
        } else {
            final Set<String> newResults = new LinkedHashSet<>();
            newResults.add(newResult);
            resultMap.put(fieldKey, newResults);
        }
    }

    public String getResultString(final Object fieldKey) {
        final Set<String> results = resultMap.get(fieldKey);
        return StringUtils.join(results, System.lineSeparator());
    }

    public String getAllResultString() {
        final Set<String> results = new LinkedHashSet<>();
        for (final Entry<Object, Set<String>> result : resultMap.entrySet()) {
            final String fieldResults = StringUtils.join(result.getValue(), System.lineSeparator());
            results.add(result.getKey() + " =" + System.lineSeparator() + fieldResults);
        }
        String resultString = "";
        if (!results.isEmpty()) {
            resultString = StringUtils.join(results, System.lineSeparator());
        }
        return resultString;
    }

    public Map<Object, Set<String>> getResultMap() {
        return resultMap;
    }

    public Set<ValidationResultEnum> getValidationStatus() {
        return status;
    }

    public boolean hasErrors() {
        return status.contains(ValidationResultEnum.ERROR);
    }

    public boolean hasWarnings() {
        return status.contains(ValidationResultEnum.WARN);
    }

    public boolean isSuccess() {
        return status.isEmpty();
    }

    public boolean isEmpty() {
        return resultMap.isEmpty();
    }
}
