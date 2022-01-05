package com.vdata.cloud.admin.biz;


import com.vdata.cloud.admin.entity.GroupType;
import com.vdata.cloud.admin.mapper.GroupTypeMapper;
import com.vdata.cloud.common.rest.BaseBiz;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ${DESCRIPTION}
 *
 * @author wanghaobin
 * @create 2017-06-12 8:48
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class GroupTypeBiz extends BaseBiz<GroupTypeMapper, GroupType> {
}
