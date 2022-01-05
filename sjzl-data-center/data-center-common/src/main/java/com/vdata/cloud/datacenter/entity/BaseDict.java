package com.vdata.cloud.datacenter.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import java.io.Serializable;
import java.util.Date;

/**
 * 实体类
 *
 * @author hk
 * @date 2021-07-19 14:34:08
 */
@ApiModel
@Data
@TableName("base_dict")
@Validated
@NoArgsConstructor
public class BaseDict implements Serializable {
    private static final long serialVersionUID = 1L;

    public BaseDict(String type, String code) {
        this.type = type;
        this.code = code;
    }

    //id
    @TableId(type = IdType.INPUT)
    private Integer id;

    //如果是1级目录则为root
    @ApiModelProperty(value = "如果是1级目录则为root")
    @TableField("type")
    private String type;

    //代码
    @ApiModelProperty(value = "代码")
    @TableField("code")
    private String code;

    //代码值
    @ApiModelProperty(value = "代码值")
    @TableField("value")
    private String value;

    //描述
    @ApiModelProperty(value = "描述")
    @TableField("description")
    private String description;

    //创建时间
    @ApiModelProperty(value = "创建时间")
    @TableField("create_time")
    private Date createTime;

    //修改时间
    @ApiModelProperty(value = "修改时间")
    @TableField("update_time")
    private Date updateTime;

    //创建人
    @ApiModelProperty(value = "创建人")
    @TableField("create_user")
    private String createUser;

    //修改人
    @ApiModelProperty(value = "修改人")
    @TableField("update_user")
    private String updateUser;

    //删除标识
    @ApiModelProperty(value = "删除标识")
    @TableField("delete_time")
    private Date deleteTime;

    //排序
    @ApiModelProperty(value = "排序")
    @TableField("sort")
    private Integer sort;


}
