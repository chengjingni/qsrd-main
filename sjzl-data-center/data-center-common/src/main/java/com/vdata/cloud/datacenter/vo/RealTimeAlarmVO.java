package com.vdata.cloud.datacenter.vo;

import lombok.Data;

/**
 * @ProjectName: sjzl-master
 * @Package: com.vdata.cloud.common.vo
 * @ClassName: RealTimeAlarmVO
 * @Author: HK
 * @Description:
 * @Date: 2021/8/2 16:51
 * @Version: 1.0
 */

@Data
public class RealTimeAlarmVO {
    //设备
    private String pulverizer;
    //部位
    private String position;
    //运行状态
    private String running;
    //报警数
    private String count;


    //磨煤机编码
    private String code;
}
