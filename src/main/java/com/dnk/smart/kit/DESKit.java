package com.dnk.smart.kit;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import static javax.crypto.Cipher.DECRYPT_MODE;

/**
 * 加密解密
 */
//TODO
public class DESKit {

    //加密向量
    private static final byte[] PARAMS = {0x63, 0x6C, 0x69, 0x63, 0x6B, 0x64, 0x65, 0x73, 0x63, 0x72, 0x69, 0x62, 0x65, 0x66, 0x69, 0x6E};
    //算法种子
    private static final byte[] KEYS = {0x61, 0x65, 0x73, 0x4C, 0x65, 0x6E, 0x67, 0x74, 0x68, 0x41, 0x6C, 0x69, 0x67, 0x6E, 0x73, 0x31};
    //算法类型
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/NoPadding";

    /**
     * 信息摘要算法(指定算法),不可逆加密
     *
     * @param input     输入数据
     * @param algorithm 算法类型
     * @return 加密结果 upper hex string
     */
    public static String digest(String input, String algorithm) {
        if (ValidateKit.isEmpty(input)) {
            return null;
        }
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
            byte[] src = input.getBytes(StandardCharsets.UTF_8);
            byte[] dest = messageDigest.digest(src);
            return ByteKit.bytesToHex(dest);
            //return new BigInteger(1, dest).toString(16);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * MD5加密
     */
    public static String digestMd5(String str) {
        return digest(str, "MD5");
    }

    /**
     * 加密解密
     *
     * @param input               待加密/解密的字节数组
     * @param mode                加密/解密 ENCRYPT_MODE(1),DECRYPT_MODE(2)
     * @param transformation      算法转换的名称
     * @param key                 密钥字节数组
     * @param algorithm           与给定的密钥内容相关联的密钥算法的名称
     * @param algorithmParameters 密码参数
     * @return 加密/解密后的数据
     */
    public static byte[] encipher(byte[] input, int mode, String transformation, byte[] key, String algorithm, byte[] algorithmParameters) {
        //提供默认设置
        transformation = ValidateKit.isEmpty(transformation) ? TRANSFORMATION : transformation;
        key = (key == null || key.length == 0) ? KEYS : key;
        algorithm = ValidateKit.isEmpty(algorithm) ? ALGORITHM : algorithm;
        algorithmParameters = (algorithmParameters == null || algorithmParameters.length == 0) ? PARAMS : algorithmParameters;

        try {
            Cipher cipher = Cipher.getInstance(transformation);
            cipher.init(mode, new SecretKeySpec(key, algorithm), new IvParameterSpec(algorithmParameters));
            return cipher.doFinal(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * fill byte to 16n bit
     */
    public static byte[] encrypt(byte[] input) {
        return encipher(ByteKit.fillZero(input), Cipher.ENCRYPT_MODE, null, null, null, null);
    }

    //TODO:trim???
    public static byte[] decrypt(byte[] input) {
        return ByteKit.trim(encipher(input, DECRYPT_MODE, null, null, null, null));
    }

    //TODO:ByteKit.hexToBytes
    public static byte[] decrypt(String input) {
        byte[] bytes = new BigInteger(input, 16).toByteArray();
        return encipher(bytes, DECRYPT_MODE, null, null, null, null);
    }

}
