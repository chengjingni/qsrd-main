package com.vdata.cloud.datacenter.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 实体类
 *
 * @author hk
 * @date 2021-07-21 11:48:25
 */
@ApiModel
@Data
@TableName("dcs_server_info")
public class DcsServerInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    //dcs服务ip
    @ApiModelProperty(value = "dcs服务端口")
    @TableField("`ip`")
    private String ip;

    //dcs服务端口
    @ApiModelProperty(value = "dcs服务端口")
    @TableField("`port`")
    private Integer port;


}
