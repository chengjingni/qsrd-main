package com.vdata.cloud.datacenter.component;

import com.vdata.cloud.datacenter.mapper.DcsServerInfoMapper;
import com.vdata.cloud.datacenter.mapper.PulverizerPointMapper;
import com.vdata.cloud.datacenter.util.GetPulverizerPointUtils;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @ProjectName: sjzl-master
 * @Package: com.vdata.cloud.datacenter.thread
 * @ClassName: GetDataThread
 * @Author: HK
 * @Description: 获得数据的接口
 * @Date: 2021/8/11 16:43
 * @Version: 1.0
 */
@Log4j2
@Component
public class GetData {

    @Autowired
    private DcsServerInfoMapper dcsServerInfoMapper;
    public static boolean restart = false;


    @Autowired
    private PulverizerPointMapper pulverizerPointMapper;

    @Autowired
    private GetPulverizerPointUtils getPulverizerPointUtils;

    public GetData() {

    }

    @SneakyThrows
    public void run() {
        //初始化
//        init();


        int i = 0;
        while (true) {

            Thread.sleep(1000);

            //运行代码
            exec();

            if (restart) {
                restart = false;
                break;
            }
        }
    }

    //执行代码
    private void exec() {
        //批量获取磨煤机点位信息
/*        LambdaQueryWrapper<PulverizerPoint> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PulverizerPoint::getEnable, 1);

        List<PulverizerPoint> pulverizerPoints = pulverizerPointMapper.selectList(queryWrapper);*/
//        pulverizerPoints.stream().map()
//        getPulverizerPointUtils.postRealTimeDatas();
    }

    //初始化
/*
    private void init() {
        DcsServerInfo dcsServerInfo = dcsServerInfoMapper.selectOne(null);
        String ip = dcsServerInfo.getIp();
        Integer port = dcsServerInfo.getPort();
        getPulverizerPointUtils.setIpAndPort(ip, port);
        log.info("ip:" + ip + ",port:" + port + "初始化完成");
    }
*/


}
