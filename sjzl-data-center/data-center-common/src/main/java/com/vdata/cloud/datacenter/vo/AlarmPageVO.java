package com.vdata.cloud.datacenter.vo;

import lombok.Data;

/**
 * @ProjectName: sjzl-master
 * @Package: com.vdata.cloud.datacenter.vo
 * @ClassName: AlarmPageVO
 * @Author: HK
 * @Description:
 * @Date: 2021/7/21 15:07
 * @Version: 1.0
 */
@Data
public class AlarmPageVO {
    //类型  今日报警为1  待处理为2  处理中为3 报警总数为4  默认显示全部
    private int type;
    //页码
    private long page;
    //条数
    private long size;


    //磨煤机代码
    private String pulverizerCode;
    //报警类型
    private String AlarmsTypeCode;
    //审核类型
    private int verifyResult;
    //起始时间
    private String startDate;

    //结束时间
    private String endDate;

    //报警点位
    private String point;

    //处理状态
    private Integer resultStatus;

    //报警类型
    private String abnormalCode;

    //部位代码
    private String positionCode;


    //是否倒序
    private String asc;
}
