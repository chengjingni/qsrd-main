package com.vdata.cloud.datacenter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.vdata.cloud.datacenter.entity.AlarmPoint;
import com.vdata.cloud.datacenter.vo.AlarmPointVO;

import java.util.List;

/**
 * @ProjectName: qsrd-main
 * @Package: com.vdata.cloud.datacenter.service
 * @ClassName: IAlarmPointService
 * @Author: HK
 * @Description:
 * @Date: 2021/12/22 11:42
 * @Version: 1.0
 */
public interface IAlarmPointService extends IService<AlarmPoint> {


    void joinPoint(List<AlarmPoint> alarmPointList);

    List<AlarmPointVO> joinPointList();

}
