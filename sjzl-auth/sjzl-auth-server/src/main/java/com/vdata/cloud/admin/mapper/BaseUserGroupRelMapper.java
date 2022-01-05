package com.vdata.cloud.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vdata.cloud.admin.entity.BaseUserGroupRel;

import java.util.List;

/**
 * @author XuBo
 * @date 2020-09-28 12:58:54
 */
public interface BaseUserGroupRelMapper extends BaseMapper<BaseUserGroupRel> {
    void insertBatch(List<BaseUserGroupRel> list);
}
