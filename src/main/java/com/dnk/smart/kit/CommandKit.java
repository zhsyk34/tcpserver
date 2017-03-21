package com.dnk.smart.kit;

import com.alibaba.fastjson.JSONObject;

public class CommandKit {
    private static final String RESULT = "result";

    private static final String OK = "ok";
    private static final String NO = "no";

    private static final String ACTION = "action";

    private static final String ERR_NO = "errno";
    private static final String KEY_CODE = "keyCode";
    private static final String KEY = "key";

    public static String correct() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(RESULT, OK);
        return jsonObject.toString();
    }

    public static String wrong(int code) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(RESULT, NO);
        jsonObject.put(ERR_NO, code);
        return jsonObject.toString();
    }

    public static String loginVerify(String key) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(ACTION, "loginVerify");
        jsonObject.put(KEY, key);
        return jsonObject.toString();
    }

    public static void main(String[] args) {
        correct();
    }
}
