package com.ptit.service.util;

import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

public class RequestUtil<T> {
    public static RequestUtil intance;

    public static RequestUtil getIntance() {
        if (intance == null) {
            intance = new RequestUtil();
        }
        return intance;
    }

    /**
     * CURL
     *
     * @param method
     * @param requestUrl
     * @param mData
     * @param headerParam
     * @return
     */
    public static HttpResponse sendRequest(
            HttpMethod method,
            String requestUrl,
            Map<String, Object> mData,
            Map<String, String> headerParam) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();

            if (headerParam != null) {
                for (Map.Entry<String, String> entry : headerParam.entrySet()) {
                    headers.add(entry.getKey(), entry.getValue());
                }
            }
            if (headers.get("Content-Type") == null) {
                headers.add("Content-Type", "application/json");
            }

            HttpEntity<Map<String, Object>> data = new HttpEntity<>(mData, headers);

            ResponseEntity<String> response =
                    restTemplate.exchange(requestUrl, method, data, String.class);

            return new HttpResponse(response.getStatusCode(), response.getBody(), response.getHeaders());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static MediaType getHeaderContentType(Map<String, String> headerParam) {
        MediaType mediaType = MediaType.APPLICATION_JSON;
        if (headerParam != null && headerParam.get("Content-Type") != null) {
            mediaType = MediaType.valueOf(headerParam.get("Content-Type"));
            headerParam.remove("Content-Type");
        }
        return mediaType;
    }

    private static SimpleClientHttpRequestFactory getClientHttpRequestFactory(int timeOut) {
        SimpleClientHttpRequestFactory clientHttpRequestFactory = new SimpleClientHttpRequestFactory();
        // Connect timeout
        clientHttpRequestFactory.setConnectTimeout(timeOut);

        // Read timeout
        clientHttpRequestFactory.setReadTimeout(timeOut);
        return clientHttpRequestFactory;
    }
    /**
     * CURL
     *
     * @param method
     * @param requestUrl
     * @param mData
     * @param headerParam
     * @return
     */
    public HttpResponse sendRequest(
            HttpMethod method, String requestUrl, T mData, Map<String, String> headerParam) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();

            if (headerParam != null) {
                for (Map.Entry<String, String> entry : headerParam.entrySet()) {
                    headers.add(entry.getKey(), entry.getValue());
                }
            }
            if (headers.get("Content-Type") == null) {
                headers.add("Content-Type", "application/json");
            }
            HttpEntity<T> data = new HttpEntity<T>(mData, headers);

            ResponseEntity<String> response =
                    restTemplate.exchange(requestUrl, method, data, String.class);

            return new HttpResponse(response.getStatusCode(), response.getBody(), response.getHeaders());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
