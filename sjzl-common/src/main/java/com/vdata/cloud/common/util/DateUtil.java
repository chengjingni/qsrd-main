/**
 * Project Name:dsp
 * File Name:DateUtil.java
 * Package cn.vdc.bots.house.common.util
 * Date:Apr 8, 2013 1:29:22 PM
 */

package com.vdata.cloud.common.util;

import lombok.extern.log4j.Log4j2;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * ClassName:DateUtil <br/>
 * Function: 提供字符和java日期类型的转换. <br/>
 * <p>
 * Date: Apr 8, 2013 1:29:22 PM <br/>
 *
 * @author 于明涛
 * @see
 * @since JDK 1.7
 */
@Log4j2
public class DateUtil {

    public static final String YEAR_SPLIT_FORMAT = "yyyy";
    public static final String MONTH_SPLIT_FORMAT = "yyyy-MM";
    public static final String DAY_SPLIT_FORMAT = "yyyy-MM-dd";

    public static final String MONTH_FORMAT = "yyyyMM";
    public static final String DAY_FORMAT = "yyyyMMdd";
    public static final String DATE_FORMAT = "yyyyMMddHHmmss";

    public static Date convertDate2Date(Date date, String formatStr) {
        if (date == null || formatStr == null) {
            return null;
        }
        Date newDate = null;
        String dateStr = null;
        try {
            dateStr = convertDate2String(date, formatStr);
            newDate = convertString2Date(dateStr, formatStr);
            log.debug(date.toString());
        } catch (Exception e) {
            log.debug(e.getMessage());
        }
        return newDate;
    }

    public static Date convertString2Date(String dateStr, String formatStr) {
        if (StringUtils.isEmpty(dateStr) || StringUtils.isEmpty(formatStr)) {
            return null;
        }

        DateFormat format = new SimpleDateFormat(formatStr);
        Date date = null;
        try {
            date = format.parse(dateStr);
            log.debug(date.toString());
        } catch (Exception e) {
            log.debug(e);
        }

        return date;
    }

    /**
     * 查询当前时间，返回时间类型 <功能详细描述>
     *
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static Date currentDate() {
        long dateLong = System.currentTimeMillis();
        Date date = new Date(dateLong);
        return date;
    }

    /**
     * 查询当前时间之后的某个时间，返回时间类型 <功能详细描述>
     *
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static Date currentDate(int day, int hour, int minute) {
        long addTime = day * 24 * 60 * 60 * 1000;
        addTime += hour * 60 * 60 * 1000;
        addTime += minute * 60 * 1000;

        long dateLong = System.currentTimeMillis() + addTime;
        Date date = new Date(dateLong);
        return date;
    }

    /**
     * 查询当前时间，返回时间类型 <功能详细描述>
     *
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static Timestamp currentTimeStamp() {
        long dateLong = System.currentTimeMillis();
        Timestamp date = new Timestamp(dateLong);
        return date;
    }

    /**
     * 根据转换格式获取当前时间字符串类型 <功能详细描述>
     *
     * @param formatStr
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static String currentDate2String(String formatStr) {
        if (StringUtils.isEmpty(formatStr)) {
            formatStr = "yyyy-MM-dd HH:mm:ss";
        }
        Date date = currentDate();
        return convertDate2String(date, formatStr);
    }

    public static String convertDate2String(Date date) {

        return convertDate2String(date, "yyyy-MM-dd HH:mm:ss");
    }

    public static String convertString2String(String date, String formatStr) {
        Date ddate = convertString2Date(date, formatStr);
        return convertDate2String(ddate, formatStr);
    }

    public static String convertDate2String(Date date, String formatStr) {
        if (date == null || date.equals("")) {
            return null;
        }

        DateFormat format = new SimpleDateFormat(formatStr);
        String dateStr = "";
        try {
            dateStr = format.format(date);
            log.debug(date.toString());
        } catch (Exception e) {
            log.debug(e);
        }

        return dateStr;
    }

    public static String convertTimeStamp2String(Timestamp date) {

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr = "";
        try {
            dateStr = format.format(date);
            log.debug(date.toString());
        } catch (Exception e) {
            log.debug(e.getMessage());
        }

        return dateStr;
    }

    /**
     * 获取时差的绝对值
     *
     * @param date
     * @return
     */
    public static long dateDiffer(Date date) {
        long nowTime = System.currentTimeMillis();
        long compareTime = date.getTime();
        return Math.abs(nowTime - compareTime);
    }

