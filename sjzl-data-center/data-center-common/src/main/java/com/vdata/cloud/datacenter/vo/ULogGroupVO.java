package com.vdata.cloud.datacenter.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @ProjectName: wru-master
 * @Package: com.vdata.cloud.datacenter.vo
 * @ClassName: ULogGroupVO
 * @Author: HK
 * @Description: 操作日志组信息
 * @Date: 2020/11/19 14:06
 * @Version: 1.0
 */
@Getter
@Setter
public class ULogGroupVO {

    private static final long serialVersionUID = 1L;

    //主键
    @TableId(type = IdType.INPUT)
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


    @ApiModelProperty(value = "组名称")
    private String groupName;


}
