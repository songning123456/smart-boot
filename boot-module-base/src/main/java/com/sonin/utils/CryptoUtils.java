package com.sonin.utils;

import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * <pre>
 * 基于AES加密、解密
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/5/2 15:39
 */
public class CryptoUtils {

    /**
     * AES加密
     *
     * @param srcContent 明文
     * @param secretKey  秘钥，必须为16个字符组成
     * @return 密文
     * @throws Exception
     */
    public static String aesEncrypt(String srcContent, String secretKey) throws Exception {
        if (StringUtils.isEmpty(srcContent) || StringUtils.isEmpty(secretKey)) {
            return null;
        }
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(secretKey.getBytes(), "AES"));
        byte[] encryptStr = cipher.doFinal(srcContent.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptStr);
    }

    /**
     * AES解密
     *
     * @param encryptContent 密文
     * @param secretKey      秘钥，必须为16个字符组成
     * @return 明文
     * @throws Exception
     */
    public static String aesDecrypt(String encryptContent, String secretKey) throws Exception {
        if (StringUtils.isEmpty(encryptContent) || StringUtils.isEmpty(secretKey)) {
            return null;
        }
        byte[] encryptByte = Base64.getDecoder().decode(encryptContent);
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(secretKey.getBytes(), "AES"));
        byte[] decryptBytes = cipher.doFinal(encryptByte);
        return new String(decryptBytes);
    }

    // 测试加密与解密
    public static void main(String[] args) {
        try {
            String secretKey = "aaaabbbbccccdddd";
            String content = "hello world";
            String s1 = aesEncrypt(content, secretKey);
            System.out.println(s1);
            String s = aesDecrypt(s1, secretKey);
            System.out.println(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
