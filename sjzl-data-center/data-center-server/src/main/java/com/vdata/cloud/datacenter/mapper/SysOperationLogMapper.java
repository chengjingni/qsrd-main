package com.vdata.cloud.datacenter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.vdata.cloud.datacenter.entity.SysOperationLog;
import com.vdata.cloud.datacenter.vo.BaseGroupVO;
import com.vdata.cloud.datacenter.vo.ULogGroupVO;
import com.vdata.cloud.datacenter.vo.ULogVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 操作日志
 *
 * @author hk
 * @date 2020-11-03 11:17:50
 */
public interface SysOperationLogMapper extends BaseMapper<SysOperationLog> {

    List<Map<String, Object>> userLoginLineChart(Map<String, Object> param);

    List<ULogGroupVO> queryList(@Param("uLogVO") ULogVO uLogVO, @Param("sysOperationLogIPage") IPage<ULogGroupVO> sysOperationLogIPage);

    BaseGroupVO getBaseGroupVO(@Param("userId") String userId);
}
