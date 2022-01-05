package com.vdata.cloud.datacenter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.vdata.cloud.datacenter.entity.PulverizerPoint;
import com.vdata.cloud.datacenter.vo.PulverizerPointPageVO;
import com.vdata.cloud.datacenter.vo.PulverizerPointRedisVO;
import com.vdata.cloud.datacenter.vo.PulverizerPointVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @ProjectName: qsrd
 * @Package: com.vdata.cloud.datacenter.mapper
 * @ClassName: PulverizerPointMapper
 * @Author: HK
 * @Description:
 * @Date: 2021/7/20 13:51
 * @Version: 1.0
 */
public interface PulverizerPointMapper extends BaseMapper<PulverizerPoint> {

    IPage<PulverizerPointVO> page(@Param("pulverizerPointIPage") IPage<PulverizerPoint> pulverizerPointIPage, @Param("obj") PulverizerPointPageVO pulverizerPointPageVO);

    PulverizerPointVO get(int id);

    List<PulverizerPointRedisVO> details();

    int existsNoCountByPulverizerCode(PulverizerPoint pulverizerPoint);
}
