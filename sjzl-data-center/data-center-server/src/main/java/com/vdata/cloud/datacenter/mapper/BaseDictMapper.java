package com.vdata.cloud.datacenter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vdata.cloud.datacenter.entity.BaseDict;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @ProjectName: qsrd
 * @Package: com.vdata.cloud.datacenter.mapper
 * @ClassName: BaseDictMapper
 * @Author: HK
 * @Description:
 * @Date: 2021/7/19 15:06
 * @Version: 1.0
 */
public interface BaseDictMapper extends BaseMapper<BaseDict> {

    /**
     * 按照id获取子集数量
     *
     * @param id
     */
    long getSubcollectionCountById(Integer id);

    int existsCountIn(@Param("baseDicts") List<BaseDict> baseDicts);
}
