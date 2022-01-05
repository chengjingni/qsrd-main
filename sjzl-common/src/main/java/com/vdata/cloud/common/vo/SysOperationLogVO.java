package com.vdata.cloud.common.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.io.Serializable;
import java.util.Date;

/**
 * 操作日志
 *
 * @author hk
 * @date 2020-11-03 11:17:50
 */
@Builder
@ApiModel
@Data
public class SysOperationLogVO implements Serializable {

    @Tolerate
    public SysOperationLogVO() {
    }

    private static final long serialVersionUID = 1L;


    //日志类型
    @ApiModelProperty(value = "日志类型")
    private String logtype;

    //日志名称
    @ApiModelProperty(value = "日志名称")
    private String logname;

    //登录用户名
    @ApiModelProperty(value = "登录用户名")
    private String loginname;

    //用户id
    @ApiModelProperty(value = "用户id")
    private String userid;

    //类名称
    @ApiModelProperty(value = "类名称")
    private String classname;

    //方法名称
    @ApiModelProperty(value = "方法名称")
    private String method;

    //是否成功
    @ApiModelProperty(value = "是否成功")
    private String succeed;

    //备注
    @ApiModelProperty(value = "备注")
    private String message;

    //请求IP
    @ApiModelProperty(value = "请求IP")
    private String ip;


}
