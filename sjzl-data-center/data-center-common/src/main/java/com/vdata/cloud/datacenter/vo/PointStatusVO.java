package com.vdata.cloud.datacenter.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @ProjectName: qsrd-main
 * @Package: com.vdata.cloud.datacenter.vo
 * @ClassName: PointStatusVO
 * @Author: HK
 * @Description:
 * @Date: 2021/11/18 14:00
 * @Version: 1.0
 */
@Data
public class PointStatusVO implements Serializable {

    private static final long serialVersionUID = -2835624334556250853L;
    //按照磨煤机代码+no
    private String id;
    //当前点位获取数据的最新时间
    private Date runDate;
    //是否正在执行数据获取脚本
    private boolean isEnable;
}
