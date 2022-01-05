package com.vdata.cloud.datacenter.util;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import com.vdata.cloud.common.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;

import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;


/**
 * RSA签名验签类
 */
@Slf4j
public class RSASignature {

    /**
     * 签名算法
     */
    public static final String SIGN_ALGORITHMS = "SHA256withRSA";
    public static final String ENCODE_ALGORITHM = "SHA-256";


    /**
     * 将keystore文件转化为pem文件
     *
     * @param key
     * @return
     */
    private static String convertKeystoreToPem(String key) {
        StringBuffer pemKey = new StringBuffer("-----BEGIN PUBLIC KEY-----\n");
        int len = key.length();
        int total = (int) Math.ceil(len / 64.0);
        for (int i = 0; i < total; i++) {
            int end = (i + 1) * 64;
            end = end > len ? len : end;
            pemKey.append(key.substring(i * 64, end));
            if (i < total - 1) {
                pemKey.append("\n");
            }
        }
        pemKey.append("-----END PUBLIC KEY-----\n");
        return pemKey.toString();
    }

    /**
     * 先将密文加上摘要然后进行签名
     *
     * @param content
     * @return
     */
    public static String signSha(String content, String subPath) {
        try {
            String keyPath = RSAEncrypt.KEY_PATH;
            if (CommonUtil.isNotEmpty(subPath)) {
                keyPath = RSAEncrypt.KEY_PATH + subPath + "/";
            }
            String privateKey = RSAEncrypt.loadPrivateKeyByFile(keyPath);
            return addSign(content, privateKey);
        } catch (Exception e) {
            log.error("数字签名出错！", e);
        }
        return null;
    }

    /**
     * 添加数字签名
     *
     * @param content
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static String addSign(String content, String privateKey) throws Exception {
        PrivateKey priKey = RSAEncrypt.loadPrivateKeyByStr(privateKey);
        MessageDigest messageDigest = MessageDigest.getInstance(ENCODE_ALGORITHM);
        messageDigest.update(content.getBytes());
        byte[] signByte = messageDigest.digest();
        Signature signature = Signature.getInstance(SIGN_ALGORITHMS);
        signature.initSign(priKey);
        signature.update(signByte);
        byte[] signed = signature.sign();
        return new String(Base64.encode(signed));
    }


    /**
     * 数字签名验证
     *
     * @param content
     * @param sign
     * @return
     */
    public static boolean doCheckSha(String content, String sign) {
        return doCheckSha((String) content, (String) sign, (String) null);
    }

    /**
     * 数字签名验证
     *
     * @param content
     * @param sign
     * @param subPath
     * @return
     */
    public static boolean doCheckSha(String content, String sign, String subPath) {
        try {
            String keyPath = RSAEncrypt.KEY_PATH;
            if (CommonUtil.isNotEmpty(keyPath)) {
                keyPath = RSAEncrypt.KEY_PATH + subPath + "/";
            }
            String publicKey = RSAEncrypt.loadPublicKeyByFile(keyPath);
            return checkSign(content, sign, publicKey);
        } catch (Exception e) {
            log.error("密钥验证出错!", e);
        }
        return false;
    }

    /**
     * 数字签名验证
     *
     * @param content
     * @param sign
     * @param publicKey
     * @return
     * @throws Exception
     */
    public static boolean checkSign(String content, String sign, String publicKey) {
        try {
            PublicKey pubKey = RSAEncrypt.loadPublicKeyByStr(publicKey);
            MessageDigest messageDigest = MessageDigest.getInstance(ENCODE_ALGORITHM);
            messageDigest.update(content.getBytes());
            byte[] signByte = messageDigest.digest();
            Signature signature = Signature.getInstance(SIGN_ALGORITHMS);
            signature.initVerify(pubKey);
            signature.update(signByte);
            boolean bverify = signature.verify(Base64.decode(sign));
            return bverify;
        } catch (Exception e) {
            log.error("数字签名验证出错!", e);
        }
        return false;
    }

    public static String toHexString(byte[] byteArray) {
        if (byteArray == null || byteArray.length < 1)
            throw new IllegalArgumentException("this byteArray must not be null or empty");

        final StringBuilder hexString = new StringBuilder();
        for (int i = 0; i < byteArray.length; i++) {
            if ((byteArray[i] & 0xff) < 0x10)//0~F前面不零
                hexString.append("0");
            hexString.append(Integer.toHexString(0xFF & byteArray[i]));
        }
        return hexString.toString().toLowerCase();
    }

    /**
     * RSA验签名检查
     *
     * @param content   待签名数据
     * @param sign      签名值
     * @param publicKey 分配给开发商公钥
     * @param encode    字符集编码
     * @return 布尔值
     */
    public static boolean doCheck(String content, String sign, String publicKey, String encode) {
        try {
            PublicKey pubKey = RSAEncrypt.loadPublicKeyByStr(publicKey);
            Signature signature = Signature.getInstance(SIGN_ALGORITHMS);
            signature.initVerify(pubKey);
            signature.update(content.getBytes(CommonUtil.nvl(encode, "UTF-8")));
            boolean bverify = signature.verify(Base64.decode(sign));
            return bverify;
        } catch (Exception e) {
            log.error("", e);
        }
        return false;
    }

}