    /**
     * 比较当前时间是否在某个时间段内
     *
     * @param start
     * @param end
     * @return
     */
    public static boolean dateDiffer(Date start, Date end) {
        long now = System.currentTimeMillis();
        long startLong = start.getTime();
        long endLong = end.getTime();
        if (startLong <= now && now <= endLong) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获得该日期指定小时之后的时间
     *
     * @param
     * @param
     * @return 返回日期
     */
    public static Date afterMinute(Date date, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, minute);
        return calendar.getTime();
    }

    /**
     * 获得该日期指定小时之后的时间
     *
     * @param
     * @param
     * @return 返回日期
     */
    public static Date afterHour(Date date, int hour) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, hour);
        return calendar.getTime();
    }

    /**
     * 获得该日期指定天数之后的日期
     *
     * @param
     * @param
     * @return 返回日期
     */
    public static Date afterDay(Date date, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, days);
        return calendar.getTime();
    }

    /**
     * 获得该日期指定数月之后的日期
     *
     * @param
     * @param
     * @return 返回日期
     */
    public static Date afterMonth(Date date, int months) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, months);
        return calendar.getTime();
    }


    /**
     * 获得该日期指定数年之前的日期
     *
     * @param
     * @param
     * @return 返回日期
     */
    public static Date afterYear(Date date, int years) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.YEAR, years);
        return calendar.getTime();
    }

    /**
     * 获得该日期指定天数之前的日期
     *
     * @param
     * @param
     * @return 返回日期
     */
    public static Date beforeDay(Date date, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, days * -1);
        return calendar.getTime();
    }

    /**
     * 获得该日期指定数月之前的日期
     *
     * @param
     * @param
     * @return 返回日期
     */
    public static Date beforeMonth(Date date, int months) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, months * -1);
        return calendar.getTime();
    }

    /**
     * 获得该日期指定数年之前的日期
     *
     * @param
     * @param
     * @return 返回日期
     */
    public static Date beforeYear(Date date, int years) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.YEAR, years * -1);
        return calendar.getTime();
    }

    /**
     * 获得该日期指定分钟之前的时间
     *
     * @param
     * @param minute
     * @return 返回日期
     */
    public static Date beforeMinute(Date date, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, minute * -1);
        return calendar.getTime();
    }

    /**
     * 根据时间类型获取
     * 获取设置时间下月的第一天time
     *
     * @param time
     * @return
     * @deprecated (when, why, etc...)
     */
    @Deprecated
    public static String getFirstDayOfMonthByDateAndMonthNumber(Date time, int monthNumber) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        calendar.add(Calendar.MONTH, monthNumber);
        calendar.set(Calendar.DAY_OF_MONTH, 1);//设置为1号,当前日期既为本月第一天
        String startDate = DateUtil.convertDate2String(calendar.getTime(), "yyyyMMdd");
        return startDate + "000000";
    }

    /**
     * 获取设置时间和月周期的最后一天
     *
     * @param time
     * @return
     * @deprecated (when, why, etc...)
     */
    @Deprecated
    public static String getEndDayOfMonthByDateAndMonthNumber(Date time, int monthNumber) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        calendar.add(Calendar.MONTH, monthNumber);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        String startDate = DateUtil.convertDate2String(calendar.getTime(), "yyyyMMdd");
        return startDate + "235959";
    }

    /**
     * 根据日期字符串获取年
     * <功能详细描述>
     *
     * @param date
     * @param format
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static Integer formatYear(String date, String format) {
        Date dateTmp = convertString2Date(date, format);
        return formatYear(dateTmp);
    }

    /**
     * 根据日期类型获取年
     * <功能详细描述>
     *
     * @param date
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static Integer formatYear(Date date) {
        return format(date, Calendar.YEAR);
    }

    /**
     * 根据日期字符串获取当年月
     * 参数时间  2015-05-11 00:00:00 将转换为 201505
     *
     * @param date
     * @param format
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static Integer formatMonthToDate(String date, String format) {
        Date dateTmp = convertString2Date(date, format);
        return formatMonthToDate(dateTmp);
    }

    /**
     * 根据日期类型获取年月
     * 参数时间  2015-05-11 00:00:00 将转换为 201505
     * <功能详细描述>
     *
     * @param date
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static Integer formatMonthToDate(Date date) {
        String month = convertDate2String(date, MONTH_FORMAT);
        return new Integer(month);
    }

    /**
     * 根据日期类型获取当月
     * 参数时间  2015-05-11 00:00:00 将转换为 5
     * <功能详细描述>
     *
     * @param date
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static Integer formatMonth(Date date) {
        return format(date, Calendar.MONTH) + 1;
    }

    /**
     * 根据日期字符串获取月
     * 参数时间  2015-05-11 00:00:00 将转换为 5
     *
     * @param date
     * @param format
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static Integer formatMonth(String date, String format) {
        Date dateTmp = convertString2Date(date, format);
        return formatMonth(dateTmp);
    }

    /**
     * 根据日期类型获取年月日
     * 参数时间  2015-05-11 00:00:00 将转换为 20150511
     * <功能详细描述>
     *
     * @param date
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static Integer formatDayToDate(Date date) {
        String day = convertDate2String(date, DAY_FORMAT);
        return new Integer(day);
    }

    /**
     * 根据日期字符串获取年月日
     * 参数时间  2015-05-11 00:00:00 将转换为 20150511
     *
     * @param date
     * @param format
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static Integer formatDayToDate(String date, String format) {
        Date dateTmp = convertString2Date(date, format);
        return formatDayToDate(dateTmp);
    }

    /**
     * 根据日期类型获取日
     * 参数时间  2015-05-11 00:00:00 将转换为 11
     *
     * @param date
     * @param
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static Integer formatDay(Date date) {
        return format(date, Calendar.DAY_OF_MONTH);
    }

    /**
     * 根据日期字符串获取日
     * 参数时间  2015-05-11 00:00:00 将转换为 11
     *
     * @param date
     * @param format
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static Integer formatDay(String date, String format) {
        Date dateTmp = convertString2Date(date, format);
        return formatDay(dateTmp);
    }

    /**
     * 根据时间获取
     * <功能详细描述>
     *
     * @param date
     * @param field
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static Integer format(Date date, Integer field) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        Integer year = cal.get(field);
        return year;
    }

    /**
     * 根据日期字符串（yyyymmdd）获取当天开始时间
     * 20150511  转换为 2015-05-11 00:00:00
     *
     * @param day
     * @param format
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static Date formatMinTimeInDate(String day, String format) {
        if ("".equals(format)) {
            return null;
        }
        Date date = convertString2Date(day, format);
        if (date == null) {
            return date;
        }
        return formatMinTimeInDate(date);
    }

    /**
     * 根据日期类型（yyyymmdd）获取当天开始时间
     * 2015-05-11 21:12:12  转换为 2015-05-11 00:00:00
     *
     * @param
     * @param
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static Date formatMinTimeInDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return formatMinTimeInDate(calendar);
    }

    /**
     * 根据日历控件获取当天开始时间
     * 2015-05-11 21:12:12  转换为 2015-05-11 00:00:00
     *
     * @param calendar
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static Date formatMinTimeInDate(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMinimum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, calendar.getActualMinimum(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, calendar.getActualMinimum(Calendar.SECOND));
        return calendar.getTime();
    }

    /**
     * 根据日期字符串（yyyymmdd）获取当天截止时间
     * 20150511  转换为 2015-05-11 23:59:59
     *
     * @param day
     * @param format
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static Date formatMaxTimeInDate(String day, String format) {
        if ("".equals(format)) {
            return null;
        }
        Date date = convertString2Date(day, format);
        if (date == null) {
            return null;
        }
        return formatMaxTimeInDate(date);
    }

    /**
     * 根据日期类型（yyyymmdd）获取当天截止时间
     * 2015-05-11 12:51:31  转换为 2015-05-11 23:59:59
     *
     * @param
     * @param
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static Date formatMaxTimeInDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return formatMaxTimeInDate(calendar);
    }

    /**
     * 根据日历控件获取当天截止时间
     * 2015-05-11 12:51:31  转换为 2015-05-11 23:59:59
     *
     * @param calendar
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static Date formatMaxTimeInDate(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMaximum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, calendar.getActualMaximum(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, calendar.getActualMaximum(Calendar.SECOND));

        return calendar.getTime();
    }

    /**
     * 根据月份字符串（yyyymm）获取当月开始时间
     * 201505  转换为 2015-05-01 00:00:00
     *
     * @param
     * @param format
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static Date formatMinTimeInMonth(String month, String format) {
        if ("".equals(format)) {
            return null;
        }
        Date date = convertString2Date(month, format);
        if (date == null) {
            return null;
        }
        return formatMinTimeInMonth(date);
    }

    /**
     * 根据日期类型（yyyymm）获取当月开始时间
     * 2015-05-11 23:00:00  转换为 2015-05-01 00:00:00
     *
     * @param
     * @param
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static Date formatMinTimeInMonth(Date month) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(month);
        return formatMinTimeInMonth(calendar);
    }

    /**
     * 根据时间控件，获取当月开始时间
     * 2015-05-11 23:00:00  转换为 2015-05-01 00:00:00
     *
     * @param calendar
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static Date formatMinTimeInMonth(Calendar calendar) {
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        return formatMinTimeInDate(calendar);
    }

    /**
     * 根据月份字符串（yyyymm）获取当月截止时间
     * 201505  转换为 2015-05-31 23:59:59
     *
     * @param
     * @param format
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static Date formatMaxTimeInMonth(String month, String format) {
        if ("".equals(format)) {
            return null;
        }
        Date date = convertString2Date(month, format);
        if (date == null) {
            return null;
        }
        return formatMaxTimeInMonth(date);
    }

    /**
     * 根据日期类型，获取当月截止时间
     * 2015-05-11 21:23:22  转换为 2015-05-31 23:59:59
     *
     * @param month
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static Date formatMaxTimeInMonth(Date month) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(month);
        return formatMaxTimeInMonth(calendar);
    }

    /**
     * 根据日期控件，获取当月截止时间
     * 2015-05-11 21:23:22  转换为 2015-05-31 23:59:59
     *
     * @param calendar
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static Date formatMaxTimeInMonth(Calendar calendar) {
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        return formatMaxTimeInDate(calendar);
    }

    /**
     * 根据当前时间，获取前20分钟时间段时间
     * 2015-10-14 17:13:22  转换为 201510141700
     *
     * @param
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static String getDayTime() {
        String day = convertDate2String(new Date(), DATE_FORMAT);
        String minute = day.substring(10, 12);
        int y = Integer.parseInt(minute) / 10;
        int m = y * 10;
        String newDay = "";
        if (0 == m) {
            newDay = DateUtil.convertDate2String(new Date(), "yyyyMMddHH") + "00";
        } else {
            newDay = day.substring(0, 10) + m;
        }
        return newDay;
    }

    /**
     * 根据当前时间,获取今天后的时间
     * 2016-07-26 17:13:22  转换为 20160726
     *
     * @param days
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static String getMonthDayTime(int days) {
        Date date = new Date();//取时间
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, days);//把日期往前减少一天，若想把日期向后推一天则将负数改为正数
        date = calendar.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat(DAY_FORMAT);
        String dateString = formatter.format(date);
        return dateString;
    }

    /**
     * 计算2个时间相隔天数
     *
     * @param startTime,endTime
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static int getBetweenDays(String startTime, String endTime) {
        Date startDate = convertString2Date(startTime, DAY_FORMAT);
        Date endDate = convertString2Date(endTime, DAY_FORMAT);
        assert endDate != null;
        assert startDate != null;
        long betweenDays = (endDate.getTime() - startDate.getTime()) / (1000 * 3600 * 24);
        return Integer.parseInt(String.valueOf(betweenDays)) + 1;
    }

    /**
     * 获取某月最后一天
     */
    public static String getLastDayOfMonth(Date date) {
        Calendar aCalendar = Calendar.getInstance(Locale.CHINA);
        aCalendar.setTime(date);
        int day = aCalendar.getActualMaximum(Calendar.DATE);
        String result = "";
        if (day < 10) {
            result = "0" + day;
        } else {
            result = day + "";
        }
        return result;
    }


    /**
     * 获取系统时间。
     *
     * @return 系统时间
     */
    public static Date getSystemDate() {
        return new Date();
    }

    /**
     * 获得两个月份之间的月份的集合
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static List<Date> getBetweenMonthsList(String startDate, String endDate) {
        Date startMonth = convertString2Date(startDate, MONTH_FORMAT);
        Date endMonth = convertString2Date(endDate, MONTH_FORMAT);
        List<Date> reList = new ArrayList<Date>();

        Calendar calendarStart = Calendar.getInstance();
        calendarStart.setTime(startMonth);
        Calendar calendarEnd = Calendar.getInstance();
        calendarEnd.setTime(endMonth);

        reList.add(calendarStart.getTime());
        while (calendarStart.compareTo(calendarEnd) == -1) {
            calendarStart.add(Calendar.MONTH, 1);
            reList.add(calendarStart.getTime());
        }
        return reList;
    }

    /**
     * 获得某个月份之后某个月数的月份的集合
     *
     * @param startDate
     * @param months
     * @return
     */
    public static List<Date> getBeforeMonthsList(String startDate, int months) {
        Date startMonth = convertString2Date(startDate, MONTH_FORMAT);
        List<Date> reList = new ArrayList<Date>();

        Calendar calendarStart = Calendar.getInstance();
        calendarStart.setTime(startMonth);

        for (int i = 0; i < months; i++) {
            calendarStart.add(Calendar.MONTH, -1);
            reList.add(calendarStart.getTime());
        }
        return reList;
    }

    /**
     * 获得两个日期之间的日期的集合
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static List<Date> getBetweenDaysDateList(String startDate, String endDate) {
        Date startMonth = convertString2Date(startDate, DAY_FORMAT);
        Date endMonth = convertString2Date(endDate, DAY_FORMAT);
        List<Date> reList = new ArrayList<Date>();

        Calendar calendarStart = Calendar.getInstance();
        calendarStart.setTime(startMonth);
        Calendar calendarEnd = Calendar.getInstance();
        calendarEnd.setTime(endMonth);

        reList.add(calendarStart.getTime());
        while (calendarStart.compareTo(calendarEnd) == -1) {
            calendarStart.add(Calendar.DAY_OF_MONTH, 1);
            reList.add(calendarStart.getTime());
        }
        return reList;
    }

    /**
     * 将日期集合转为字符串集合
     *
     * @param dateList
     * @return
     */
    public static List<String> transForDateListToDateString(List<Date> dateList) {
        List<String> retList = new ArrayList<String>();
        for (Date date : dateList) {
            retList.add(convertDate2String(date, "yyyyMM"));
        }
        return retList;
    }

    /**
     * String转localDate
     */
    public static LocalDate localDate(String str) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate time = LocalDate.parse(str, formatter);
        return time;
    }

    /**
     * 年增加
     *
     * @param date 对象日期
     * @param year 年
     * @return 增加后的日期
     */
    public static Date addYear(Date date, int year) {
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.YEAR, year);
        return calendar.getTime();
    }

    /**
     * 月份增加
     *
     * @param date  对象日期
     * @param month 月
     * @return 增加后的日期
     */
    public static Date addMonth(Date date, int month) {
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, month);
        return calendar.getTime();
    }

    /**
     * 小时增加
     *
     * @param date 对象日期
     * @param hour 日
     * @return 增加后的日期
     */
    public static Date addHour(Date date, int hour) {
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, hour);
        return calendar.getTime();
    }

    /**
     * 分钟增加
     *
     * @param date   对象日期
     * @param minute 分钟
     * @return 增加后的日期
     */
    public static Date addMinute(Date date, int minute) {
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, minute);
        return calendar.getTime();
    }

    /**
     * 秒增加
     *
     * @param date   对象日期
     * @param second 秒
     * @return 增加后的日期
     */
    public static Date addSecond(Date date, int second) {
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.SECOND, second);
        return calendar.getTime();
    }

    /**
     * 获取某月天数
     *
     * @param month 月数202002
     * @return 当月的天数
     */
    public static int getMonthDays(String month) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
        Date date = sdf.parse(month);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    /**
     * 天增加
     *
     * @param date 对象日期
     * @param hour 日
     * @return 增加后的日期
     */
    public static Date addDay(Date date, int day) {
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, day);
        return calendar.getTime();
    }

    /*public static void main(String[] args) throws ParseException {
        System.out.println(getMonthDays("202007"));
    }*/
}
