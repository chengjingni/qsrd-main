package com.vdata.cloud.datacenter.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 实体类
 *
 * @author hk
 * @date 2021-07-20 13:44:56
 */
@ApiModel
@Data
@TableName("pulverizer_point")
public class PulverizerPoint implements Serializable {
    private static final long serialVersionUID = 1L;

    //id
    @TableId(type = IdType.AUTO)
    private Integer id;

    //磨煤机代码
    @ApiModelProperty(value = "磨煤机代码")
    @TableField("pulverizer_code")
    private String pulverizerCode;

    //传感器类型代ui码
    @ApiModelProperty(value = "传感器类型代码")
    @TableField("sensor_type_code")
    private String sensorTypeCode;

    //点位名称
    @ApiModelProperty(value = "点位名称")
    @TableField("point_name")
    private String pointName;

    //部位代码
    @ApiModelProperty(value = "部位代码")
    @TableField("position_code")
    private String positionCode;


    //检测项代码
    @ApiModelProperty(value = "检测项代码")
    @TableField("test_item_code")
    private String testItemCode;


    @ApiModelProperty(value = "是否启 用")
    @TableField("enable")
    private int enable;

    //单位在传感器类型的描述获取
    @ApiModelProperty(value = "单位在传感器类型的描述获取")
    @TableField("unit")
    private String unit;

    //是否启用

    //对应13个传感器
    @ApiModelProperty(value = "对应13个传感器")
    @TableField("no")
    private Integer no;

    //DCS数据标识符
    @ApiModelProperty(value = "DCS数据标识符")
    @TableField("dcs_data_identifier")
    private String dcsDataIdentifier;


    //上限
    @ApiModelProperty("上限")
    @TableField("upper_limit")
    private BigDecimal upperLimit;


    //下限
    @ApiModelProperty("下限")
    @TableField("lower_limit")
    private BigDecimal lowerLimit;


    //是否被关注   未关注:0  关注:1
    @ApiModelProperty("关注")
    @TableField("follow")
    private BigDecimal follow;

    //是否需要报警 为0 不需要报警 为1需要报警
    @ApiModelProperty("是否需要报警")
    @TableField("alarm")
    private int alarm;


    //创建时间
    @ApiModelProperty(value = "创建时间")
    @TableField("create_date")
    private Date createDate;

    //修改时间
    @ApiModelProperty(value = "修改时间")
    @TableField("update_date")
    private Date updateDate;

    //创建人
    @ApiModelProperty(value = "创建人")
    @TableField("create_user")
    private String createUser;

    //修改人
    @ApiModelProperty(value = "修改人")
    @TableField("update_user")
    private String updateUser;

    //删除标志
    @ApiModelProperty(value = "删除标志")
    @TableField("delete_date")
    private Date deleteDate;


}
