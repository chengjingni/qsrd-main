package com.vdata.cloud.admin.biz;

import com.vdata.cloud.admin.entity.GateLog;
import com.vdata.cloud.admin.mapper.GateLogMapper;
import com.vdata.cloud.common.rest.BaseBiz;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * ${DESCRIPTION}
 *
 * @author wanghaobin
 * @create 2017-07-01 14:36
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class GateLogBiz extends BaseBiz<GateLogMapper, GateLog> {

    @Resource
    private GateLogMapper mapper;

    public void insertSelective(GateLog entity) {
        mapper.insert(entity);
    }
}
