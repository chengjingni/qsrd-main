package com.vdata.cloud.admin.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * 组VO
 */
@Data
@ApiModel
public class GroupVO implements Serializable {
    //主键
    @ApiModelProperty(name = "id",notes = "主键",required =true,dataType = "Integer")
    private String id;

    //角色编码
    @ApiModelProperty(name = "code",notes = "角色编码",required =true,dataType = "String")
    @NotEmpty(message = "角色编码不能为空")
    private String code;

    //角色编码
    @ApiModelProperty(name = "name",notes = "角色名称",required =true,dataType = "String")
    @NotEmpty(message = "角色名称不能为空")
    private String name;

    //上级节点
    @ApiModelProperty(name = "parentId",notes = "上级节点",required =true,dataType = "String")
    @NotNull(message = "上级节点不能为空")
    private String parentId;

    //树状关系
    @ApiModelProperty(name = "path",notes = "树状关系",required =false,dataType = "String")
    private String path;

    //类型
    @ApiModelProperty(name = "type",notes = "类型",required =false,dataType = "String")
    private String type;

    //角色组类型
    @ApiModelProperty(name = "groupType",notes = "角色组类型",required =true,dataType = "Integer")
    @NotNull(message = "角色组类型不能为空")
    private Integer groupType;

    //描述
    @ApiModelProperty(name = "description",notes = "描述",required =false,dataType = "String")
    private String description;

    //创建时间
    @ApiModelProperty(name = "crtTime",notes = "创建时间",required =false,dataType = "Date")
    private Date crtTime;

    //创建人id
    @ApiModelProperty(name = "crtUser",notes = "创建人id",required =false,dataType = "String")
    private String crtUser;

    //创建人名称
    @ApiModelProperty(name = "crtName",notes = "创建人名称",required =false,dataType = "String")
    private String crtName;

    //创建者ip
    @ApiModelProperty(name = "crtHost",notes = "创建者ip",required =false,dataType = "String")
    private String crtHost;

    //修改时间
    @ApiModelProperty(name = "updTime",notes = "修改时间",required =false,dataType = "Date")
    private Date updTime;

    //修改人id
    @ApiModelProperty(name = "updUser",notes = "修改人id",required =false,dataType = "String")
    private String updUser;

    //修改人名称
    @ApiModelProperty(name = "updName",notes = "修改人名称",required =false,dataType = "String")
    private String updName;

    //修改者ip
    @ApiModelProperty(name = "updHost",notes = "修改者ip",required =false,dataType = "String")
    private String updHost;

    //状态
    @ApiModelProperty(name = "status",notes = "状态",required =false,dataType = "String")
    private String status;

    //排序
    @ApiModelProperty(name = "sort",notes = "排序",required =false,dataType = "String")
    private String sort;


}
