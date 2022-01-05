package com.vdata.cloud.common.util;

import java.util.Arrays;

public class Base64Util {
    public static String encode(String data) {
        // BASE64Encoder encoder = new BASE64Encoder();
        // String encode = encoder.encode(data);
        // 从JKD 9开始rt.jar包已废除，从JDK 1.8开始使用java.util.Base64.Encoder
        java.util.Base64.Encoder encoder = java.util.Base64.getEncoder();
        String encode = encoder.encodeToString(data.getBytes());
        return encode;
    }

    /**
     * BASE64Decoder 解密
     *
     * @param data 要解密的字符串
     * @return 解密后的byte[]
     * @throws Exception
     */
    public static String decode(String data) throws Exception {
        // BASE64Decoder decoder = new BASE64Decoder();
        // byte[] buffer = decoder.decodeBuffer(data);
        // 从JKD 9开始rt.jar包已废除，从JDK 1.8开始使用java.util.Base64.Decoder
        java.util.Base64.Decoder decoder = java.util.Base64.getDecoder();
        byte[] buffer = decoder.decode(data);
        return new String(buffer);

    }


    public static void main(String[] args) {
        String type="34765";
        int length=type.length();
        String code=CommonUtil.randomStr(length,true);
        Integer[] intArr=new Integer[length];
        for (int i = 0; i < length; i++) {
            int typeChr =type.charAt(i);
            int codeChr = code.charAt(i);
            intArr[i] = typeChr * codeChr;
        }
        String typeStr = Arrays.toString(intArr); //方法将数组转换为字符串[1, 2, 3, 4]
        typeStr = Base64Util.encode(typeStr.substring(1, typeStr.length()-1));//去掉手尾的中括号，即得到想要的结果
        System.out.println(typeStr);
    }
}
