package com.dnk.smart.http;

import com.alibaba.fastjson.JSON;

import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class BaseRequest extends Util {

    public static void main(String[] args) throws Exception {
//        String url = "http://120.76.234.67:8088/";
        String url = "http://localhost:8080/";
        Map<String, String> map = new HashMap<>();
        map.put("lock", "bd9b94d1-fa74-11e6-8c12-408d5cf38325");
        System.out.println(new BaseRequest().post(url + "api/record", null, map));
    }

    @Override
    public String get(String url, Map<String, String> headerMap, Map<String, String> paramMap) throws Exception {
        String result;
        if (paramMap != null && !paramMap.isEmpty()) {
            url += super.parseParams(Constant.Method.GET, paramMap);
        }

        URL address = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) address.openConnection();

        setHeader(connection, headerMap, true);

        int code = connection.getResponseCode();
        if (code != 200) {
            return super.receive(connection.getErrorStream());
        }

        result = super.receive(connection.getInputStream());
        connection.disconnect();

        return result;
    }

    @Override
    public String post(String url, Map<String, String> headerMap, Map<String, String> paramMap) throws Exception {
        String params = JSON.toJSONString(paramMap);
        System.out.println("params: " + params);
        return this.post(url, headerMap, params);
//        return this.post(url, headerMap, JSON.toJSONString(paramMap));
    }

    @Override
    public String post(String url, Map<String, String> headerMap, String params) throws Exception {
        String result;
        URL address = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) address.openConnection();

        connection.setRequestMethod(Constant.Method.POST.toString());
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setInstanceFollowRedirects(false);

        setHeader(connection, headerMap, false);

        if (params != null && !params.isEmpty()) {
            PrintWriter out;
            out = new PrintWriter(connection.getOutputStream());
            out.print(params);
            out.flush();
            out.close();
        }

        int code = connection.getResponseCode();
        if (code != 200) {
            return super.receive(connection.getErrorStream());
        }

        result = super.receive(connection.getInputStream());
        connection.disconnect();
        return result;
    }
}