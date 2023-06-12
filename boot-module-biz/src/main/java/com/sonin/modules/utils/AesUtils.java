package com.sonin.modules.utils;

import com.alibaba.druid.util.Base64;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.zip.Inflater;

@Slf4j
public class AesUtils {

    //解密密码 密钥必须是16位的
    public static String DECRYPT_PASSWORD = "jkadminjn1234567";

    /**
     * 解密
     *
     * @param content 密文
     * @param key     加密密码
     * @return String
     * @throws Exception 异常
     */
    public static byte[] decode(byte[] content, String key) throws Exception {
        byte[] raw = key.getBytes(StandardCharsets.UTF_8);
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        return cipher.doFinal(content);
    }


    /**
     * 将16进制字符串转换为字节数组
     *
     * @param str 16进制字符串
     * @return byte[]
     */
    public static byte[] string2ByteArr(String str) {
        byte[] bytes;
        bytes = new byte[str.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(str.substring(2 * i, 2 * i + 2), 16);
        }
        return bytes;
    }

    public static void main(String[] args) throws Exception {
        String s = new String(decode(Base64.base64ToByteArray("4g9XetqA7P5pgVTpTkCVTg=="), "jkadminjn1234567"), "UTF-8");
        System.out.println(s);
    }

    // 解压缩 字节数组
    public static String decompressStr(byte[] data) {
        byte[] output;
        Inflater inflater = new Inflater();
        inflater.reset();
        inflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        try {
            byte[] buf = new byte[1024];
            while (!inflater.finished()) {
                int count = inflater.inflate(buf);
                outputStream.write(buf, 0, count);
            }
            output = outputStream.toByteArray();
        } catch (Exception e) {
            output = data;
            log.error("异常：" + e.getMessage());
        } finally {
            try {
                outputStream.close();
                inflater.end();
            } catch (IOException e) {
                log.error("异常：" + e.getMessage());
            }
        }
        return new String(output, 0, output.length, StandardCharsets.UTF_8);
    }

}