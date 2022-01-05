package com.vdata.cloud.datacenter.util;

import com.vdata.cloud.common.util.CommonUtil;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *  * @ProjectName:    wru-master
 *  * @Package:        com.vdata.cloud.datacenter.util
 *  * @ClassName:      CheckUtil
 *  * @Author:         Xubo
 *  * @Description:    后端校验工具类
 *  * @Date:            2020/9/15 13:39
 *  * @Version:    1.0
 *  
 */
public class CheckUtil {

    /**
     * 日期格式校验
     *
     * @param value
     * @param format
     * @return
     */
    public static boolean checkDate(String value, String format) {
        if (CommonUtil.isEmpty(value)) {
            return true;
        }
        format = CommonUtil.nvl(format, "yyyy-MM-dd");
        format = CommonUtil.decode(format, "3", "yyyy-MM-dd", "4", "yyyy-MM-dd HH:mm:ss", format);
        String regex = "";
        if ("yyyy-MM-dd".equals(format)) {
            regex = "^((([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))-02-29))$";
            if (value.length() > 9 && value.endsWith("00:00:00")) {
                value = value.substring(0, value.length() - 8).trim();
            }
        } else if ("yyyy-MM-dd HH:mm:ss".equals(format)) {
            regex = "^((([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))-02-29))\\s+([0-1]?[0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])$";
        } else if ("HH:mm:ss".equals(format)) {
            regex = "([0-1]?[0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])$";
        }
        if (!Pattern.matches(regex, value)) {
            return false;
        }
        return true;
    }

    /**
     * 布尔类型校验
     *
     * @param value
     * @return
     */
    public static boolean checkBool(Object value) {
        if (value instanceof String) {
            return false;
        }
        String val = value + "";
        return "0".equals(val) || "1".equals(val);
    }

    /**
     * 数值格式校验
     *
     * @param value
     * @param checkRule
     * @return
     */
    public static boolean checkNumber(String value, String checkRule) {
        try {
            int num1 = CommonUtil.getStringInt(checkRule, 0, ",");
            int num2 = CommonUtil.getStringInt(checkRule, 1, ",");
            num1 = num1 - num2;
            String regex = "";
            if (num2 == 0) {
                regex = "^([0-9]{0," + num1 + "})$";
            } else {
                regex = "^([0-9]{0," + num1 + "})|([0-9]{0," + num1 + "}\\.[0-9]{1," + num2 + "})$";
            }
            if (!Pattern.matches(regex, value)) {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    /**
     * 是否满足正则表达式
     *
     * @param value
     * @param regex
     * @return
     */
    public static boolean checkRegex(String regex, String value) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(value);
        boolean isMatch = m.matches();
        return isMatch;
    }

    /**
     * 是否电话号码(固定电话/手机号码)
     *
     * @param phone
     * @return
     */
    public static boolean isPhone(String phone) {
        return isTelephone(phone) || isMobile(phone);
    }

    /**
     * 是否固定电话
     *
     * @param mobile
     * @return
     */
    public static boolean isTelephone(String mobile) {
        String regex1 = "^[0][1-9]{2,3}-[0-9]{5,10}$";// 验证带区号的
        String regex2 = "^[1-9]{1}[0-9]{5,8}$";// 验证没有区号的
        return checkRegex(regex1, mobile) || checkRegex(regex2, mobile);
    }


    /**
     * 验证手机号码
     *
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean isMobile(String mobile) {
        if (mobile.length() != 11) {
            return false;
        }
        String regex = "^1[2|3|4|5|6|7|8|9][0-9]\\d{4,8}$";
        return checkRegex(regex, mobile);
    }

    /**
     * @Title: isEmail @Description:判断邮箱 @param str 设定字符 @return 返回结果 @throws
     */
    public static boolean isEmail(String email) {
        String regex = "^([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)*@([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)+[\\.][A-Za-z]{2,3}([\\" + "" +
                ".][A-Za-z]{2})?$";
        return checkRegex(regex, email);
    }

    /**
     * 验证字符串是否包含中文
     */
    public static boolean isContainChinese(String str) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        return m.find();
    }

    /**
     * 身份证号校验（包含15位和18位身份证号校验）
     *
     * @param idcard
     * @return
     */
    public static boolean isIdCard(String idcard) {
        return IDCardUtils.validateCard(idcard);
    }


    /**
     * 判断集合元素是否相等（无序）
     */
    public static boolean isEqualCollection(Collection a, Collection b) {

        if (a.size() != b.size()) {  // size是最简单的相等条件
            return false;
        }
        Map mapa = getCardinalityMap(a);
        Map mapb = getCardinalityMap(b);

        // 转换map后，能去掉重复的，这时候size就是非重复项，也是先决条件
        if (mapa.size() != mapb.size()) {
            return false;
        }
        Iterator it = mapa.keySet().iterator();
        while (it.hasNext()) {
            Object obj = it.next();
            // 查询同一个obj，首先两边都要有，而且还要校验重复个数，就是map.value
            if (getFreq(obj, mapa) != getFreq(obj, mapb)) {
                return false;
            }
        }
        return true;
    }

    public static Map getCardinalityMap(Collection coll) {
        Map count = new HashMap();
        for (Iterator it = coll.iterator(); it.hasNext(); ) {
            Object obj = it.next();
            Integer c = (Integer) count.get(obj);
            if (c == null)
                count.put(obj, 1);
            else {
                count.put(obj, new Integer(c.intValue() + 1));
            }
        }
        return count;
    }

    private static final int getFreq(Object obj, Map freqMap) {
        Integer count = (Integer) freqMap.get(obj);
        if (count != null) {
            return count.intValue();
        }
        return 0;
    }


}
