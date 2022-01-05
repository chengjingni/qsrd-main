package com.vdata.cloud.datacenter.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @ProjectName: qsrd
 * @Package: com.vdata.cloud.datacenter.vo
 * @ClassName: PulverizerPointVO
 * @Author: HK
 * @Description:
 * @Date: 2021/7/20 15:44
 * @Version: 1.0
 */
@Data
public class PulverizerPointVO {
    //id
    private int id;

    //排序
    private int no;

    //磨煤机代码
    @ApiModelProperty(value = "磨煤机代码")
    private String pulverizerCode;

    //点位名称
    @ApiModelProperty(value = "点位名称")
    private String pointName;

    //传感器类型代码
    @ApiModelProperty(value = "传感器类型代码")
    private String sensorTypeCode;

    //传感器类型代码
    @ApiModelProperty(value = "传感器类型")
    private String sensorType;


    //解析标识符
    private String dcsDataIdentifier;

    //检测项代码
    @ApiModelProperty(value = "检测项代码")
    private String testItemCode;

    //检测项
    private String testItem;

    //单位
    private String unit;


    //部位代码
    private String positionCode;


    //部位值
    private String positionValue;


    //上限
    private BigDecimal upperLimit;


    //下限
    private BigDecimal lowerLimit;


    //是否被关注   未关注:0  关注:1
    private BigDecimal follow;


    //是否需要报警 为0 不需要报警 为1需要报警
    private int alarm;


    private String pulverizerName;


}
