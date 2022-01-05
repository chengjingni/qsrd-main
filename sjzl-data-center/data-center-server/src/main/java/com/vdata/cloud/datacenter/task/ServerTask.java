package com.vdata.cloud.datacenter.task;

import cn.hutool.core.date.DateUtil;
import com.vdata.cloud.common.constant.Constants;
import com.vdata.cloud.common.vo.DataResult;
import com.vdata.cloud.datacenter.constants.CommonConstans;
import com.vdata.cloud.datacenter.service.IAlarmService;
import com.vdata.cloud.datacenter.vo.RealTimeAlarmVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ProjectName: sjzl-master
 * @Package: com.vdata.cloud.datacenter.task
 * @ClassName: ServerTask
 * @Author: HK
 * @Description:
 * @Date: 2021/7/12 15:16
 * @Version: 1.0
 */
@Slf4j
@Component
public class ServerTask {

    @Autowired
    private SimpMessagingTemplate wsTemplate;


    @Autowired
    private IAlarmService alarmService;

    //    @Scheduled(cron = "0/2 * * * * ?")
    public void websocket1() throws Exception {
        int i = 0;
        Map<String, Integer> map = new HashMap<>();

        log.info("【推送消息】开始执行：{}", DateUtil.formatDateTime(new Date()));
        map.put("key", i += 1);
        wsTemplate.convertAndSend("/topic/server1", map);
        log.info("【推送消息】执行结束：{}", DateUtil.formatDateTime(new Date()));
    }

    @Scheduled(cron = "0/5 * * * * ?")
    public void pullRealTimeAlarm() throws Exception {
        DataResult result = new DataResult();
        List<RealTimeAlarmVO> realTimeAlarmVOS = alarmService.realTimeAlarm();


        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("data", realTimeAlarmVOS);
        resultMap.put("type", CommonConstans.SocketType.REALTIMEALARM.getValue());
        result.setData(resultMap);
        result.setCode(Constants.RETURN_NORMAL);
        result.setMessage("获取磨煤机实时情况成功");


        log.info("【推送消息】开始执行：{}", DateUtil.formatDateTime(new Date()));
//        wsTemplate.convertAndSend("/topic/" + CommonConstans.REALTIMEALARM_WEBSOCKET, result);
        wsTemplate.convertAndSend("/topic/server", result);
        log.info("【推送消息】执行结束：{}", DateUtil.formatDateTime(new Date()));
    }


}
