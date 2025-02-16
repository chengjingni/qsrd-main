package com.vdata.cloud.datacenter.util;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import com.vdata.cloud.common.util.CommonUtil;
import com.vdata.cloud.datacenter.entity.RsaKey;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

@Slf4j
public class RSAEncrypt {

    public static final String KEY_ALGORITHM = "RSA";
    public static final int RAS_KEY_SIZE = 2048;
    public static final String KEY_PATH = "key/";
    public static final String PRI_PATH = "privateKey.keystore";
    public static final String PUB_PATH = "publicKey.keystore";


    /**
     * 私钥解密（公钥加密）
     *
     * @param encryptPlainText
     * @param privateKeyStr
     * @return
     */
    public static String decryptPlainText(String encryptPlainText, String privateKeyStr) {
        try {
            RSAPrivateKey privateKey = loadPrivateKeyByStr(privateKeyStr);
            String decryptPlainText = new String(decrypt(privateKey, Base64.decode(encryptPlainText)));
            return decryptPlainText;
        } catch (Exception e) {
            log.error("私钥解密出错！", e);
            return encryptPlainText;
        }
    }

    /**
     * 公钥加密（私钥解密）
     *
     * @param plainText
     * @param publicKeyStr
     * @return
     */
    public static String encryptPlainText(String plainText, String publicKeyStr) {
        RSAPublicKey publicKey = null;
        try {
            publicKey = loadPublicKeyByStr(publicKeyStr);
            String encryptPlainText = new String(Base64.encode(encrypt(publicKey, plainText.getBytes())));
            return encryptPlainText;
        } catch (Exception e) {
            log.error("公钥加密出错！", e);
            return plainText;
        }
    }


    /**
     * 公钥解密（私钥加密）
     *
     * @param encryptPlainText
     * @param publicKeyStr
     * @return
     */
    public static String decryptPlainTextPub(String encryptPlainText, String publicKeyStr) {
        try {
            if (CommonUtil.isEmpty(encryptPlainText)) {
                return encryptPlainText;
            }
            RSAPublicKey publicKey = loadPublicKeyByStr(publicKeyStr);
            String decryptPlainText = new String(decrypt(publicKey, Base64.decode(encryptPlainText)));
            return decryptPlainText;
        } catch (Exception e) {
            log.error("公钥解密出错！", e);
            return encryptPlainText;
        }
    }

    /**
     * 私钥加密（公钥解密）
     *
     * @param plainText
     * @param privateKeyStr
     * @return
     */
    public static String encryptPlainTextPri(String plainText, String privateKeyStr) {
        RSAPrivateKey privateKey = null;
        try {
            if (CommonUtil.isEmpty(plainText)) {
                return plainText;
            }
            privateKey = loadPrivateKeyByStr(privateKeyStr);
            String encryptPlainText = new String(Base64.encode(encrypt(privateKey, plainText.getBytes())));
            return encryptPlainText;
        } catch (Exception e) {
            log.error("私钥加密出错！", e);
            return plainText;
        }
    }

