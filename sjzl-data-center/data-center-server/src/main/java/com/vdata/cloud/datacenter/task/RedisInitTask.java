package com.vdata.cloud.datacenter.task;

import com.vdata.cloud.datacenter.service.IPulverizerPointService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @ProjectName: sjzl-master
 * @Package: com.vdata.cloud.datacenter.task
 * @ClassName: RedisInitTask
 * @Author: HK
 * @Description:
 * @Date: 2021/8/2 14:28
 * @Version: 1.0
 */
@Component
@Log4j2
public class RedisInitTask {
    @Autowired
    private IPulverizerPointService pulverizerPointService;

    @Scheduled(cron = "0/10 * * * * ? ")
    public void initpulverizerPointInfo() {
        log.info("更新reids缓存中的数据----start");
        pulverizerPointService.listSaveRedis();
        log.info("更新reids缓存中的数据----end");
    }

}
