package com.vdata.cloud.datacenter.vo;

import com.vdata.cloud.datacenter.entity.AlarmPoint;
import lombok.Data;

import java.util.List;

/**
 * @ProjectName: qsrd-main
 * @Package: com.vdata.cloud.datacenter.vo
 * @ClassName: AlarmPointJoinVO
 * @Author: HK
 * @Description:
 * @Date: 2021/12/23 9:50
 * @Version: 1.0
 */
@Data
public class AlarmPointJoinVO {

    private String alarmCode;
    private List<AlarmPoint> alarmPointList;
}
