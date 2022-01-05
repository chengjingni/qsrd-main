package com.vdata.cloud.datacenter.vo;

import lombok.Data;

/**
 * @ProjectName: qsrd-main
 * @Package: com.vdata.cloud.datacenter.vo
 * @ClassName: PointListQuyerVO
 * @Author: HK
 * @Description:
 * @Date: 2021/12/13 11:35
 * @Version: 1.0
 */
@Data
public class PointListQuyerVO {
    String pulverizerCode;
    String startTime;
    String endTime;
    Long start;
    Integer count;

    int orderByDesc;
}
