package com.vdata.cloud.datacenter.service;

/**
 * @ProjectName: sjzl-master
 * @Package: com.vdata.cloud.datacenter.service
 * @ClassName: IDcsServerService
 * @Author: HK
 * @Description:
 * @Date: 2021/8/11 17:51
 * @Version: 1.0
 */
public interface IDcsServerService {

    void restart();

    void loadRedis();
}
