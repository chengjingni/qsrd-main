package com.vdata.cloud.datacenter.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @ProjectName: sjzl-master
 * @Package: com.vdata.cloud.datacenter.vo
 * @ClassName: AbnormalDetailVO
 * @Author: HK
 * @Description:
 * @Date: 2021/7/26 13:45
 * @Version: 1.0
 */
@Data
public class AbnormalDetailVO {

    //id
    @TableId(type = IdType.AUTO)
    private Integer id;

    //报警id
    @ApiModelProperty(value = "报警id")
    private Integer alarmInformationId;

    //磨煤机点位id
    @ApiModelProperty(value = "磨煤机点位id")
    private Integer pulverizerPointId;

    //磨煤机点位名称
    @ApiModelProperty(value = "磨煤机点位名称")
    private String pulverizerPointName;

    //报警时间
    @ApiModelProperty(value = "报警时间")
    private Date alarmTime;

    //报警类型代码
    @ApiModelProperty(value = "报警类型代码")
    private String abnormalCode;

    //报警类型值
    @ApiModelProperty(value = "报警类型值")
    private String abnormalValue;
    //检测值
    @ApiModelProperty(value = "检测值")
    private String detectionValue;

    //报警描述
    @ApiModelProperty(value = "报警描述")
    private String alarmDescription;
}
