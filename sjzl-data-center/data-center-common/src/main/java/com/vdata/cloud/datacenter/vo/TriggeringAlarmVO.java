package com.vdata.cloud.datacenter.vo;

import lombok.Data;

/**
 * @ProjectName: qsrd-main
 * @Package: com.vdata.cloud.datacenter.vo
 * @ClassName: TriggeringAlarmVO
 * @Author: HK
 * @Description:
 * @Date: 2022/1/6 10:38
 * @Version: 1.0
 */
@Data
public class TriggeringAlarmVO {
    //校验码
    private String key;
    //磨煤机代码
    private String pulverizerCode;
    //报警代码
    private String warningCode;
    //可能原因
    private String possibleCause;

    //处理建议
    private String proposal;

    //所属部位
    private String positionCode;

}
