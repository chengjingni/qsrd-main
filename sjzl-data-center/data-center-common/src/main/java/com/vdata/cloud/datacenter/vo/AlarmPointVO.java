package com.vdata.cloud.datacenter.vo;

import lombok.Data;

import java.util.List;

/**
 * @ProjectName: qsrd-main
 * @Package: com.vdata.cloud.datacenter.vo
 * @ClassName: AlarmPointVO
 * @Author: HK
 * @Description:
 * @Date: 2021/12/22 13:53
 * @Version: 1.0
 */
@Data
public class AlarmPointVO {
    private String code;
    private String value;
    private String pulverizerPointIds;
    private List<PulverizerPointRedisVO> pulverizerPointRedisVOS;
}
