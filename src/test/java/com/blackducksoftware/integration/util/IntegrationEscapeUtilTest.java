package com.blackducksoftware.integration.util;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class IntegrationEscapeUtilTest {
    @Test
    public void testEscapingForUri() {
        final IntegrationEscapeUtil integrationEscapeUtil = new IntegrationEscapeUtil();
        Assert.assertEquals(null, integrationEscapeUtil.escapeForUri(null));
        Assert.assertEquals("", integrationEscapeUtil.escapeForUri(""));
        Assert.assertEquals("_a_b_c_1_2___3", integrationEscapeUtil.escapeForUri("!a$b:c@1 2-- 3"));

        final List<String> messyStrings = Arrays.asList(new String[] { "#A(B)C++=", "def", "~\tgh1<>23*i.." });
        final List<String> cleanStrings = Arrays.asList(new String[] { "_A_B_C___", "def", "__gh1__23_i__" });
        Assert.assertEquals(cleanStrings, integrationEscapeUtil.escapePiecesForUri(messyStrings));
    }
}
