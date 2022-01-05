package com.vdata.cloud.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vdata.cloud.admin.entity.ResourceAuthority;
import org.apache.ibatis.annotations.Param;

public interface ResourceAuthorityMapper extends BaseMapper<ResourceAuthority> {
    public void deleteByAuthorityIdAndResourceType(@Param("authorityId") String authorityId, @Param("resourceType") String resourceType);
}