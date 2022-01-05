package com.vdata.cloud.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 
 * 
 * @author XuBo
 * @date 2020-09-28 12:58:54
 */
@ApiModel
@Data
@TableName("base_user_group_rel")
public class BaseUserGroupRel implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//主键
    @TableId(type = IdType.AUTO)
    private Integer id;
	
	//用户id
    @ApiModelProperty(value = "用户id")
    @TableField("fk_base_user")
    private Integer fkBaseUser;

    //组群id
    @ApiModelProperty(value = "组群id")
    @TableField("fk_base_group")
    private String fkBaseGroup;

    //组群类型（1：角色， 2：机构）
    @ApiModelProperty(value = "组群类型")
    @TableField("group_type")
    private String groupType;

}
