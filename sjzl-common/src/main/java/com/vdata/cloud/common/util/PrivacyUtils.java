package com.vdata.cloud.common.util;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.util.Arrays;

/**
 * 隐私处理工具类
 */
public class PrivacyUtils {

    public static String encrypt(String content,String key) {
            int length=content.length();
            Integer[] intArr=new Integer[length];
            for (int i = 0; i < length; i++) {
                int contentChr =content.charAt(i);
                int keyChr = key.charAt(i);
                intArr[i] = contentChr + keyChr;
            }
            String contentStr = Arrays.toString(intArr);
            contentStr=  contentStr.substring(1, contentStr.length()-1);
            contentStr = Base64Util.encode(contentStr);
            return contentStr;
    }


}
