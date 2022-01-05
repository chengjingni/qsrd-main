package com.vdata.cloud.datacenter.service;

import com.vdata.cloud.datacenter.vo.HisPointDataVO;

import java.text.ParseException;
import java.util.Date;
import java.util.Map;

/**
 * @ProjectName: sjzl-master
 * @Package: com.vdata.cloud.datacenter.service
 * @ClassName: IPointRunService
 * @Author: HK
 * @Description:
 * @Date: 2021/7/29 16:00
 * @Version: 1.0
 */
public interface IPointRunService {
    Map<String, Object> getPointRuns(String pulverizerCode, String startTimeStr, String endTimeStr, Long start, Integer count, int orderByDesc) throws ParseException;

    Map<String, Object> getHourPointRuns(String pulverizerCode, String startTimeStr, String endTimeStr, Long start, Integer count) throws ParseException;


    /**
     * 按照指定时间填充数据
     */
    void getHisPointRunByDate(Date startDate, Date endDate, String pulverizerCode, Integer no) throws ParseException;

    void datafilling() throws ParseException;

    void pullHisPointData(HisPointDataVO hisPointDataVO);

    void hourStat(long oldtime, long currentTime) throws ParseException;

}
