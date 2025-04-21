package com.Synchrome.workspace.common;

import org.apache.commons.lang.RandomStringUtils;

public class InviteCodeGenerator {
    public static String generate(int length) {
        return RandomStringUtils.randomAlphanumeric(length);
    }
}
