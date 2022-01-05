package com.vdata.cloud.datacenter.component;

import com.vdata.cloud.datacenter.mapper.DcsServerInfoMapper;
import com.vdata.cloud.datacenter.mapper.PulverizerPointMapper;
import com.vdata.cloud.datacenter.service.IBaseDictService;
import com.vdata.cloud.datacenter.service.IDcsServerService;
import com.vdata.cloud.datacenter.service.IPointRunService;
import com.vdata.cloud.datacenter.service.IPulverizerPointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.text.ParseException;

/**
 * @ProjectName: sjzl-master
 * @Package: com.vdata.cloud.datacenter
 * @ClassName: RedisInitRunnerImpl
 * @Author: HK
 * @Description: 初始化磨煤机数据到redis
 * @Date: 2021/8/2 14:26
 * @Version: 1.0
 */
@Component
public class RedisInitRunnerImpl implements CommandLineRunner {

    @Autowired
    private IPulverizerPointService pulverizerPointService;

    @Autowired
    private IBaseDictService baseDictService;


    @Autowired
    private DcsServerInfoMapper dcsServerInfoMapper;
    public static boolean restart = false;


    @Autowired
    private PulverizerPointMapper pulverizerPointMapper;

    @Autowired
    private IPointRunService pointRunService;

    @Autowired
    private RedisTemplate redisTemplate;


//    @Autowired
//    private GetData getData;


    @Autowired
    private IDcsServerService dcsServerService;

    @Override
    public void run(String... args) throws ParseException {
        pulverizerPointService.listSaveRedis();
        baseDictService.listRedis();

        //补充数据
//        pointRunService.datafilling();

    }


}