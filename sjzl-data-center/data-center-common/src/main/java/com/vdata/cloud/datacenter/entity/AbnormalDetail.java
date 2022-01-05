package com.vdata.cloud.datacenter.entity;

import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

/**
 * 实体类
 * 
 * @author hk
 * @date 2021-07-21 14:43:16
 */
@ApiModel
@Data
@TableName("abnormal_detail")
public class AbnormalDetail implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//id
    @TableId(type = IdType.AUTO)
    private Integer id;
	
	//报警id
    @ApiModelProperty(value = "报警id")
    @TableField("alarm_information_id")
    private Integer alarmInformationId;
	
	//磨煤机点位id
    @ApiModelProperty(value = "磨煤机点位id")
    @TableField("pulverizer_point_id")
    private Integer pulverizerPointId;
	
	//报警时间
    @ApiModelProperty(value = "报警时间")
    @TableField("alarm_time")
    private Date alarmTime;
	
	//报警类型代码
    @ApiModelProperty(value = "报警类型代码")
    @TableField("abnormal_code")
    private String abnormalCode;
	
	//检测值
    @ApiModelProperty(value = "检测值")
    @TableField("detection_value")
    private String detectionValue;
	
	//报警描述
    @ApiModelProperty(value = "报警描述")
    @TableField("alarm_description")
    private String alarmDescription;
	

}