    public static RsaKey genKeyPair() throws Exception {
        // KeyPairGenerator类用于生成公钥和私钥对，基于RSA算法生成对象
        KeyPairGenerator keyPairGen = null;
        try {
            keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        keyPairGen.initialize(RAS_KEY_SIZE, new SecureRandom());
        // 生成一个密钥对，保存在keyPair中
        KeyPair keyPair = keyPairGen.generateKeyPair();
        // 得到私钥
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        // 得到公钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        // 得到公钥字符串
        String publicKeyString = new String(Base64.encode(publicKey.getEncoded()));
        // 得到私钥字符串
        String privateKeyString = new String(Base64.encode(privateKey.getEncoded()));
        return new RsaKey(publicKeyString, privateKeyString);
    }

    /**
     * 从文件中输入流中加载公钥
     *
     * @param path 公钥输入流
     * @throws Exception 加载公钥时产生的异常
     */
    public static String loadPublicKeyByFile(String path) throws Exception {
        try {
            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader(path + PUB_PATH));
            } catch (FileNotFoundException e) {
                br = new BufferedReader(new FileReader(RSAEncrypt.KEY_PATH + PUB_PATH));
            }
            String readLine = null;
            StringBuilder sb = new StringBuilder();
            while ((readLine = br.readLine()) != null) {
                sb.append(readLine);
            }
            br.close();
            return sb.toString();
        } catch (IOException e) {
            log.error("公钥数据读取错误[path:" + path + "]", e);
            throw new Exception("公钥数据流读取错误");
        } catch (NullPointerException e) {
            throw new Exception("公钥输入流为空");
        }
    }

    /**
     * 从字符串中加载公钥
     *
     * @param publicKeyStr 公钥数据字符串
     * @throws Exception 加载公钥时产生的异常
     */
    public static RSAPublicKey loadPublicKeyByStr(String publicKeyStr) throws Exception {
        try {
            byte[] buffer = Base64.decode(publicKeyStr);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
            return (RSAPublicKey) keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此算法");
        } catch (InvalidKeySpecException e) {
            throw new Exception("公钥非法");
        } catch (NullPointerException e) {
            throw new Exception("公钥数据为空");
        }
    }

    /**
     * 从文件中加载私钥
     *
     * @param path 私钥文件名
     * @return 是否成功
     * @throws Exception
     */
    public static String loadPrivateKeyByFile(String path) throws Exception {
        try {
            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader(path + PRI_PATH));
            } catch (FileNotFoundException e) {
                br = new BufferedReader(new FileReader(RSAEncrypt.KEY_PATH + PRI_PATH));
            }
            String readLine = null;
            StringBuilder sb = new StringBuilder();
            while ((readLine = br.readLine()) != null) {
                sb.append(readLine);
            }
            br.close();
            return sb.toString();
        } catch (IOException e) {
            log.error("私钥数据读取错误[path:" + path + "]", e);
            throw new Exception("私钥数据读取错误");
        } catch (NullPointerException e) {
            throw new Exception("私钥输入流为空");
        }
    }

    /**
     * 根据私钥字符串转化得到私钥对象
     *
     * @param privateKeyStr
     * @return
     * @throws Exception
     */
    public static RSAPrivateKey loadPrivateKeyByStr(String privateKeyStr) throws Exception {
        try {
            byte[] buffer = Base64.decode(privateKeyStr);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此算法");
        } catch (InvalidKeySpecException e) {
            throw new Exception("私钥非法");
        } catch (NullPointerException e) {
            throw new Exception("私钥数据为空");
        }
    }

    /**
     * 公钥加密过程
     *
     * @param publicKey     公钥
     * @param plainTextData 明文数据
     * @return
     * @throws Exception 加密过程中的异常信息
     */
    public static byte[] encrypt(RSAPublicKey publicKey, byte[] plainTextData) throws Exception {
        if (publicKey == null) {
            throw new Exception("加密公钥为空, 请设置");
        }
        Cipher cipher = null;
        try {
            // 使用默认RSA
            cipher = Cipher.getInstance(KEY_ALGORITHM);
            // cipher= Cipher.getInstance(KEY_ALGORITHM, new
            // BouncyCastleProvider());
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] output = cipher.doFinal(plainTextData);
            return output;
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此加密算法");
        } catch (NoSuchPaddingException e) {
            return null;
        } catch (InvalidKeyException e) {
            throw new Exception("加密公钥非法,请检查");
        } catch (IllegalBlockSizeException e) {
            throw new Exception("明文长度非法");
        } catch (BadPaddingException e) {
            throw new Exception("明文数据已损坏");
        }
    }

    /**
     * 私钥加密过程
     *
     * @param privateKey    私钥
     * @param plainTextData 明文数据
     * @return
     * @throws Exception 加密过程中的异常信息
     */
    public static byte[] encrypt(RSAPrivateKey privateKey, byte[] plainTextData) throws Exception {
        if (privateKey == null) {
            throw new Exception("加密私钥为空, 请设置");
        }
        Cipher cipher = null;
        try {
            // 使用默认RSA
            cipher = Cipher.getInstance(KEY_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            byte[] output = cipher.doFinal(plainTextData);
            return output;
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此加密算法");
        } catch (NoSuchPaddingException e) {
            return null;
        } catch (InvalidKeyException e) {
            throw new Exception("加密私钥非法,请检查");
        } catch (IllegalBlockSizeException e) {
            throw new Exception("明文长度非法");
        } catch (BadPaddingException e) {
            throw new Exception("明文数据已损坏");
        }
    }

    /**
     * 私钥解密过程
     *
     * @param privateKey 私钥
     * @param cipherData 密文数据
     * @return 明文
     * @throws Exception 解密过程中的异常信息
     */
    public static byte[] decrypt(RSAPrivateKey privateKey, byte[] cipherData) throws Exception {
        if (privateKey == null) {
            throw new Exception("解密私钥为空, 请设置");
        }
        Cipher cipher = null;
        try {
            // 使用默认RSA
            cipher = Cipher.getInstance(KEY_ALGORITHM);
            // cipher= Cipher.getInstance(KEY_ALGORITHM, new
            // BouncyCastleProvider());
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] output = cipher.doFinal(cipherData);
            return output;
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此解密算法");
        } catch (NoSuchPaddingException e) {
            return null;
        } catch (InvalidKeyException e) {
            throw new Exception("解密私钥非法,请检查");
        } catch (IllegalBlockSizeException e) {
            throw new Exception("密文长度非法");
        } catch (BadPaddingException e) {
            throw new Exception("密文数据已损坏");
        }
    }

    /**
     * 公钥解密过程
     *
     * @param publicKey  公钥
     * @param cipherData 密文数据
     * @return 明文
     * @throws Exception 解密过程中的异常信息
     */
    public static byte[] decrypt(RSAPublicKey publicKey, byte[] cipherData) throws Exception {
        if (publicKey == null) {
            throw new Exception("解密公钥为空, 请设置");
        }
        Cipher cipher = null;
        try {
            // 使用默认RSA
            cipher = Cipher.getInstance(KEY_ALGORITHM);
            // cipher= Cipher.getInstance(KEY_ALGORITHM, new
            // BouncyCastleProvider());
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            byte[] output = cipher.doFinal(cipherData);
            return output;
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此解密算法");
        } catch (NoSuchPaddingException e) {
            return null;
        } catch (InvalidKeyException e) {
            throw new Exception("解密公钥非法,请检查");
        } catch (IllegalBlockSizeException e) {
            throw new Exception("密文长度非法");
        } catch (BadPaddingException e) {
            throw new Exception("密文数据已损坏");
        }
    }

}