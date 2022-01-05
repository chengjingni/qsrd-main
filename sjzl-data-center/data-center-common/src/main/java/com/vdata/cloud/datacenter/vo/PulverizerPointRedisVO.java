package com.vdata.cloud.datacenter.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @ProjectName: sjzl-master
 * @Package: com.vdata.cloud.datacenter.vo
 * @ClassName: PulverizerPointRedisVO
 * @Author: HK
 * @Description:
 * @Date: 2021/8/2 13:40
 * @Version: 1.0
 */
@Data
public class PulverizerPointRedisVO {
    //id 
    private int id;
    //燃煤机代码
    private String pulverizerCode;
    //燃煤机名称
    private String pulverizerValue;
    //传感器类型代码
    private String sensorTypeCode;
    //传感器名称
    private String sensorTypeValue;
    //点位名称
    private String pointName;
    //检测项代码 
    private String testItemCode;
    //检测项名称
    private String testItemValue;
    //部位代码
    private String positionCode;
    //部位名称
    private String positionValue;
    //单位
    private String unit;
    //点位编号
    private int no;
    //解析标识符
    private String dcsDataIdentifier;
    //启用标识  启用为1  未启用为0
    private int enable;


    //上限
    private BigDecimal upperLimit;


    //下限
    private BigDecimal lowerLimit;


    //是否被关注   未关注:0  关注:1
    private BigDecimal follow;

    //是否需要报警 为0 不需要报警 为1需要报警
    private int alarm;


}
