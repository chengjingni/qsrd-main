package com.vdata.cloud.datacenter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vdata.cloud.datacenter.entity.AlarmPoint;
import com.vdata.cloud.datacenter.vo.AlarmPointVO;

import java.util.List;

/**
 * @author hk
 * @date 2021-07-21 14:43:16
 */
public interface AlarmPointMapper extends BaseMapper<AlarmPoint> {


    List<AlarmPointVO> joinPointList();

}
