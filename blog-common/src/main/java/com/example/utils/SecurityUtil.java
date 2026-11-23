package com.example.utils;

import java.util.UUID;

import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

public class SecurityUtil {
    public static String encrypt(String password) {
        String salt = UUID.randomUUID().toString().replace("-", "");
        String finalPassword = DigestUtils.md5DigestAsHex((salt+password).getBytes());
        return salt + finalPassword;
    }

    public static boolean verify(String password, String finalPassword) {
        if (!StringUtils.hasLength(password) || !StringUtils.hasLength(finalPassword)) {
            return false;
        }

        if (finalPassword.length() != 64) {
            return false;
        }

        String salt = finalPassword.substring(0, 32);
        String securityPassword = DigestUtils.md5DigestAsHex((salt + password).getBytes());
        return (salt + securityPassword).equals(finalPassword);
    }

    public static void main(String[] args) {
        String finalPassword = encrypt("123456");
        System.out.println(finalPassword);
        System.out.println(verify("123456", finalPassword));
    }
}
