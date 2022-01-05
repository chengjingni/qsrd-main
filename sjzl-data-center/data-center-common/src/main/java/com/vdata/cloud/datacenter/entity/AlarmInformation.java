package com.vdata.cloud.datacenter.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 实体类
 *
 * @author hk
 * @date 2021-07-21 14:43:16
 */
@ApiModel
@Data
@TableName("alarm_information")
public class AlarmInformation implements Serializable {
    private static final long serialVersionUID = 1L;

    //
    @TableId(type = IdType.AUTO)
    private Integer id;

    //报警时间
    @ApiModelProperty(value = "报警时间")
    @TableField("alarm_time")
    private Date alarmTime;

    //报警类型代码
    @ApiModelProperty(value = "报警类型代码")
    @TableField("abnormal_code")
    private String abnormalCode;

    //磨煤机点位id
    @ApiModelProperty(value = "磨煤机点位id")
    @TableField("pulverizer_point_id")
    private Integer pulverizerPointId;

    //检测值
    @ApiModelProperty(value = "检测值")
    @TableField("detection_value")
    private String detectionValue;

    //报警次数
    @ApiModelProperty(value = "报警次数")
    @TableField("count")
    private Integer count;

    //报警描述
    @ApiModelProperty(value = "报警描述")
    @TableField("alarm_description")
    private String alarmDescription;

    //误报:1  真实故障:2
    @ApiModelProperty(value = "误报:1  真实故障:2")
    @TableField("verify_result")
    private Integer verifyResult;

    //核实描述
    @ApiModelProperty(value = "核实描述")
    @TableField("verify_description")
    private String verifyDescription;

    //核实中:1 已核实:2 处理中:3 已处理:4
    @ApiModelProperty(value = "核实中:1 已核实:2 处理中:3 已处理:4")
    @TableField("result_status")
    private Integer resultStatus;

    //处理描述
    @ApiModelProperty(value = "处理描述")
    @TableField("process_description")
    private String processDescription;

    //处理时间
    @ApiModelProperty(value = "处理时间")
    @TableField("finish_date")
    private Date finishDate;


    //可能原因
    @ApiModelProperty(value = "可能原因")
    @TableField("possible_cause")
    private String possibleCause;

    //处理建议
    @ApiModelProperty(value = "处理建议")
    @TableField("proposal")
    private String proposal;


    //部位代码
    @ApiModelProperty(value = "部位代码")
    @TableField("position_code")
    private String positionCode;

}
