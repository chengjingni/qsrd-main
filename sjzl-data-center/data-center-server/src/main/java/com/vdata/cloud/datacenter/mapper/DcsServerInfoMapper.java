package com.vdata.cloud.datacenter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vdata.cloud.datacenter.entity.DcsServerInfo;
import org.apache.ibatis.annotations.Param;

/**
 * @ProjectName: sjzl-master
 * @Package: com.vdata.cloud.datacenter.mapper
 * @ClassName: DcsServerInfoMapper
 * @Author: HK
 * @Description: dcs服务信息
 * @Date: 2021/7/21 11:49
 * @Version: 1.0
 */
public interface DcsServerInfoMapper extends BaseMapper<DcsServerInfo> {
    void update(@Param("dcsServerInfo") DcsServerInfo dcsServerInfo);
}
