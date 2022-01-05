package com.vdata.cloud.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vdata.cloud.admin.entity.Element;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ElementMapper extends BaseMapper<Element> {
    List<Element> selectAuthorityElementByUserId(@Param("userId") int userId);

    List<Element> selectAuthorityMenuElementByUserId(@Param("userId") int userId, @Param("menuId") String menuId);

    List<Element> selectAuthorityElementByClientId(@Param("clientId") String clientId);

    List<Element> selectAllElementPermissions();
}