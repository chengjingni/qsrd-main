package com.vdata.cloud.datacenter.service.impl;

import com.vdata.cloud.datacenter.component.GetData;
import com.vdata.cloud.datacenter.constants.CommonConstans;
import com.vdata.cloud.datacenter.entity.DcsServerInfo;
import com.vdata.cloud.datacenter.mapper.DcsServerInfoMapper;
import com.vdata.cloud.datacenter.service.IDcsServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @ProjectName: sjzl-master
 * @Package: com.vdata.cloud.datacenter.service.impl
 * @ClassName: DcsServerServiceImpl
 * @Author: HK
 * @Description:
 * @Date: 2021/8/11 17:51
 * @Version: 1.0
 */
@Service
public class DcsServerServiceImpl implements IDcsServerService {

    @Autowired
    private GetData getData;

    @Autowired
    private DcsServerInfoMapper dcsServerInfoMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    @Async
    public void restart() {
        GetData.restart = true;
        getData.run();
    }

    @Override
    public void loadRedis() {
        DcsServerInfo dcsServerInfo = dcsServerInfoMapper.selectOne(null);


//        rt.opsForHash().putAll(CommonConstans.BASE_DICT_REDIS, map);

        redisTemplate.opsForValue().set(CommonConstans.DCSSERVERINFO_REDIS, dcsServerInfo.getIp() + "|" + dcsServerInfo.getPort());
    }
}
