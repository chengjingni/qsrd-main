package com.vdata.cloud.admin.entity;

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
 * 数据字典实体类
 *
 * @author admin
 * @date 2020-11-20 09:36:49
 */
@ApiModel
@Data
@TableName("data_dic_tbl")
public class DataDicTbl implements Serializable {
    private static final long serialVersionUID = 1L;

    //主键
    @TableId(type = IdType.AUTO)
    private Integer id;

    //类型
    @ApiModelProperty(value = "类型")
    @TableField("TYPE")
    private String type;

    //代码
    @ApiModelProperty(value = "代码")
    @TableField("CODE")
    private String code;

    //名称
    @ApiModelProperty(value = "名称")
    @TableField("NAME")
    private String name;

    //排序
    @ApiModelProperty(value = "排序")
    @TableField("SORT")
    private Integer sort;

    //修改时间
    @ApiModelProperty(value = "修改时间")
    @TableField("UPD_TIME")
    private Date updTime;

    @ApiModelProperty(value = "备注")
    @TableField("REMARK")
    private String remark;


}
