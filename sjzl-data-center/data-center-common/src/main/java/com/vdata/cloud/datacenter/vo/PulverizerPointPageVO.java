package com.vdata.cloud.datacenter.vo;

import lombok.Data;

/**
 * @ProjectName: qsrd
 * @Package: com.vdata.cloud.datacenter.vo
 * @ClassName: PulverizerPointPageVO
 * @Author: HK
 * @Description:
 * @Date: 2021/7/20 14:11
 * @Version: 1.0
 */
@Data
public class PulverizerPointPageVO {
    //燃煤机编码
    private String code;
    //页码
    private long page;
    //条数
    private long size;
    //所属部位
    private String positionCode;
}
