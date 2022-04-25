package com.sonin.encryption.sm2.utils;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import com.sonin.encryption.sm2.entity.Cipher;
import com.sonin.encryption.sm2.entity.SM2;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.math.ec.ECPoint;

/**
 * @Author sonin
 * @Date 2021/7/15 17:49
 */
public class SM2Utils {

    /**
     * 生成随机秘钥对
     * @return
     */
    public static Map<String, String> generateKeyPair() {
        Map<String, String> result = new HashMap<>(2);
        SM2 sm2 = SM2.getInstance();
        AsymmetricCipherKeyPair key = sm2.eccKeyPairGenerator.generateKeyPair();
        ECPrivateKeyParameters ecPrivateKeyParameters = (ECPrivateKeyParameters) key.getPrivate();
        ECPublicKeyParameters ecPublicKeyParameters = (ECPublicKeyParameters) key.getPublic();
        BigInteger privateKey = ecPrivateKeyParameters.getD();
        ECPoint publicKey = ecPublicKeyParameters.getQ();

        result.put("publicKey", ByteUtils.byteToHex(publicKey.getEncoded()));
        result.put("privateKey", ByteUtils.byteToHex(privateKey.toByteArray()));
        return result;
    }

    /**
     * 数据加密
     * @param publicKey
     * @param data
     * @return
     */
    public static String encrypt(byte[] publicKey, byte[] data) {
        if (publicKey == null || publicKey.length == 0) {
            return null;
        }

        if (data == null || data.length == 0) {
            return null;
        }

        byte[] source = new byte[data.length];
        System.arraycopy(data, 0, source, 0, data.length);

        Cipher cipher = new Cipher();
        SM2 sm2 = SM2.getInstance();
        ECPoint userKey = sm2.eccCurve.decodePoint(publicKey);

        ECPoint c1 = cipher.initEnc(sm2, userKey);
        cipher.encrypt(source);
        byte[] c3 = new byte[32];
        cipher.doFinal(c3);

        // C1 C2 C3拼装成加密字串
        return ByteUtils.byteToHex(c1.getEncoded()) + ByteUtils.byteToHex(source) + ByteUtils.byteToHex(c3);

    }

    /**
     * 数据解密
     * @param privateKey
     * @param encryptedData
     * @return
     */
    public static byte[] decrypt(byte[] privateKey, byte[] encryptedData) {
        if (privateKey == null || privateKey.length == 0) {
            return null;
        }

        if (encryptedData == null || encryptedData.length == 0) {
            return null;
        }
        // 加密字节数组转换为十六进制的字符串 长度变为encryptedData.length * 2
        String data = ByteUtils.byteToHex(encryptedData);
        byte[] c1Bytes = ByteUtils.hexToByte(data.substring(0, 130));
        int c2Len = encryptedData.length - 97;
        byte[] c2 = ByteUtils.hexToByte(data.substring(130, 130 + 2 * c2Len));
        byte[] c3 = ByteUtils.hexToByte(data.substring(130 + 2 * c2Len, 194 + 2 * c2Len));

        SM2 sm2 = SM2.getInstance();
        BigInteger userD = new BigInteger(1, privateKey);

        // 通过C1实体字节来生成ECPoint
        ECPoint c1 = sm2.eccCurve.decodePoint(c1Bytes);
        Cipher cipher = new Cipher();
        cipher.initDec(userD, c1);
        cipher.decrypt(c2);
        cipher.doFinal(c3);

        // 返回解密结果
        return c2;
    }

}
