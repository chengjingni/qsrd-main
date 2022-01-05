package com.vdata.cloud.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vdata.cloud.admin.entity.Group;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface GroupMapper extends BaseMapper<Group> {
    void deleteGroupMembersById(@Param("groupId") String groupId);

    void deleteGroupLeadersById(@Param("groupId") String groupId);

    void insertGroupMembersById(@Param("groupId") String groupId, @Param("userId") int userId);

    void insertGroupLeadersById(@Param("groupId") String groupId, @Param("userId") int userId);

    List<Map<String, Object>> getUserGroupInfo(@Param("userId") int userId);
}