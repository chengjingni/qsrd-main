package com.vdata.cloud.auth.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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
@Document(collection = "sysOperationLog")
public class SysOperationLogEx implements Serializable {

    @Tolerate
    public SysOperationLogEx() {
    }

    private static final long serialVersionUID = 1L;

    //主键
    @Id
    private String id;

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

    /**
     * 组id
     */
    @TableField(exist = false)
    private String fkBaseGroup;

    //类名称
    @ApiModelProperty(value = "类名称")
    private String classname;

    //方法名称
    @ApiModelProperty(value = "方法名称")
    private String method;

    //创建时间
    @ApiModelProperty(value = "创建时间")
    private Date createtime;

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
