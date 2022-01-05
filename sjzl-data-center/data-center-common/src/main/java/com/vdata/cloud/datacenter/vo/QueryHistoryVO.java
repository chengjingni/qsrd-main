package com.vdata.cloud.datacenter.vo;

import lombok.Data;

/**
 * @ProjectName: qsrd-main
 * @Package: com.vdata.cloud.datacenter.vo
 * @ClassName: QueryHistoryVO
 * @Author: HK
 * @Description: 查询历史时间段数据
 * @Date: 2021/11/8 14:26
 * @Version: 1.0
 */
@Data
public class QueryHistoryVO {
    /**
     * 点名
     */
    private String tagName;


    /**
     * 起始时间
     */
    private long stTime;

    /**
     * 结束时间
     */
    private long edTime;

    /**
     * 时间间隔（秒）
     */
    private int interval = 1;


}
