package com.vdata.cloud.common.constant;

import java.nio.charset.Charset;

/**
 * Created by ace on 2017/8/29.
 */
public class Constants {
    public final static String RESOURCE_TYPE_MENU = "menu";
    /*时间FORMART yyyy-mm-dd hh:mm:ss*/
    public static final String DATE_YYYY_MM_DD_TIME_LOCALE = "yyyy-MM-dd HH:mm:ss";
    // 时间FORMART yyyyMMddHHmmss
    public static final String DATE_TIME_YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
    public final static String RESOURCE_TYPE_BTN = "button";
    public static final String DATE_YYYY_MM_DD = "yyyy-MM-dd";
    public static final String DATE_YYYYMMDD = "yyyyMMdd";
    // 用户token异常
    public static final Integer EX_USER_INVALID_CODE = 40101;
    public static final Integer EX_USER_PASS_INVALID_CODE = 40001;
    // 客户端token异常
    public static final Integer EX_CLIENT_INVALID_CODE = 40301;
    public static final Integer EX_CLIENT_FORBIDDEN_CODE = 40331;
    public static final Integer EX_OTHER_CODE = 500;
    public static final String CONTEXT_KEY_USER_ID = "currentUserId";
    public static final String CONTEXT_KEY_USERNAME = "currentUserName";
    public static final String CONTEXT_KEY_USER_NAME = "currentUser";
    public static final String CONTEXT_KEY_USER_TOKEN = "currentUserToken";
    public static final String COOKIE_TOKEN_KEY = "Admin-Token";
    public static final String JWT_KEY_USER_ID = "userId";
    public static final String JWT_KEY_NAME = "name";

    /**
     * 返回码名称
     */
    public static final String RETURN_CODE = "code";
    /**
     * 返回信息
     */
    public static final String RETURN_MSG = "msg";
    /**
     * 返回数据
     */
    public static final String RETURN_DATA = "data";
    /**
     * 返回正常(00：正常)
     */
    public static final String RETURN_NORMAL = "00";
    /**
     * 返回异常(01：异常)
     */
    public static final String RETURN_UNNORMAL = "01";
    /**
     * 返回异常(99：token异常)
     */
    public static final String RETURN_UNNORMAL_TOKEN = "99";
    /**
     * 返回信息
     */
    public static final String RETURN_NORMAL_MSG = "操作成功。";
    /**
     * 返回信息
     */
    public static final String RETURN_UNNORMAL_MSG = "处理失败。";
    /**
     * 返回标记
     */
    public static final String RETURN_TOKEN = "token";
    /**
     * 编码类型
     */
    public static final String CHARSET_UTF_8 = "utf-8";

    public static final Charset CHARSET_UTF8 = Charset.forName("utf8");
    /**
     * 空字符
     */
    public static final String EMPY = "";
    /**
     * 反斜线
     */
    public static final String LINE_LEFT = "/";

    public static final String YES = "1";//是
    public static final String NO = "0";//否

    public static final String APP_ID = "SJZL";

    public static final int RECORD_SIZE = 1000;

    public static final String TEMP = "_temp";

    public static final String ADMIN = "admin";


    public static final String REVIEW = "4";

}
