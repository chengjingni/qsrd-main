package com.vdata.cloud.datacenter.vo;

import lombok.Data;

import java.util.Date;

/**
 * @ProjectName: sjzl-master
 * @Package: com.vdata.cloud.datacenter.vo
 * @ClassName: PointRunTimeVO
 * @Author: HK
 * @Description:
 * @Date: 2021/8/5 13:54
 * @Version: 1.0
 */
@Data
public class PointRunTimeVO {

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
    //检测值
    private double value;
    //运行时间
    private Date time;
}
