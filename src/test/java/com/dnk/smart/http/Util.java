package com.dnk.smart.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

public abstract class Util {
    private static final String CHARSET = "utf-8";

    public String base(String url, Constant.Method method, Map<String, String> headerMap, Map<String, String> paramMap) throws Exception {
        System.setProperty("http.agent", "");//TODO 清除java信息 爬虫?
        switch (method) {
            case GET:
                return this.get(url, headerMap, paramMap);
            case POST:
                return post(url, headerMap, paramMap);
        }
        return null;
    }

    public abstract String get(String url, Map<String, String> headerMap, Map<String, String> paramMap) throws Exception;

    public abstract String post(String url, Map<String, String> headerMap, Map<String, String> paramMap) throws Exception;

    public abstract String post(String url, Map<String, String> headerMap, String params) throws Exception;

    //设置常用首部
    public Map<String, String> setBaseHeader() {
        Map<String, String> map = new HashMap<>();
        map.put("Accept", "*/*");
        map.put("Accept-Charset", CHARSET);

        map.put("Cache-Control", "no-cache");
        map.put("Pragma", "no-cache");

        map.put("Connection", "keep-alive");
        map.put("Content-Type", "application/x-www-form-urlencoded; charset=" + CHARSET);
        return map;
    }

    //base:是否设置默认值
    public void setHeader(URLConnection connection, Map<String, String> map, boolean base) {
        if (map == null) {
            if (base) {
                map = new HashMap<>();
                map.putAll(this.setBaseHeader());
            } else {
                return;
            }
        }
        map.forEach((k, v) -> connection.setRequestProperty(k, v));
    }

    //请求参数处理
    public String parseParams(Constant.Method method, Map<String, String> params) {
        StringBuilder result = new StringBuilder();
        params.forEach((k, v) -> result.append("&" + k + "=" + v));
        result.replace(0, 1, "");

        switch (method) {
            case GET:
                result.insert(0, "?");
                break;
            case POST:
                break;
        }
        System.out.println(result);
        return result.toString();
    }

    //将得到的inputStream转为string
    public String receive(InputStream in) throws IOException {
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }
        reader.close();
        return builder.toString();
    }
}