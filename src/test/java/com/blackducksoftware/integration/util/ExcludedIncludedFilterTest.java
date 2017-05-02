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
package com.blackducksoftware.integration.util;

import org.junit.Assert;
import org.junit.Test;

public class ExcludedIncludedFilterTest {
    @Test
    public void testConstructor() {
        ExcludedIncludedFilter excludedIncludedFilter = new ExcludedIncludedFilter("", "");
        Assert.assertTrue(excludedIncludedFilter.shouldInclude("whatever"));

        excludedIncludedFilter = new ExcludedIncludedFilter(null, null);
        Assert.assertTrue(excludedIncludedFilter.shouldInclude("whatever"));
    }

    @Test
    public void testExcluded() {
        ExcludedIncludedFilter excludedIncludedFilter = new ExcludedIncludedFilter("bad", "");
        Assert.assertTrue(excludedIncludedFilter.shouldInclude("whatever"));
        Assert.assertFalse(excludedIncludedFilter.shouldInclude("bad"));

        excludedIncludedFilter = new ExcludedIncludedFilter("really_bad,also_really_bad", null);
        Assert.assertTrue(excludedIncludedFilter.shouldInclude("whatever"));
        Assert.assertFalse(excludedIncludedFilter.shouldInclude("really_bad"));
        Assert.assertFalse(excludedIncludedFilter.shouldInclude("also_really_bad"));
    }

    @Test
    public void testIncludedAndExcluded() {
        ExcludedIncludedFilter excludedIncludedFilter = new ExcludedIncludedFilter("bad", "good,bad");
        Assert.assertFalse(excludedIncludedFilter.shouldInclude("whatever"));
        Assert.assertTrue(excludedIncludedFilter.shouldInclude("good"));
        Assert.assertFalse(excludedIncludedFilter.shouldInclude("bad"));

        excludedIncludedFilter = new ExcludedIncludedFilter("really_bad,also_really_bad", "good");
        Assert.assertFalse(excludedIncludedFilter.shouldInclude("whatever"));
        Assert.assertTrue(excludedIncludedFilter.shouldInclude("good"));
        Assert.assertFalse(excludedIncludedFilter.shouldInclude("really_bad"));
        Assert.assertFalse(excludedIncludedFilter.shouldInclude("also_really_bad"));
    }

}
