package com.vdata.cloud.common.util;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * <p>
 * 通用工具类
 * </p>
 *
 * @author xubo
 * @since 2019-12-11
 */
@Slf4j
public class CommonUtil {

    private CommonUtil() {
        throw new IllegalStateException("CommonUtil class");
    }

    private static String dot = ".";
    private static Pattern linePattern = Pattern.compile("_(\\w)");
    private static Pattern camelPattern = Pattern.compile("[A-Z]");

    /**
     * 判断对象是否为空
     *
     * @param obj
     * @return
     */
    public static boolean isEmpty(Object obj) {
        if (obj == null) {
            return true;
        }
        if (obj instanceof List) {
            List list = (List) obj;
            return list.isEmpty();
        } else if (obj instanceof Map) {
            Map map = (Map) obj;
            return map.isEmpty();
        } else if (obj instanceof String) {
            String str = (String) obj;
            return str.length() == 0;
        } else if (obj instanceof StringBuffer) {
            StringBuffer str = (StringBuffer) obj;
            return str.length() == 0;
        } else if (obj instanceof StringBuilder) {
            StringBuilder str = (StringBuilder) obj;
            return str.length() == 0;
        } else if (obj instanceof String[]) {
            String[] arr = (String[]) obj;
            return arr.length == 0;
        } else {
            return false;
        }
    }

    /**
     * 判断对象是否不为空
     *
     * @param obj
     * @return
     */
    public static boolean isNotEmpty(Object obj) {
        return !isEmpty(obj);
    }

    /**
     * 如果第一个参数值为空，则返回第二个默认参数值
     *
     * @param value
     * @param nullDefault
     * @param <T>
     * @return
     */
    public static <T> T nvl(Object value, T nullDefault) {
        if (isEmpty(value)) {
            return nullDefault;
        }
        return (T) value;
    }

    /**
     * 如果第一个参数值不为空，则返回第二个参数，否则返回第三个参数
     *
     * @param value
     * @param value2
     * @param nullDefault
     * @param <T>
     * @return
     */
    public static <T> T nvl(Object value, Object value2, T nullDefault) {
        if (isEmpty(value)) {
            return nullDefault;
        }
        return (T) value2;
    }

    /**
     * 如果value不为空则返回value2
     *
     * @param value
     * @param value2
     * @param <T>
     * @return
     */
    public static <T> T nvln(Object value, Object value2) {
        if (isEmpty(value)) {
            return null;
        }
        return (T) value2;
    }

    /**
     * 将实体类里面为空的字符串字段转化为空串，整数转化为0
     *
     * @param bean
     * @param <T>
     * @return
     */
    public static <T> T nvl(T bean) {
        try {
            if (isEmpty(bean)) {
                return bean;
            }
            Field[] field = bean.getClass().getDeclaredFields();
            setFieldDefault(bean, field);
            Class superClass = bean.getClass().getSuperclass();
            if (superClass != null) {
                field = superClass.getDeclaredFields();
                setFieldDefault(bean, field);
            }
            return bean;
        } catch (Exception e) {
            return bean;
        }
    }


    /**
     * 将实体类里面为空的字符串字段转化为空串，整数转化为0
     *
     * @param bean
     * @param <T>
     * @return
     */
    public static <T> void setFieldDefault(T bean, Field[] field) {
        for (int j = 0; j < field.length; j++) {     //遍历所有属性
            String name = field[j].getName();    //获取属性的名字
            if ("SerialVersionUID".equalsIgnoreCase(name))
                continue;
            if ("ascs".equalsIgnoreCase(name))
                continue;
            if ("descs".equalsIgnoreCase(name))
                continue;
            //将属性的首字符大写，方便构造get，set方法
            name = name.substring(0, 1).toUpperCase() + name.substring(1);
            String type = field[j].getGenericType().toString();    //获取属性的类型
            try {
                if (type.equals("boolean")) {
                    continue;
                }
                Method mGet = bean.getClass().getMethod("get" + name);
                if (type.equals("class java.lang.String")) {   //字符串默认为""
                    String value = (String) mGet.invoke(bean);
                    if (isEmpty(value)) {
                        Method mSet = bean.getClass().getMethod("set" + name, String.class);
                        mSet.invoke(bean, "");
                    }
                } /*else if (type.equals("class java.lang.Integer")) {//整数默认为0
                    Number value = (Number) mGet.invoke(bean);
                    if (isEmpty(value)) {
                        Method mSet = bean.getClass().getMethod("set" + name, new Class[]{Integer.class});
                        mSet.invoke(bean, 0);
                    }
                } else if (type.equals("class java.math.BigDecimal")) {//小数默认为0.0
                    Number value = (Number) mGet.invoke(bean);
                    if (isEmpty(value)) {
                        Method mSet = bean.getClass().getMethod("set" + name, new Class[]{BigDecimal.class});
                        mSet.invoke(bean, new BigDecimal(0.0));
                    }
                }*/
            } catch (Exception e) {
                log.error("设置null为空字符串出错:" + e.getMessage());
            }
        }
    }

