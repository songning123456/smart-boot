package com.sonin.encryption.entity;

import java.math.BigInteger;
import java.security.SecureRandom;

import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.ECFieldElement.Fp;

/**
 * @Author sonin
 * @Date 2021/7/15 17:50
 */
public class SM2 {

    /**
     * 正式参数
     */
    public static String[] eccParam = {
            "FFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF00000000FFFFFFFFFFFFFFFF",
            "FFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF00000000FFFFFFFFFFFFFFFC",
            "28E9FA9E9D9F5E344D5A9E4BCF6509A7F39789F515AB8F92DDBCBD414D940E93",
            "FFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFF7203DF6B21C6052B53BBF40939D54123",
            "32C4AE2C1F1981195F9904466A39C9948FE30BBFF2660BE1715A4589334C74C7",
            "BC3736A2F4F6779C59BDCEE36B692153D0A9877CC62A474002DF32E52139F0A0"
    };

    public static SM2 getInstance() {
        return new SM2();
    }

    public final BigInteger eccP;
    public final BigInteger eccA;
    public final BigInteger eccB;
    public final BigInteger eccN;
    public final BigInteger eccGx;
    public final BigInteger eccGy;
    public final ECCurve eccCurve;
    public final ECPoint eccPointG;
    public final ECDomainParameters eccBcSpec;
    public final ECKeyPairGenerator eccKeyPairGenerator;
    public final ECFieldElement eccGxFieldElement;
    public final ECFieldElement eccGyFieldElement;

    public SM2() {
        this.eccP = new BigInteger(eccParam[0], 16);
        this.eccA = new BigInteger(eccParam[1], 16);
        this.eccB = new BigInteger(eccParam[2], 16);
        this.eccN = new BigInteger(eccParam[3], 16);
        this.eccGx = new BigInteger(eccParam[4], 16);
        this.eccGy = new BigInteger(eccParam[5], 16);

        this.eccGxFieldElement = new Fp(this.eccP, this.eccGx);
        this.eccGyFieldElement = new Fp(this.eccP, this.eccGy);

        this.eccCurve = new ECCurve.Fp(this.eccP, this.eccA, this.eccB);
        this.eccPointG = new ECPoint.Fp(this.eccCurve, this.eccGxFieldElement, this.eccGyFieldElement);

        this.eccBcSpec = new ECDomainParameters(this.eccCurve, this.eccPointG, this.eccN);

        ECKeyGenerationParameters eccEcGenParam;
        eccEcGenParam = new ECKeyGenerationParameters(this.eccBcSpec, new SecureRandom());

        this.eccKeyPairGenerator = new ECKeyPairGenerator();
        this.eccKeyPairGenerator.init(eccEcGenParam);
    }
}
