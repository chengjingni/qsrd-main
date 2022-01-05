package com.vdata.cloud.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.vdata.cloud.admin.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface UserMapper extends BaseMapper<User> {
    List<User> selectMemberByGroupId(@Param("groupId") String groupId);

    List<User> selectLeaderByGroupId(@Param("groupId") String groupId);

    List<User> userList(@Param("page") IPage<User> page, @Param("params") Map<String, Object> params);

    List<Map<String, String>> selectAllById(@Param("id") String id);
}