    /**
     * 将集合里面的实体类为空的字符串字段转化为空串
     *
     * @param beanList
     * @param <T>
     * @return
     */
    public static <T> List<T> nvl(List<T> beanList) {
        try {
            if (isEmpty(beanList)) {
                return beanList;
            }
            beanList.forEach(CommonUtil::nvl);
            return beanList;
        } catch (Exception e) {
            return beanList;
        }
    }

    /**
     * 如果第一个参数等于偶数索引的值则返回下一个索引值，类似于Oracle的decode函数
     *
     * @param arr
     * @return
     * @throws Exception
     */
    public static <T> T decode(T... arr) {
        if (isNotEmpty(arr) && arr.length > 2) {
            T field = arr[0];
            for (int i = 1; i < arr.length - 1; i += 2) {
                if (field.equals(arr[i])) {
                    return arr[i + 1];
                }
            }
            if (arr.length % 2 == 0) {
                return arr[arr.length - 1];
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public static <T> T decode2(T param1, T param2, T param3) {
        if (param1.equals(param2)) {
            return param3;
        } else {
            return param1;
        }
    }

    /**
     * 根据类名对传入的字符串值进行转化后返回
     *
     * @param className
     * @param value
     * @return
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */

    public static <T> T parseValue(String className, String value) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        if (isEmpty(value)) {
            return null;
        }
        if ("String".equals(nvl(className, "String"))) {
            return (T) value;
        } else if ("BigDecimal".equals(className)) {
            return (T) new BigDecimal(value);
        } else if ("Date".equals(className)) {
            return (T) format.parse(value);
        } else {
            Class<?> tClass = Class.forName("java.lang." + className);
            className = decode(className, "Integer", "Int", className);
            Method m = tClass.getDeclaredMethod("parse" + className, String.class);
            if ("Int".equals(className) && value.indexOf(dot) > -1) {
                value = value.substring(0, value.indexOf(dot));
            }
            return (T) m.invoke(tClass, value);
        }
    }

    /**
     * 通过反射机制动态获取对象属性的值
     *
     * @param model
     * @param field
     * @return String
     */
    public static <T> T getFieldValue(Object model, String field) {
        try {
            if (isEmpty(field)) {
                return null;
            }
            field = field.substring(0, 1).toUpperCase() + field.substring(1);
            Method m = model.getClass().getMethod("get" + field);
            return (T) m.invoke(model);
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    /**
     * 通过反射机制动态给对象属性的赋值
     *
     * @param model
     * @param field
     * @param value
     * @param <T>
     */
    public static <T> void setFieldValue(Object model, String field, T value) {
        try {
            if (isEmpty(field)) {
                return;
            }
            if (value == null) {
                return;
            }
            field = field.substring(0, 1).toUpperCase() + field.substring(1);
            Method m = model.getClass().getMethod("set" + field, value.getClass());
            m.invoke(model, value);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 根据类名构建对象实例
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T newObject(String clazz) {
        try {
            if (clazz.indexOf(dot) == -1) {
                clazz = "com.vdata.cloud.datacenter.entity." + clazz;
            }
            //根据类名获取Class对象
            Class c = Class.forName(clazz);
            //根据参数类型获取相应的构造函数
            Constructor constructor = c.getConstructor();
            //根据获取的构造函数和参数，创建实例
            return (T) constructor.newInstance();
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    /**
     * 设置用户编号
     *
     * @param bean
     * @param userId
     */
    public static void setUserId(Object bean, String userId) {
        try {
            CommonUtil.setFieldValue(bean, "crtStffId", userId);
            CommonUtil.setFieldValue(bean, "modStffId", userId);
        } catch (Exception e) {
            log.error("设置用户编号出错！", e);
        }
    }


    /**
     * 动态Java编译执行
     *
     * @param code
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> T eval(String code) {
        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine se = manager.getEngineByName("js");
            return (T) se.eval(code);
        } catch (Exception e) {
            log.info("code:" + code);
            log.error("动态Java执行出错!", e);
        }
        return null;
    }


    /**
     * Map转成实体对象
     *
     * @param map   map实体对象包含属性
     * @param clazz 实体对象类型
     * @return
     */
    public static <T> T map2Object(Map<String, Object> map, Class<T> clazz) {
        if (map == null) {
            return null;
        }
        T obj = null;
        try {
            obj = clazz.newInstance();
            Field[] fields = obj.getClass().getDeclaredFields();
            for (Field field : fields) {
                String filedTypeName = field.getType().getName();
                try {
                    int mod = field.getModifiers();
                    if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
                        continue;
                    }
                    field.setAccessible(true);
                    if (filedTypeName.equalsIgnoreCase("java.util.date")) {
                        String datetimestamp = String.valueOf(map.get(field.getName()));
                        if (datetimestamp.equalsIgnoreCase("null")) {
                            field.set(obj, null);
                        } else {
                            field.set(obj, new Date(Long.parseLong(datetimestamp)));
                        }
                    } else {
                        field.set(obj, map.get(field.getName()));
                    }
                } catch (IllegalArgumentException e) {
                    if (filedTypeName.equalsIgnoreCase("java.lang.integer")) {
                        String value = map.get(field.getName()) + "";
                        field.set(obj, Integer.parseInt(value));
                    } else if (filedTypeName.equalsIgnoreCase("java.lang.long")) {
                        String value = map.get(field.getName()) + "";
                        field.set(obj, Long.parseLong(value));
                    }
                } catch (Exception e) {
                    log.error("字段赋值出错！{}", field.getName() + ":" + map.get(field.getName()));
                }
            }
        } catch (Exception e) {
            log.error("将Map转化为实体类出错！", e);
        }
        return obj;
    }


    /**
     * 实体对象转成Map
     *
     * @param obj 实体对象
     * @return
     */
    public static Map<String, Object> object2Map(Object obj) {
        Map<String, Object> map = new HashMap<>();
        if (obj == null) {
            return map;
        }
        Class clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();
        try {
            for (Field field : fields) {
                field.setAccessible(true);
                map.put(field.getName(), field.get(obj));
            }
        } catch (Exception e) {
            log.error("实体对象转成Map出错！", e);
        }
        return map;
    }

    /**
     * 根据传入的字段对Map的字段进行筛选
     *
     * @param map
     * @param fields
     * @return
     */
    public static Map<String, Object> mapFilter(Map<String, Object> map, String[] fields) {
        if (isEmpty(map) || isEmpty(fields)) {
            return map;
        }
        try {
            List<String> list = Arrays.asList(fields);
            map.forEach((k, v) -> {
                if (!list.contains(k)) {
                    map.remove(k);
                }
            });
        } catch (Exception e) {
            log.error("Map过滤出错！", e);
        }
        return map;
    }

    /**
     * 获取随机序列
     *
     * @param pre
     * @param num
     * @return
     */
    public static String getRandomID(String pre, int num) {
        String id = pre + RandomStringUtils.random(num, "QWERTYUIOPASDFGHJKLZXCVBNM0123456789");
        return id;
    }

    /**
     * 当位数不足时向左边补零来达到对应的位数
     *
     * @param str
     * @param num
     * @return
     */
    public static String lpad(String str, int num) {
        if (num < 1) {
            return str;
        }
        if (str == null) {
            str = "";
        }
        while (str.length() < num) {
            str = "0" + str;
        }
        return str;
    }

    /**
     * 当位数不足时向右边补零来达到对应的位数
     *
     * @param str
     * @param num
     * @return
     */
    public static String rpad(String str, int num) {
        if (num < 1) {
            return str;
        }
        if (str == null) {
            str = "";
        }
        while (str.length() < num) {
            str = str + "0";
        }
        return str;
    }

    /**
     * 获取当前时间
     *
     * @param format
     * @return
     */
    public static String getNowTime(String format) {
        if (format == null) {
            format = "yyyyMMddHHmmssSSS";
        }
        if ("".equals(format)) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat format1 = new SimpleDateFormat(format);
        String timeStr = format1.format(new Date());
        return timeStr;
    }


    /**
     * 将List转化为以,分割的字符串
     *
     * @param list
     * @return
     */
    public static String join(List<String> list) {
        if (isEmpty(list)) {
            return null;
        }
        StringBuilder sb = new StringBuilder("");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(list.get(i));
        }
        return sb.toString();
    }

    public static String join(Stream<String> stream) {
        if (isEmpty(stream)) {
            return null;
        }
        return StringUtils.join(stream.collect(Collectors.toList()), ",");
    }

    /**
     * 将字符串以分隔符分割，取第i个值
     *
     * @param str
     * @param n
     * @param separator
     * @return
     */
    public static Integer getStringInt(String str, int n, String separator) {
        if (isNotEmpty(str)) {
            String[] arr = str.split(separator);
            return Integer.parseInt(arr[n]);
        } else {
            return null;
        }
    }


    /**
     * 将实体类的相同的属性值赋值到一个新的实体类
     *
     * @param oriBean
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T, A> T copyBean(A oriBean, Class<T> clazz) {
        T newBean = BeanUtils.instantiateClass(clazz);
        BeanUtils.copyProperties(oriBean, newBean);
        return newBean;
    }

    /**
     * 年月日时分秒微秒+4位随机数
     */
    public static String getNumber() {
        StringBuilder StringBuilder = new StringBuilder();
        Date t = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        StringBuilder.append(df.format(t));
        int ran = (int) (Math.random() * 9000) + 1000;
        StringBuilder.append(String.valueOf(ran));
        return StringBuilder.toString();
    }

    /**
     * Map<K, V> 中k，v如果为null就转换
     */
    public static Map setNull(Map<String, Object> params) {

        Set<String> set = params.keySet();
        if (set != null && !set.isEmpty()) {
            for (String key : set) {
                params.putIfAbsent(key, "");
            }
        }
        return params;
    }


    /**
     * 列表关键字查询去首位空格及特殊符号转义
     */
    public static String convertKeyword(String keyword) {
        //模糊查询关键字
        String search = "";
        if (!StringUtils.isEmpty(keyword)) {
            keyword = keyword.trim();
            if (keyword.contains("%") || keyword.contains("_")) {
                search = keyword.trim().replaceAll("\\%", "\\\\%").replaceAll("\\_", "\\\\_");
            } else {
                search = keyword;
            }
        }
        return search;
    }


    public static String getPrefixNumber(String str) {
        if (isEmpty(str)) return str;
        String regEx = "[^0-9.]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        if (isEmpty(m)) return "";
        return m.replaceAll("").trim();
    }

    /**
     * 驼峰转下划线,最后转为大写
     *
     * @param str
     * @return
     */
    public static String camelToUnderline(String str) {
        Matcher matcher = camelPattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "_" + matcher.group(0).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString().toUpperCase();
    }

    /**
     * 下划线转驼峰,正常输出
     *
     * @param str
     * @return
     */
    public static String underlineToCamel(String str) {
        Matcher matcher = linePattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * Object对象向文字列变更。
     *
     * @param obj Object对象
     * @return 向文字
     */
    public static String objToStr(Object obj) {
        if (obj == null) {
            return "";
        } else {
            return String.valueOf(obj);
        }
    }

    /**
     * Object对象向文字列变更。
     *
     * @param obj Object对象
     * @return 向文字
     */
    public static Integer objToInteger(Object obj) {
        if (obj == null) {
            return null;
        } else {
            return Integer.valueOf(objToStr(obj));
        }
    }

    /**
     * Object对象向Long变更。
     *
     * @param obj Object对象
     * @return 向文字
     */
    public static long objToLong(Object obj) {
        if (obj == null) {
            return 0;
        } else if ("".equals(obj)) {
            return 0;
        } else {
            return Long.parseLong(obj.toString());
        }
    }

    /**
     * Object对象向Double变更。
     *
     * @param obj Object对象
     * @return 向文字
     */
    public static double objToDouble(Object obj) {
        if (obj == null) {
            return 0;
        } else {
            return Double.parseDouble(obj.toString());
        }
    }

    /**
     * 指定长度的random生成
     *
     * @param length 长度
     * @return random结果
     */
    public static String randomStr(int length, boolean isOnlyNumber) {
        // 英数字组合
        int numChar = 59;
        // 数字
        if (isOnlyNumber) {
            numChar = 10;
        }

        StringBuilder t = new StringBuilder();
        String[] v = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f", "g", "h", "i",
                "j", "k", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "A", "B", "C", "D",
                "E", "F", "G", "H", "J", "K", "L", "M", "N", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

        for (int i = 0; i < length; i++) {
            Random r = ThreadLocalRandom.current();
            int j = r.nextInt(numChar);
            t.append(v[j]);
        }

        return t.toString();
    }

    /**
     * 根据请求获取客户端IP地址
     *
     * @param request
     * @return
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ipAddress = request.getHeader("x-forwarded-for");
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        //对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if (ipAddress != null) {
            if (ipAddress.equals("127.0.0.1") || ipAddress.equals("0:0:0:0:0:0:0:1")) {
                //根据网卡取本机配置的IP
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost();
                    ipAddress = inet.getHostAddress();
                } catch (UnknownHostException e) {
                    log.error("获取本地主机出错", e.getMessage());
                }
            }
            if (ipAddress.length() > 15 && ipAddress.indexOf(",") > 0) {//"***.***.***.***".length() = 15
                ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
            }
        }
        return ipAddress;
    }


    /**
     * List 子元素类型转化
     *
     * @param list
     * @param clazz
     * @param <A>
     * @param <B>
     * @return
     */
    public static <A, B> List<B> castList(List<A> list, Class<B> clazz) {
        return list.parallelStream().map(v -> (B) v).collect(Collectors.toList());
    }


}
