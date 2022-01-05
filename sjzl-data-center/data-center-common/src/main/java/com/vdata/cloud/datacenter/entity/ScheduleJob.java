package com.vdata.cloud.datacenter.entity;

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
 * 动态定时任务
 *
 * @author hk
 * @date 2020-09-10 11:09:16
 */
@ApiModel
@TableName("schedule_job")
@Data
public class ScheduleJob implements Serializable {
    private static final long serialVersionUID = -2874324648662526110L;
    @TableId(type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "apiId")
    private Integer jobId;

    @ApiModelProperty(value = "cron表达式")
    private String cron;

    @ApiModelProperty(value = "介绍")
    private String description;

    @ApiModelProperty(value = "执行类名称")
    private String beanName;
    @ApiModelProperty(value = "创建时间")
    @TableField("Crt_Tm")
    private Date crtTm;
    //修改时间
    @ApiModelProperty(value = "修改时间")
    @TableField("Mod_Tm")
    private Date modTm;

}
