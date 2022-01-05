package com.vdata.cloud.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.vdata.cloud.admin.biz.CalendarBiz;
import com.vdata.cloud.admin.entity.Calendar;
import com.vdata.cloud.common.constant.Constants;
import com.vdata.cloud.common.util.DateUtil;
import com.vdata.cloud.common.vo.DataResult;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * 工作历
 *
 * @author fjzha
 * @version 1.0
 * @date 2021/1/16 8:15
 */

@Log4j2
@Controller
@RequestMapping(value = "/calendar")
public class CalendarController {

    @Autowired
    private CalendarBiz calendarBiz;

    @GetMapping("")
    public String index(ModelMap model) {
        model.addAttribute("type", "1");
        return "calendar/index";
    }

    @ResponseBody
    @GetMapping("/detail/{year}")
    public Object init(@PathVariable(value = "year") String year) {
        return toYear(year);
    }

    @ResponseBody
    @PostMapping("/update")
    public DataResult update(@RequestBody Calendar calendar) {
        DataResult result = new DataResult();
        try {
            calendar.setDay(calendar.getDay().replace("休", ""));
            LambdaQueryWrapper<Calendar> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Calendar::getYear, calendar.getYear())
                    .eq(Calendar::getMonth, calendar.getMonth())
                    .eq(Calendar::getDay, calendar.getDay());
            Calendar calendar_ = (Calendar) calendarBiz.getOne(wrapper);
            if (calendar_ == null) {
                calendar.setType("1");
                calendarBiz.saveOrUpdate(calendar);
            } else {
                calendar.setId(calendar_.getId());
                calendar.setType(calendar_.getType().equals("0") ? "1" : "0");
                if ("0".equals(calendar.getType()))
                    calendarBiz.removeById(calendar);
                else
                    calendarBiz.updateById(calendar);
            }
            result.setCode(Constants.RETURN_NORMAL);
            result.setMessage("修改成功。");
        } catch (Exception e) {
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage("修改失败。");
        }
        return result;
    }

    public Map<String, Map<String, String>> toYear(String year) {
        LambdaQueryWrapper<Calendar> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Calendar::getYear, year);
        List<Calendar> calendarList = calendarBiz.list(wrapper);
        String hour = " 00:00:01";
        boolean isSd = false;
        int count = 0;
        String startDay = "";
        String endDate = "";
        Date JNDate = DateUtil.convertString2Date(year + "-12-26" + hour, Constants.DATE_YYYY_MM_DD_TIME_LOCALE);
        Date JNDate_ = JNDate;
        // 如果查询年本身就是星期六，就从上一年开始回滚
        String JN = DateUtil.convertDate2String(JNDate_, Constants.DATE_YYYY_MM_DD);
        String week = dateToWeek(JN);
        if (week.equals("星期六")) {
            JNDate_ = DateUtil.addYear(JNDate_, -1);
            isSd = true;
        }
        // 时间回滚，寻找星期六
        while (true) {
            JN = DateUtil.convertDate2String(JNDate_, Constants.DATE_YYYY_MM_DD);
            week = dateToWeek(JN);
            if (week.equals("星期六")) {
                startDay = DateUtil.convertDate2String(DateUtil.addDay(JNDate_, 8), Constants.DATE_YYYY_MM_DD);
                break;
            }
            JNDate_ = DateUtil.addYear(JNDate_, -1);
            count++;
        }
        log.info(startDay);
        // 查询年为周六
        if (isSd) {
            count++;
        }
        // 往后推日期
        // 此年 12月26推下一年开始日
        while (count > 1) {
            Date SD = DateUtil.convertString2Date(startDay + hour, Constants.DATE_YYYY_MM_DD_TIME_LOCALE);
            startDay = DateUtil.convertDate2String(DateUtil.addDay(SD, 364), Constants.DATE_YYYY_MM_DD);
            count--;
        }
        // 今年26号判断
        week = dateToWeek(DateUtil.convertDate2String(JNDate, Constants.DATE_YYYY_MM_DD));
        if (week.equals("星期六")) {
            endDate = DateUtil.convertDate2String(DateUtil.addDay(JNDate, 7), Constants.DATE_YYYY_MM_DD);
        } else {
            Date SD = DateUtil.convertString2Date(startDay + hour, Constants.DATE_YYYY_MM_DD_TIME_LOCALE);
            endDate = DateUtil.convertDate2String(DateUtil.addDay(SD, 363), Constants.DATE_YYYY_MM_DD);
        }

        String startTime = startDay.replace("-", "") + " 000001";
        String endTime = endDate.replace("-", "") + " 000001";

        count = 1;
        int p = 1;
        Map<String, String> pxMap = new LinkedHashMap<>();
        while (DateUtil.getBetweenDays(startTime, endTime) != 0) {
            if (count == 29) {
                count = 1;
                p++;
            }
            if (p > 13)
                p--;
            String key = "P" + p;
            if (!pxMap.containsKey(key)) {
                pxMap.put(key, "");
            }
            Date s = DateUtil.convertString2Date(startTime, Constants.DATE_TIME_YYYYMMDDHHMMSS);
            String rq = DateUtil.convertDate2String(s, Constants.DATE_YYYY_MM_DD);

            String stringBuilder = pxMap.get(key);
            // 判断是否是1号 日期（1号为月缩写）,星期天标识(0否，1是),排班标识（0否，1是）,法定节假日:
            // 判断是否是1号 日期（1号为月缩写）
            int day = getDay(s);
            String day_ = day + "";
            if (day == 1) {
                DateFormat df = new SimpleDateFormat("MMM", Locale.ENGLISH);
                stringBuilder += (day_ = df.format(s)) + ",";
            } else {
                stringBuilder += day + ",";
            }
            // 星期天标识(0否，1是)
            week = dateToWeek(rq);
            if (week.equals("星期六") || week.equals("星期日")) {
                stringBuilder += 1 + ",";
            } else {
                stringBuilder += 0 + ",";
            }
            // 判断
            String type = "0";
            for (Calendar calendar : calendarList) {
                if (calendar.getYear().equals(year) && calendar.getMonth().equals(key) && calendar.getDay().equals(day_ + "")) {
                    type = calendar.getType();
                    calendarList.remove(calendar);
                    break;
                }
            }
            // 排班标识（0否，1是）
            stringBuilder += type + ",";
            // 法定节假日
            stringBuilder += "0" + ":";

            pxMap.put(key, stringBuilder);
            startTime = DateUtil.convertDate2String(DateUtil.addDay(s, 1), Constants.DATE_YYYYMMDD) + " 000001";
            count++;
        }

        Map<String, Map<String, String>> map = new HashMap<>();
        map.put(year, pxMap);
        log.info(String.format("%S年 开始日期：%S 结束日期：%S", year, startDay, endDate));
        //log.info(JSON.toJSONString(map));
        return map;
    }


    /**
     * 功能描述：返回日期
     *
     * @param date Date 日期
     * @return 返回日份
     */

    public static int getDay(Date date) {

        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(java.util.Calendar.DAY_OF_MONTH);
    }


    /**
     * 根据日期获取 星期 （2019-05-06 ——> 星期一）
     *
     * @param datetime
     * @return
     */

    public static String dateToWeek(String datetime) {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        java.util.Calendar cal = java.util.Calendar.getInstance();
        Date date;
        try {
            date = f.parse(datetime);
            cal.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //一周的第几天
        int w = cal.get(java.util.Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }
}

