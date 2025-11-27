package com.sonin.modules.tuya;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author sonin
 * @Date 2025/11/27 14:44
 */
@Component
public class TuyaHttpClient {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private TuyaSignatureUtils signatureUtils;

    @Value("${tuya.api.base-url}")
    private String baseUrl;

    /**
     * 发送 GET 请求
     */
    public String doGet(String path) throws Exception {
        long timestamp = System.currentTimeMillis();
        String nonce = signatureUtils.generateNonce();
        String signature = signatureUtils.generateSignature(timestamp, nonce, null);

        HttpHeaders headers = new HttpHeaders();
        headers.set("client_id", signatureUtils.getAccessId());
        headers.set("sign", signature);
        headers.set("t", String.valueOf(timestamp));
        headers.set("nonce", nonce);
        headers.set("sign_method", "HMAC-SHA256");

        HttpEntity<?> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + path,
                HttpMethod.GET,
                entity,
                String.class
        );
        return response.getBody();
    }

    /**
     * 发送 POST 请求
     */
    public String doPost(String path, String body) throws Exception {
        long timestamp = System.currentTimeMillis();
        String nonce = signatureUtils.generateNonce();
        String signature = signatureUtils.generateSignature(timestamp, nonce, body);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("client_id", signatureUtils.getAccessId());
        headers.set("sign", signature);
        headers.set("t", String.valueOf(timestamp));
        headers.set("nonce", nonce);
        headers.set("sign_method", "HMAC-SHA256");

        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + path,
                HttpMethod.POST,
                entity,
                String.class
        );
        return response.getBody();
    }

}
