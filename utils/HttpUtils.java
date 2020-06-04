package com.yidao.court.prelitigation.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yidao.court.core.enums.ErrorCodeEnum;
import com.yidao.court.core.exception.ServiceException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * http工具类
 */
public class HttpUtils {

    /**
     * get请求
     * @param token
     * @param url
     * @return
     */
    public static String get(String url, String token) {
        RestTemplate restTemplate = new RestTemplate();
        String result;
        if (token != null) {
            HttpHeaders header = new HttpHeaders();
            header.set("Authorization", token);
            HttpEntity<String> requestEntity = new HttpEntity<>(null, header);
            ResponseEntity<String> resEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
            result = resEntity.getBody();
        } else {
            result = restTemplate.getForObject(url, String.class);
        }

        return decode(url, result);
    }

    /**
     * https调用
     * @param url
     * @return
     */
    public static String getHttps(String url) {
        RestTemplate restTemplate;
        if (url.contains("127.0.0.1") || url.contains("localhost")) {
            // 本地测试
            restTemplate = new RestTemplate();
        } else {
            // 实际环境
            restTemplate = new RestTemplate(new HttpsClientRequestFactory());
        }
        String result = restTemplate.getForObject(url, String.class);

        return decode(url, result);
    }

    /**
     * 解析
     * @param url
     * @param result
     * @return
     */
    private static String decode(String url, String result) {
        JSONObject jsonObject = (JSONObject) JSON.parse(result);
        if (jsonObject.containsKey("code") || jsonObject.containsKey("rs_code")) {
            Object codeObject = jsonObject.get("code") != null ? jsonObject.get("code") : jsonObject.get("rs_code");
            Integer code = 1999;
            if (codeObject != null) {
                code = (Integer) codeObject;
            }

            if (!code.equals(1000)) {
                throw new ServiceException(ErrorCodeEnum.HTTP_REMOTE_ERROR, url);
            }
        }

        return result;
    }
}
