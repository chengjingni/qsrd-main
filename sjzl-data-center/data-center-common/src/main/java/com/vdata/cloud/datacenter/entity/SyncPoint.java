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

/**
 * 点位同步表实体类
 *
 * @author hk
 * @date 2021-11-11 14:40:54
 */
@ApiModel
@Data
@TableName("sync_point")
public class SyncPoint implements Serializable {
    private static final long serialVersionUID = 1L;
    //
    @ApiModelProperty(value = "")
    @TableId(value = "name", type = IdType.INPUT)
    private String name;
    //
    @TableField("id")
    private Integer id;

    //
    @ApiModelProperty(value = "")
    @TableField("descp")
    private String descp;

    //
    @ApiModelProperty(value = "")
    @TableField("eqid")
    private String eqid;

    //
    @ApiModelProperty(value = "")
    @TableField("eqname")
    private String eqname;

    //
    @ApiModelProperty(value = "")
    @TableField("func")
    private String func;

    //
    @ApiModelProperty(value = "")
    @TableField("high")
    private Integer high;

    //
    @ApiModelProperty(value = "")
    @TableField("highhigh")
    private Integer highhigh;

    //
    @ApiModelProperty(value = "")
    @TableField("location")
    private String location;

    //
    @ApiModelProperty(value = "")
    @TableField("low")
    private Integer low;

    //
    @ApiModelProperty(value = "")
    @TableField("lowlow")
    private Integer lowlow;

    //
    @ApiModelProperty(value = "")
    @TableField("max")
    private BigDecimal max;

    //
    @ApiModelProperty(value = "")
    @TableField("min")
    private BigDecimal min;


    //
    @ApiModelProperty(value = "")
    @TableField("roles")
    private String roles;

    //
    @ApiModelProperty(value = "")
    @TableField("test")
    private String test;

    //
    @ApiModelProperty(value = "")
    @TableField("type")
    private String type;

    //
    @ApiModelProperty(value = "")
    @TableField("typical")
    private Integer typical;

    //
    @ApiModelProperty(value = "")
    @TableField("unit")
    private String unit;


}
