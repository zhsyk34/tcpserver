package com.dnk.smart.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class JsonKit {

    public static JSONObject map(String str) {
        return JSON.parseObject(str);
    }

    public static String getString(String str, String key) {
        return map(str).getString(key);
    }

    public static int getInt(String str, String key) {
        return map(str).getIntValue(key);
    }

}
