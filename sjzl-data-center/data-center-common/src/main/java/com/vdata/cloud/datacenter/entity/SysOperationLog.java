package com.vdata.cloud.datacenter.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;
import org.springframework.data.annotation.Id;

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
@TableName("sys_operation_log")
public class SysOperationLog implements Serializable {

    @Tolerate
    public SysOperationLog() {
    }

    private static final long serialVersionUID = 1L;

    //主键
    @TableId(type = IdType.INPUT)
    @Id
    private String id;

    //日志类型
    @ApiModelProperty(value = "日志类型")
    @TableField("LOGTYPE")
    private String logtype;

    //日志名称
    @ApiModelProperty(value = "日志名称")
    @TableField("LOGNAME")
    private String logname;

    //登录用户名
    @ApiModelProperty(value = "登录用户名")
    @TableField("LOGINNAME")
    private String loginname;

    //用户id
    @ApiModelProperty(value = "用户id")
    @TableField("USERID")
    private String userid;

    /**
     * 组id
     */
    @TableField(exist = false)
    private String fkBaseGroup;

    //类名称
    @ApiModelProperty(value = "类名称")
    @TableField("CLASSNAME")
    private String classname;

    //方法名称
    @ApiModelProperty(value = "方法名称")
    @TableField("METHOD")
    private String method;

    //创建时间
    @ApiModelProperty(value = "创建时间")
    @TableField("CREATETIME")
    private Date createtime;

    //是否成功
    @ApiModelProperty(value = "是否成功")
    @TableField("SUCCEED")
    private String succeed;

    //备注
    @ApiModelProperty(value = "备注")
    @TableField("MESSAGE")
    private String message;

    //请求IP
    @ApiModelProperty(value = "请求IP")
    @TableField("IP")
    private String ip;


}
