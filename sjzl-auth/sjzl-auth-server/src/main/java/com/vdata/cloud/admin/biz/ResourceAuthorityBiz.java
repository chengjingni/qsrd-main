package com.vdata.cloud.admin.biz;


import com.vdata.cloud.admin.entity.ResourceAuthority;
import com.vdata.cloud.admin.mapper.ResourceAuthorityMapper;
import com.vdata.cloud.common.rest.BaseBiz;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Ace on 2017/6/19.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ResourceAuthorityBiz extends BaseBiz<ResourceAuthorityMapper, ResourceAuthority> {
}
