package com.vdata.cloud.admin.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 用户系统关联VO
 */
@Data
@ApiModel
@Builder
public class BaseUserSystemRelVO implements Serializable {
    //主键
    @ApiModelProperty(name = "id",notes = "主键",required =true,dataType = "Integer")
    private Integer id;

    //用户id
    @ApiModelProperty(name = "fkbaseuser",notes = "用户id",required =true,dataType = "Integer")
    @NotNull(message = "用户id不能为空")
    private Integer fkbaseUser;

    //应用id
    @ApiModelProperty(name = "appId",notes = "应用id",required =true,dataType = "String")
    @NotEmpty(message = "应用id不能为空")
    private String appId;

    //系统名称
    @ApiModelProperty(name = "systemName",notes = "系统名称",required =true,dataType = "String")
    @NotEmpty(message = "系统名称不能为空")
    private String systemName;

    //系统回调url
    @ApiModelProperty(name = "backUrl",notes = "系统回调url",required =false,dataType = "String")
    private String backUrl;

}
