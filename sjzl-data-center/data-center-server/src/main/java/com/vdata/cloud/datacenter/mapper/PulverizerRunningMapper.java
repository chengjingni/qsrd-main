package com.vdata.cloud.datacenter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vdata.cloud.datacenter.entity.PulverizerRunning;
import org.apache.ibatis.annotations.Param;

/**
 * @ProjectName: sjzl-master
 * @Package: com.vdata.cloud.datacenter.mapper
 * @ClassName: PulverizerRunningMapper
 * @Author: HK
 * @Description:
 * @Date: 2021/8/3 14:13
 * @Version: 1.0
 */
public interface PulverizerRunningMapper extends BaseMapper<PulverizerRunning> {
    void addDay(@Param("pulverizerCode") String pulverizerCode);

    void zero(@Param("pulverizerCode") String pulverizerCode);

}
