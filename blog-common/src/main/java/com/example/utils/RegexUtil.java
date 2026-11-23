package com.example.utils;

import java.util.regex.Pattern;

import org.springframework.util.StringUtils;

public class RegexUtil {
    private static final String emailRegex = "^[a-z0-9]+([._\\\\-]*[a-z0-9])*@([a-z0-9]+[-a-z0-9]*[a-z0-9]+.){1,63}[a-z0-9]+$";

    private static final String urlRegex = "^(https?):\\/\\/([a-zA-Z0-9.-]+)(:\\d+)?(\\/[^\\s]*)?(\\?[^\\s]*)?$";

    public static boolean checkMail(String content) {
        if (!StringUtils.hasText(content)) {
            return false;
        }
        return Pattern.matches(emailRegex, content);
    }

    public static boolean checkUrl(String content) {
        if (!StringUtils.hasText(content)) {
            return false;
        }
        return Pattern.matches(urlRegex, content);
    }
}
