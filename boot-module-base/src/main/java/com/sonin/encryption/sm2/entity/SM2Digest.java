package com.sonin.encryption.sm2.entity;

import com.sonin.encryption.sm2.utils.ByteUtils;

/**
 * @Author sonin
 * @Date 2021/7/16 9:30
 */
public class SM2Digest {

    /**
     * SM2���鳤��
     */
    private static final int BLOCK_LENGTH = 64;

    /**
     * ����������
     */
    private static final int BUFFER_LENGTH = BLOCK_LENGTH;

    /**
     * ������
     */
    private final byte[] xBuf = new byte[BUFFER_LENGTH];

    /**
     * ������ƫ����
     */
    private int xBufOff;

    /**
     * ��ʼ����
     */
    private final byte[] initV = IV.clone();

    private int cntBlock = 0;

    public static final byte[] IV = {0x73, (byte) 0x80, 0x16, 0x6f, 0x49,
            0x14, (byte) 0xb2, (byte) 0xb9, 0x17, 0x24, 0x42, (byte) 0xd7,
            (byte) 0xda, (byte) 0x8a, 0x06, 0x00, (byte) 0xa9, 0x6f, 0x30,
            (byte) 0xbc, (byte) 0x16, 0x31, 0x38, (byte) 0xaa, (byte) 0xe3,
            (byte) 0x8d, (byte) 0xee, 0x4d, (byte) 0xb0, (byte) 0xfb, 0x0e,
            0x4e
    };

    public static int[] TJ = new int[64];

    static {
        for (int i = 0; i < 16; i++) {
            TJ[i] = 0x79cc4519;
        }

        for (int i = 16; i < 64; i++) {
            TJ[i] = 0x7a879d8a;
        }
    }

    public SM2Digest() {
    }

    public SM2Digest(SM2Digest sm2Digest) {
        System.arraycopy(sm2Digest.xBuf, 0, this.xBuf, 0, sm2Digest.xBuf.length);
        this.xBufOff = sm2Digest.xBufOff;
        System.arraycopy(sm2Digest.initV, 0, this.initV, 0, sm2Digest.initV.length);
    }

    public static byte[] cf(byte[] vBytes, byte[] bBytes) {
        int[] v, b;
        v = convert(vBytes);
        b = convert(bBytes);
        return convert(cf(v, b));
    }

    private static int[] convert(byte[] arr) {
        int[] out = new int[arr.length / 4];
        byte[] tmp = new byte[4];
        for (int i = 0; i < arr.length; i += 4) {
            System.arraycopy(arr, i, tmp, 0, 4);
            out[i / 4] = bigEndianByteToInt(tmp);
        }
        return out;
    }

    private static byte[] convert(int[] arr) {
        byte[] out = new byte[arr.length * 4];
        byte[] tmp;
        for (int i = 0; i < arr.length; i++) {
            tmp = bigEndianIntToByte(arr[i]);
            System.arraycopy(tmp, 0, out, i * 4, 4);
        }
        return out;
    }

    public static int[] cf(int[] vBytes, int[] bBytes) {
        int a, b, c, d, e, f, g, h;
        int ss1, ss2, tt1, tt2;
        a = vBytes[0];
        b = vBytes[1];
        c = vBytes[2];
        d = vBytes[3];
        e = vBytes[4];
        f = vBytes[5];
        g = vBytes[6];
        h = vBytes[7];

        int[][] arr = expand(bBytes);
        int[] w = arr[0];
        int[] w1 = arr[1];

        for (int j = 0; j < 64; j++) {
            ss1 = (bitCycleLeft(a, 12) + e + bitCycleLeft(TJ[j], j));
            ss1 = bitCycleLeft(ss1, 7);
            ss2 = ss1 ^ bitCycleLeft(a, 12);
            tt1 = ffj(a, b, c, j) + d + ss2 + w1[j];
            tt2 = ggj(e, f, g, j) + h + ss1 + w[j];
            d = c;
            c = bitCycleLeft(b, 9);
            b = a;
            a = tt1;
            h = g;
            g = bitCycleLeft(f, 19);
            f = e;
            e = p0(tt2);

        }

        int[] out = new int[8];
        out[0] = a ^ vBytes[0];
        out[1] = b ^ vBytes[1];
        out[2] = c ^ vBytes[2];
        out[3] = d ^ vBytes[3];
        out[4] = e ^ vBytes[4];
        out[5] = f ^ vBytes[5];
        out[6] = g ^ vBytes[6];
        out[7] = h ^ vBytes[7];

        return out;
    }

    private static int[][] expand(int[] bBytes) {
        int[] w = new int[68];
        int[] w1 = new int[64];
        System.arraycopy(bBytes, 0, w, 0, bBytes.length);

        for (int i = 16; i < 68; i++) {
            w[i] = p1(w[i - 16] ^ w[i - 9] ^ bitCycleLeft(w[i - 3], 15)) ^ bitCycleLeft(w[i - 13], 7) ^ w[i - 6];
        }

        for (int i = 0; i < 64; i++) {
            w1[i] = w[i] ^ w[i + 4];
        }

        return new int[][]{w, w1};
    }

    private static byte[] bigEndianIntToByte(int num) {
        return back(ByteUtils.intToBytes(num));
    }

    private static int bigEndianByteToInt(byte[] bytes) {
        return ByteUtils.byteToInt(back(bytes));
    }

    private static int ffj(int x, int y, int z, int j) {
        if (j >= 0 && j <= 15) {
            return ff1j(x, y, z);
        } else {
            return ff2j(x, y, z);
        }
    }

