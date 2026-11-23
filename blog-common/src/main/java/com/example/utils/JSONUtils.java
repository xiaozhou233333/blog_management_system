package com.example.utils;

import com.alibaba.fastjson2.JSON;

public class JSONUtils {
    public static <T> T parseObject(String json, Class<T> clazz) {
        if (json == null || json.length() == 0 || clazz == null) {
            return null;
        }
        return JSON.parseObject(json, clazz);
    }

    public static String toJson(Object object) {
        if (object != null) {
            return JSON.toJSONString(object);
        } else {
            return null;
        }
    }
}
