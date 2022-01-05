package com.vdata.cloud.admin.biz;


import com.vdata.cloud.admin.entity.BaseUserGroupRel;
import com.vdata.cloud.admin.mapper.BaseUserGroupRelMapper;
import com.vdata.cloud.common.rest.BaseBiz;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * ${DESCRIPTION}
 *
 * @author zhangdi
 * @create 2020-09-28 11:48
 */
@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class BaseUserGroupRelBiz extends BaseBiz<BaseUserGroupRelMapper, BaseUserGroupRel> {

    @Resource
    private BaseUserGroupRelMapper baseUserGroupRelMapper;

}
