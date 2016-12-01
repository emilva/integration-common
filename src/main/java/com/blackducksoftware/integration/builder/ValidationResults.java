/*******************************************************************************
 * Copyright (C) 2016 Black Duck Software, Inc.
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
 *******************************************************************************/
package com.blackducksoftware.integration.builder;

import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

public class ValidationResults<K, T> {
    private T constructedObject;

    private final Map<K, Set<String>> resultMap = new LinkedHashMap<>();

    private final Set<ValidationResultEnum> status = EnumSet.noneOf(ValidationResultEnum.class);

    public void addAllResults(final Map<K, List<ValidationResult>> results) {
        for (final Entry<K, List<ValidationResult>> entry : results.entrySet()) {
            for (final ValidationResult result : entry.getValue()) {
                // This will prevent duplication
                addResult(entry.getKey(), result);
            }
        }
    }

    public void addResult(final K fieldKey, final ValidationResult result) {
        String newResult = result.toString().trim();

        final Set<String> resultStrings;
        if (resultMap.containsKey(fieldKey)) {
            resultStrings = resultMap.get(fieldKey);

            if (!resultStrings.contains(newResult)) {
                resultStrings.add(newResult);
                resultMap.put(fieldKey, resultStrings);
            }
        } else {
            Set<String> newResults = new LinkedHashSet<>();
            newResults.add(newResult);
            resultMap.put(fieldKey, newResults);
        }
        status.add(result.getResultType());
    }

    public String getResultString(final K fieldKey) {
        Set<String> results = resultMap.get(fieldKey);
        return StringUtils.join(results, System.lineSeparator());
    }

    public String getAllResultString() {
        Set<String> results = new LinkedHashSet<>();
        for (Entry<K, Set<String>> result : resultMap.entrySet()) {
            String fieldResults = StringUtils.join(result.getValue(), System.lineSeparator());
            results.add(result.getKey() + " =" + System.lineSeparator() + fieldResults);
        }
        String resultString = "";
        if (!results.isEmpty()) {
            resultString = StringUtils.join(results, System.lineSeparator());
        }
        return resultString;
    }

    public T getConstructedObject() {
        return constructedObject;
    }

    public void setConstructedObject(final T constructedObject) {
        this.constructedObject = constructedObject;
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
