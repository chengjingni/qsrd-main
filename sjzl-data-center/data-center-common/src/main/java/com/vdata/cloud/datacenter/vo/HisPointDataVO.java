package com.vdata.cloud.datacenter.vo;

import lombok.Data;

import java.util.List;

/**
 * @ProjectName: qsrd-main
 * @Package: com.vdata.cloud.datacenter.vo
 * @ClassName: HisPointDataVO
 * @Author: HK
 * @Description:
 * @Date: 2021/11/30 9:18
 * @Version: 1.0
 */
@Data
public class HisPointDataVO {
    //起始时间
    private String startDate;
    //结束时间
    private String endDate;
 /*   //点位编号
    private int no;
    //磨煤机编号
    private String pulverizerCode;*/


    List<PointVO> pointVOs;
}