    private static int ggj(int x, int y, int z, int j) {
        if (j >= 0 && j <= 15) {
            return gg1j(x, y, z);
        } else {
            return gg2j(x, z, z);
        }
    }

    private static int ff1j(int x, int y, int z) {
        return x ^ y ^ z;
    }

    private static int ff2j(int x, int y, int z) {
        return ((x & y) | (x & z) | (y & z));
    }

    private static int gg1j(int x, int y, int z) {
        return x ^ y ^ z;
    }

    private static int gg2j(int x, int y, int z) {
        return (x & y) | (~x & z);
    }

    private static int p0(int x) {
        int y;
        y = bitCycleLeft(x, 9);
        int z;
        z = bitCycleLeft(x, 17);
        return x ^ y ^ z;
    }

    private static int p1(int x) {
        return x ^ bitCycleLeft(x, 15) ^ bitCycleLeft(x, 23);
    }

    /**
     * �����һ�������ֽ�����padding
     *
     * @param in
     * @param bLen �������
     * @return
     */
    public static byte[] padding(byte[] in, int bLen) {
        int k = 448 - (8 * in.length + 1) % 512;
        if (k < 0) {
            k = 960 - (8 * in.length + 1) % 512;
        }
        k += 1;
        byte[] padd = new byte[k / 8];
        padd[0] = (byte) 0x80;
        long n = in.length * 8 + bLen * 512;
        byte[] out = new byte[in.length + k / 8 + 64 / 8];
        int pos = 0;
        System.arraycopy(in, 0, out, 0, in.length);
        pos += in.length;
        System.arraycopy(padd, 0, out, pos, padd.length);
        pos += padd.length;
        byte[] tmp = back(ByteUtils.longToBytes(n));
        System.arraycopy(tmp, 0, out, pos, tmp.length);
        return out;
    }

    /**
     * �ֽ���������
     *
     * @param in
     * @return
     */
    private static byte[] back(byte[] in) {
        byte[] out = new byte[in.length];
        for (int i = 0; i < out.length; i++) {
            out[i] = in[out.length - i - 1];
        }

        return out;
    }

    private static int bitCycleLeft(int n, int bitLen) {
        bitLen %= 32;
        byte[] tmp = bigEndianIntToByte(n);
        int byteLen = bitLen / 8;
        int len = bitLen % 8;
        if (byteLen > 0) {
            tmp = byteCycleLeft(tmp, byteLen);
        }

        if (len > 0) {
            tmp = bitSmall8CycleLeft(tmp, len);
        }

        return bigEndianByteToInt(tmp);
    }

    private static byte[] bitSmall8CycleLeft(byte[] in, int len) {
        byte[] tmp = new byte[in.length];
        int t1, t2, t3;
        for (int i = 0; i < tmp.length; i++) {
            t1 = (byte) ((in[i] & 0x000000ff) << len);
            t2 = (byte) ((in[(i + 1) % tmp.length] & 0x000000ff) >> (8 - len));
            t3 = (byte) (t1 | t2);
            tmp[i] = (byte) t3;
        }

        return tmp;
    }

    private static byte[] byteCycleLeft(byte[] in, int byteLen) {
        byte[] tmp = new byte[in.length];
        System.arraycopy(in, byteLen, tmp, 0, in.length - byteLen);
        System.arraycopy(in, 0, tmp, in.length - byteLen, byteLen);
        return tmp;
    }

    /**
     * SM2������
     *
     * @param out    ����SM2�ṹ�Ļ�����
     */
    public void doFinal(byte[] out) {
        byte[] tmp = doFinal();
        System.arraycopy(tmp, 0, out, 0, tmp.length);
    }

    /**
     * ��������
     *
     * @param in    �������뻺����
     * @param inOff ������ƫ����
     * @param len   ���ĳ���
     */
    public void update(byte[] in, int inOff, int len) {
        int partLen = BUFFER_LENGTH - xBufOff;
        int inputLen = len;
        int dPos = inOff;
        if (partLen < inputLen) {
            System.arraycopy(in, dPos, xBuf, xBufOff, partLen);
            inputLen -= partLen;
            dPos += partLen;
            doUpdate();
            while (inputLen > BUFFER_LENGTH) {
                System.arraycopy(in, dPos, xBuf, 0, BUFFER_LENGTH);
                inputLen -= BUFFER_LENGTH;
                dPos += BUFFER_LENGTH;
                doUpdate();
            }
        }

        System.arraycopy(in, dPos, xBuf, xBufOff, inputLen);
        xBufOff += inputLen;
    }

    private void doUpdate() {
        byte[] bBytes = new byte[BLOCK_LENGTH];
        for (int i = 0; i < BUFFER_LENGTH; i += BLOCK_LENGTH) {
            System.arraycopy(xBuf, i, bBytes, 0, bBytes.length);
            doHash(bBytes);
        }
        xBufOff = 0;
    }

    private void doHash(byte[] bBytes) {
        byte[] tmp = cf(initV, bBytes);
        System.arraycopy(tmp, 0, initV, 0, initV.length);
        cntBlock++;
    }

    private byte[] doFinal() {
        byte[] bBytes = new byte[BLOCK_LENGTH];
        byte[] buffer = new byte[xBufOff];
        System.arraycopy(xBuf, 0, buffer, 0, buffer.length);
        byte[] tmp = padding(buffer, cntBlock);
        for (int i = 0; i < tmp.length; i += BLOCK_LENGTH) {
            System.arraycopy(tmp, i, bBytes, 0, bBytes.length);
            doHash(bBytes);
        }
        return initV;
    }

    public void update(byte in) {
        byte[] buffer = new byte[]{in};
        update(buffer, 0, 1);
    }

}
