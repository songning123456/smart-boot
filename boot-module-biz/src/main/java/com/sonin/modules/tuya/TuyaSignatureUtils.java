package com.sonin.modules.tuya;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.UUID;

/**
 * @Author sonin
 * @Date 2025/11/27 14:43
 */
@Component
public class TuyaSignatureUtils {

    private static final Logger logger = LoggerFactory.getLogger(TuyaSignatureUtils.class);

    @Value("${tuya.api.access-id}")
    private String accessId;

    @Value("${tuya.api.access-key}")
    private String accessKey;

    public String getAccessId() {
        return accessId;
    }

    public String getAccessKey() {
        return accessKey;
    }

    /**
     * 生成签名
     *
     * @param timestamp 时间戳（毫秒级）
     * @param nonce     随机字符串
     * @param body      请求体（POST 请求时需传入）
     * @return 签名结果
     */
    public String generateSignature(long timestamp, String nonce, String body) {
        try {
            // 1. 拼接签名原文
            StringBuilder sb = new StringBuilder();
            sb.append(accessId)
                    .append(timestamp)
                    .append(nonce);

            String bodySha256 = "";
            if (body != null && !body.isEmpty()) {
                // 对请求体进行 SHA256 加密
                bodySha256 = sha256(body);
                sb.append(bodySha256);
            }

            String signatureSource = sb.toString();
            logger.info("Signature Source String: {}", signatureSource);
            logger.info("Body SHA256: {}", bodySha256);

            // 2. HMAC-SHA256 加密
            Mac hmacSha256 = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(accessKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            hmacSha256.init(secretKey);
            byte[] signatureBytes = hmacSha256.doFinal(signatureSource.getBytes(StandardCharsets.UTF_8));

            // 3. Base64 编码
            String sign = Base64.getEncoder().encodeToString(signatureBytes);
            logger.info("Generated Sign: {}", sign);

            return sign;
        } catch (Exception e) {
            logger.error("Failed to generate signature", e);
            throw new RuntimeException("Signature generation failed", e);
        }
    }

    /**
     * SHA256 加密
     */
    private String sha256(String content) throws Exception {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        byte[] digest = messageDigest.digest(content.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    /**
     * 生成随机字符串
     */
    public String generateNonce() {
        return UUID.randomUUID().toString().replace("-", "");
    }

}
