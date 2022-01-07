package com.vdata.cloud.datacenter.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.vdata.cloud.common.exception.BusinessException;
import com.vdata.cloud.common.vo.UserVO;
import com.vdata.cloud.datacenter.entity.AlarmInformation;
import com.vdata.cloud.datacenter.entity.OperationLog;
import com.vdata.cloud.datacenter.vo.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @ProjectName: sjzl-master
 * @Package: com.vdata.cloud.datacenter.service
 * @ClassName: AlarmService
 * @Author: HK
 * @Description:
 * @Date: 2021/7/21 15:04
 * @Version: 1.0
 */
public interface IAlarmService {
    IPage<AlarmInformationVO> page(AlarmPageVO alarmPageVO) throws BusinessException;

    /**
     * 判断是否存在当前报警
     *
     * @param pulverizerPointId 点位id
     * @param abnormalCode      报警类型
     * @return
     */
    AlarmInformation exists(Integer pulverizerPointId, String abnormalCode);


    void manage(AlarmInformation alarmInformation, UserVO user);

    IPage<AbnormalDetailVO> countDetailList(AbnormalDetailPageVO abnormalDetailPageVO);

    IPage<OperationLog> operationLogList(AbnormalDetailPageVO abnormalDetailPageVO);

    Map<String, List<Integer>> trendOfStatistical(AlarmAnalyseQueryVO alarmAnalyseQueryVO);

    Map<String, Map<String, Integer>> faultStatistics(AlarmAnalyseQueryVO alarmAnalyseQueryVO);

    List<RealTimeAlarmVO> realTimeAlarm();


    //推送消息到websocket
    void pushAlarmInfo(AlarmInformation alarmInformation, Date date);

    List<Map<String, Object>> pulverizerRealTimeAlarm(String pulverizerCode);

    List<String> years();


    Map<String, Long> count();


    void insertOperationLog(Date alarmTime, AlarmInformation alarmInformation);

    void alarmCreate(Date alarmTime, Integer pulverizerPointId, String abnormalCode, String alarmDescription, String detectionValue, String positionCode);

    Map<String, Map<String, Integer>> hisTrueFaultByPulverizer(AlarmAnalyseQueryVO alarmAnalyseQueryVO);

    void export(AlarmPageVO alarmPageVO, HttpServletResponse response) throws IOException;

    void triggeringAlarm(TriggeringAlarmVO triggeringAlarmVO);
}
