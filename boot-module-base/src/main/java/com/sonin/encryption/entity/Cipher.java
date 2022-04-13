package com.sonin.encryption.entity;

import java.math.BigInteger;

import com.sonin.encryption.util.CommonUtils;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.math.ec.ECPoint;


/**
 * @Author sonin
 * @Date 2021/4/10 10:23 上午
 * @Version 1.0
 **/
public class Cipher {

    private int ct;
    private ECPoint p2;
    private SM2Digest sm2KeyBase;
    private SM2Digest sm2c2;
    private final byte[] key;
    private byte keyOff;

    public Cipher() {
        this.ct = 1;
        this.key = new byte[32];
        this.keyOff = 0;
    }

    private void reset() {
        this.sm2KeyBase = new SM2Digest();
        this.sm2c2 = new SM2Digest();

        byte[] p = CommonUtils.byteConvert32Bytes(p2.getX().toBigInteger());
        this.sm2KeyBase.update(p, 0, p.length);
        this.sm2c2.update(p, 0, p.length);

        p = CommonUtils.byteConvert32Bytes(p2.getY().toBigInteger());
        this.sm2KeyBase.update(p, 0, p.length);
        this.ct = 1;
        nextKey();
    }

    private void nextKey() {
        SM2Digest sm2Digest = new SM2Digest(this.sm2KeyBase);
        sm2Digest.update((byte) (ct >> 24 & 0xff));
        sm2Digest.update((byte) (ct >> 16 & 0xff));
        sm2Digest.update((byte) (ct >> 8 & 0xff));
        sm2Digest.update((byte) (ct & 0xff));
        sm2Digest.doFinal(key);
        this.keyOff = 0;
        this.ct++;
    }

    public ECPoint initEnc(SM2 sm2, ECPoint userKey) {
        AsymmetricCipherKeyPair key = sm2.eccKeyPairGenerator.generateKeyPair();
        ECPrivateKeyParameters ecPrivateKeyParameters = (ECPrivateKeyParameters) key.getPrivate();
        ECPublicKeyParameters ecPublicKeyParameters = (ECPublicKeyParameters) key.getPublic();
        BigInteger k = ecPrivateKeyParameters.getD();
        ECPoint c1 = ecPublicKeyParameters.getQ();
        this.p2 = userKey.multiply(k);
        reset();
        return c1;
    }

    public void encrypt(byte[] data) {
        this.sm2c2.update(data, 0, data.length);
        for (int i = 0; i < data.length; i++) {
            if (keyOff == key.length) {
                nextKey();
            }
            data[i] ^= key[keyOff++];
        }
    }

    public void initDec(BigInteger userD, ECPoint c1) {
        this.p2 = c1.multiply(userD);
        reset();
    }

    public void decrypt(byte[] data) {
        for (int i = 0; i < data.length; i++) {
            if (keyOff == key.length) {
                nextKey();
            }
            data[i] ^= key[keyOff++];
        }
        this.sm2c2.update(data, 0, data.length);
    }

    public void doFinal(byte[] c3) {
        byte[] p = CommonUtils.byteConvert32Bytes(p2.getY().toBigInteger());
        this.sm2c2.update(p, 0, p.length);
        this.sm2c2.doFinal(c3);
        reset();
    }
}
