package com.vdata.cloud.datacenter.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @ProjectName: wru-master
 * @Package: com.vdata.cloud.datacenter.vo
 * @ClassName: ULogVo
 * @Author: HK
 * @Description:
 * @Date: 2020/11/4 11:13
 * @Version: 1.0
 */
@ApiModel
@Data
public class ULogVO {
    @ApiModelProperty(value = "日志类型")
    String logType;
    @ApiModelProperty(value = "日志名称")
    String logname;

    @ApiModelProperty(value = "搜索")
    String search;


    @ApiModelProperty(value = "是否成功")
    String succeed;

    @ApiModelProperty(value = "排序字段 createtime")
    String sortField;

    @ApiModelProperty(value = "排序方式  0倒序  1正序")
    String sortType;


    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Alisa/Shanghai")
    @ApiModelProperty(value = "起始时间")
    Date startDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Alisa/Shanghai")
    @ApiModelProperty(value = "结束时间")
    Date endDate;


    @ApiModelProperty("页码")
    long page;

    @ApiModelProperty("条数")
    long limit;


    @ApiModelProperty("搜索范围")
    String searchField;


    @ApiModelProperty("登录用户名")
    String loginname;


    @ApiModelProperty("用户id")
    String userId;


    @ApiModelProperty("用户组id")
    String groupId;

}
