package com.sonin.encryption.demo;

import com.sonin.encryption.util.CommonUtils;
import com.sonin.encryption.util.SM2Utils;

import java.util.Map;

/**
 * @Author sonin
 * @Date 2021/7/15 17:46
 */
public class SM2Demo {

    public static void main(String[] args) {

        // 生成一对公私钥
        Map<String, String> keys = SM2Utils.generateKeyPair();
        System.out.println("publicKey: " + keys.get("publicKey"));
        System.out.println("privateKey: " + keys.get("privateKey"));

        System.out.println("\n=========\n");

        // 原始文本
        String sourceText = "2021-05-12 14:44:44";
        System.out.println("原始文本: " + sourceText);

        // 加密文本
        String encryptText = SM2Utils.encrypt(CommonUtils.hexToByte(keys.get("publicKey")), sourceText.getBytes());
        System.out.println("加密文本: " + encryptText);

        // 解密文本
        String decryptText = new String(SM2Utils.decrypt(CommonUtils.hexToByte(keys.get("privateKey")), CommonUtils.hexToByte(encryptText)));
        System.out.println("解密文本: " + decryptText);

        System.out.println("\n=========\n");

        // 是否相等
        System.out.println("是否相等: " + sourceText.equals(decryptText));
    }

}
