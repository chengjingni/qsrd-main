package com.vdata.cloud.datacenter.vo;

import lombok.Data;

import java.util.Date;

/**
 * @ProjectName: sjzl-master
 * @Package: com.vdata.cloud.datacenter.vo
 * @ClassName: AlarmAnalyseQueryVO
 * @Author: HK
 * @Description: 报警分析接口
 * @Date: 2021/7/27 14:41
 * @Version: 1.0
 */
@Data
public class AlarmAnalyseQueryVO {
    //年
    private int year;
    //磨煤机代码
    private String pulverizerCode;
    //报警类型
    private String AlarmsTypeCode;
    //审核类型
    private int verifyResult;

    //起始时间
    private Date startDate;

    //结束时间
    private Date endDate;

    //报警点位
    private String point;

    //处理状态
    private Integer resultStatus;

    //报警类型
    private String abnormalCode;
    

}
