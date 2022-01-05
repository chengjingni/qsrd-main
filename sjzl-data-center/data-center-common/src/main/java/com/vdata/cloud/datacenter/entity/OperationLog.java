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
@TableName("operation_log")
public class OperationLog implements Serializable {
    private static final long serialVersionUID = 1L;

    //id
    @TableId(type = IdType.AUTO)
    private Integer id;

    //报警id
    @ApiModelProperty(value = "")
    @TableField("alarm_information_id")
    private Integer alarmInformationId;

    //描述
    @ApiModelProperty(value = "")
    @TableField("description")
    private String description;

    //核实中:1 已核实:2 处理中:3 已处理:4
    @ApiModelProperty(value = "核实中:1 已核实:2 处理中:3 已处理:4")
    @TableField("result_status")
    private Integer resultStatus;


    //误报:1  真实故障:2
    @ApiModelProperty(value = "误报:1  真实故障:2")
    @TableField("verify_result")
    private Integer verifyResult;

    //登录名
    @ApiModelProperty(value = "登录名")
    @TableField("user_name")
    private String userName;


    //昵称
    @ApiModelProperty(value = "昵称")
    @TableField("nick_name")
    private String nickName;

    //发生时间
    @ApiModelProperty(value = "")
    @TableField("date_time")
    private Date dateTime;


}
