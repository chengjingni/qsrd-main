package com.vdata.cloud.datacenter.vo;

import lombok.Data;

/**
 * @ProjectName: qsrd
 * @Package: com.vdata.cloud.datacenter.vo
 * @ClassName: BaseDictPageVO
 * @Author: HK
 * @Description:
 * @Date: 2021/7/19 17:04
 * @Version: 1.0
 */
@Data
public class BaseDictPageVO {
    //类型
    private String type;
    //页码
    private long page;
    //条数
    private long size;

}
