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

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.lang3.StringUtils;

public class ValidationResults<K, T> {
    private T constructedObject;

    private final Map<K, Map<ValidationResultEnum, List<ValidationResult>>> resultMap;

    private final Set<ValidationResultEnum> status = EnumSet.noneOf(ValidationResultEnum.class);

    public ValidationResults() {
        resultMap = new HashMap<>();
    }

    public void addAllResults(final Map<K, List<ValidationResult>> results) {
        for (final Entry<K, List<ValidationResult>> entry : results.entrySet()) {
            for (final ValidationResult result : entry.getValue()) {
                // This will prevent duplication
                addResult(entry.getKey(), result);
            }
        }
    }

    public void addResult(final K fieldKey, final ValidationResult result) {
        final List<ValidationResult> resultList;
        final Map<ValidationResultEnum, List<ValidationResult>> resultListMap;
        final ValidationResultEnum resultType = result.getResultType();
        if (resultMap.containsKey(fieldKey)) {
            resultListMap = resultMap.get(fieldKey);

        } else {
            resultListMap = new LinkedHashMap<>();
            resultMap.put(fieldKey, resultListMap);
        }

        if (resultListMap.containsKey(resultType)) {
            resultList = resultListMap.get(result.getResultType());
        } else {
            resultList = new Vector<>();
            resultListMap.put(resultType, resultList);
        }
        status.add(resultType);

        addIfNotTheSame(resultList, result);
    }

    private void addIfNotTheSame(final List<ValidationResult> list, final ValidationResult potentialResult) {
        boolean found = false;
        for (final ValidationResult result : list) {
            if (result.getResultType() == potentialResult.getResultType()
                    && StringUtils.trimToEmpty(result.getMessage()).equals(potentialResult.getMessage())) {
                found = true;
                break;
            }
        }

        if (!found) {
            list.add(potentialResult);
        }
    }

    public Map<K, List<ValidationResult>> getResultMap() {
        final Map<K, List<ValidationResult>> map = new HashMap<>();
        for (final K fieldKey : resultMap.keySet()) {
            map.put(fieldKey, getResultList(fieldKey));
        }
        return map;
    }

    public List<ValidationResult> getResultList(final K fieldKey) {
        if (resultMap.containsKey(fieldKey)) {
            final List<ValidationResult> resultList = new Vector<>();
            final Map<ValidationResultEnum, List<ValidationResult>> itemList = resultMap.get(fieldKey);
            for (final ValidationResultEnum key : itemList.keySet()) {
                resultList.addAll(itemList.get(key));
            }

            return resultList;
        } else {
            return new Vector<>();
        }
    }

    public List<String> getResultList(final K fieldKey, final ValidationResultEnum resultEnum) {
        final List<String> resultList = new ArrayList<>();
        if (resultMap.containsKey(fieldKey)) {
            final Map<ValidationResultEnum, List<ValidationResult>> listMap = resultMap.get(fieldKey);
            if (listMap.containsKey(resultEnum)) {
                final List<ValidationResult> itemList = listMap.get(resultEnum);
                for (final ValidationResult result : itemList) {
                    if (result.getThrowable() != null) {
                        String resultMessage = String.format("%s [%s]", result.getMessage(), result.getThrowable().toString());
                        resultList.add(resultMessage);
                    } else {
                        resultList.add(result.getMessage());
                    }
                }
            }
        }
        return resultList;
    }

    public String getResultString(final K fieldKey, final ValidationResultEnum resultEnum) {
        String resultString = "";
        final List<String> resultList = getResultList(fieldKey, resultEnum);
        if (!resultList.isEmpty()) {
            resultString = StringUtils.join(resultList, "\n");
        }
        return resultString;
    }

    public List<Throwable> getResultThrowables(final K fieldKey, final ValidationResultEnum resultEnum) {
        final List<Throwable> throwables = new ArrayList<>();

        if (resultMap.containsKey(fieldKey)) {
            final Map<ValidationResultEnum, List<ValidationResult>> listMap = resultMap.get(fieldKey);
            if (listMap.containsKey(resultEnum)) {
                final List<ValidationResult> itemList = listMap.get(resultEnum);
                for (final ValidationResult result : itemList) {
                    if (result.getThrowable() != null) {
                        throwables.add(result.getThrowable());
                    }
                }
            }
        }
        return throwables;
    }

    public List<String> getAllResultList(final ValidationResultEnum resultEnum) {
        final List<String> resultList = new ArrayList<>();

        for (final Entry<K, Map<ValidationResultEnum, List<ValidationResult>>> entry : resultMap.entrySet()) {
            if (entry.getValue().containsKey(resultEnum)) {
                for (final ValidationResult result : entry.getValue().get(resultEnum)) {
                    if (result.getThrowable() != null) {
                        String resultMessage = String.format("%s [%s]", result.getMessage(), result.getThrowable().toString());
                        resultList.add(resultMessage);
                    } else {
                        resultList.add(result.getMessage());
                    }
                }
            }
        }
        return resultList;
    }

    public String getAllResultString(final ValidationResultEnum resultEnum) {
        String resultString = "";

        final List<String> resultList = getAllResultList(resultEnum);
        if (!resultList.isEmpty()) {
            resultString = StringUtils.join(resultList, "\n");
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

    public boolean hasErrors(final K fieldKey) {
        if (resultMap.containsKey(fieldKey)) {
            return resultMap.get(fieldKey).containsKey(ValidationResultEnum.ERROR);
        } else {
            return false;
        }
    }

    public boolean hasWarnings(final K fieldKey) {
        if (resultMap.containsKey(fieldKey)) {
            return resultMap.get(fieldKey).containsKey(ValidationResultEnum.WARN);
        } else {
            return false;
        }
    }

    public boolean hasErrors() {
        return status.contains(ValidationResultEnum.ERROR);
    }

    public boolean hasWarnings() {
        return status.contains(ValidationResultEnum.WARN);
    }

    public boolean isSuccess() {
        return (status.size() == 1 && status.contains(ValidationResultEnum.OK));
    }

    public boolean isEmpty() {
        return status.isEmpty();
    }

}
