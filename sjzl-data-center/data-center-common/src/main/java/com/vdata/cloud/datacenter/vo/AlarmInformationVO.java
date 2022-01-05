package com.vdata.cloud.datacenter.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 实体类
 *
 * @author hk
 * @date 2021-07-21 14:43:16
 */
@Data
public class AlarmInformationVO {

    //
    private Integer id;

    //报警时间
    @ApiModelProperty(value = "报警时间")
    private Date alarmTime;

    //报警类型代码
    @ApiModelProperty(value = "报警类型代码")
    private String abnormalCode;

    //报警类型
    private String abnormalValue;

    //磨煤机点位id
    @ApiModelProperty(value = "磨煤机点位id")
    private Integer pulverizerPointId;

    @ApiModelProperty(value = "磨煤机点位名称")
    private String pointName;

    //磨煤机代码
    @ApiModelProperty(value = "磨煤机代码")
    private String pulverizerCode;


    //磨煤机值
    @ApiModelProperty(value = "磨煤机值")
    private String pulverizerValue;

    //检测值
    @ApiModelProperty(value = "检测值")
    private String detectionValue;

    //报警次数
    @ApiModelProperty(value = "报警次数")
    private Integer count;

    //报警描述
    @ApiModelProperty(value = "报警描述")
    private String alarmDescription;

    //误报:1  真实报警:2
    @ApiModelProperty(value = "误报:1  真实报警:2")
    private Integer verifyResult;

    //核实名称
    @ApiModelProperty(value = "误报:1  真实报警:2")
    private String verifyResultValue;

    //核实描述
    @ApiModelProperty(value = "核实描述")
    private String verifyDescription;

    //核实中:1 已核实:2 处理中:3 已处理:4
    @ApiModelProperty(value = "核实中:1 已核实:2 处理中:3 已处理:4")
    private Integer resultStatus;

    //处理名称
    private String resultStatusValue;

    //处理描述
    @ApiModelProperty(value = "处理描述")
    private String processDescription;

    //处理时间
    @ApiModelProperty(value = "处理时间")
    private Date finishDate;


    //可能原因
    private String possibleCause;

    //处理建议
    private String proposal;


    //部位代码
    private String positionCode;


    //部位名称
    private String positionValue;

    private String nos;


}
