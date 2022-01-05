package com.vdata.cloud.common.rest;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vdata.cloud.common.util.CommonUtil;

import java.util.Map;
import java.util.stream.Collectors;

/**
 *  * @ProjectName:    base-project-structure
 *  * @Package:        com.vdata.cloud.common.rest
 *  * @ClassName:      BaseBiz
 *  * @Author:         Torry
 *  * @Description:    通用实现类
 *  * @Date:            2020/11/20 15:41
 *  * @Version:    1.0
 *  
 */
public class BaseBiz<M extends BaseMapper<Entity>, Entity> extends ServiceImpl {


    /**
     * 分页查询通用
     *
     * @param params
     * @return
     */
    public IPage<Entity> pageList(Map<String, Object> params) {
        IPage<Entity> page = pageListSetPage(params);
        QueryWrapper<Entity> wrapper = new QueryWrapper<>();
        pageListSetWrapper(wrapper, params);
        pageListSetSort(wrapper, params);
        page = page(page, wrapper);
        if (CommonUtil.isNotEmpty(page.getRecords())) {
            page.setRecords(page.getRecords().stream().map(v -> dataConvert(v)).collect(Collectors.toList()));
        }
        return page;
    }

    public IPage<Entity> pageListSetPage(Map<String, Object> params) {
        long current = CommonUtil.objToLong(CommonUtil.nvl(params.get("page"), "0"));
        long size = CommonUtil.objToLong(CommonUtil.nvl(params.get("limit"), "10"));
        return (IPage<Entity>) new Page(current, size);
    }

    public QueryWrapper<Entity> pageListSetWrapper(QueryWrapper<Entity> wrapper, Map<String, Object> params) {
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String key = entry.getKey();
            if ("page,limit,sort,order".indexOf(key) == -1) {
                Object value = entry.getValue();
                if (CommonUtil.isNotEmpty(value)) {
                    if (value instanceof Integer) {
                        wrapper.eq(CommonUtil.camelToUnderline(key), entry.getValue());
                    } else {
                        wrapper.like(CommonUtil.camelToUnderline(key), entry.getValue());
                    }
                }
            }
        }
        return wrapper;
    }

    public void pageListSetSort(QueryWrapper<Entity> wrapper, Map<String, Object> params) {
        String sortField = CommonUtil.objToStr(params.get("sort"));
        if (CommonUtil.isNotEmpty(sortField)) {
            sortField = CommonUtil.camelToUnderline(sortField);
            String order = CommonUtil.objToStr(params.get("order"));
            if ("desc".equals(order)) {
                //排序规则为降序
                wrapper.orderByDesc(sortField);
            } else {
                //排序规则为升序
                wrapper.orderByAsc(sortField);
            }
        }
    }

    public Entity dataConvert(Entity v) {
        return v;
    }

    public Object getOne(Object id) {
        QueryWrapper<Entity> wrapper = new QueryWrapper<>();
        wrapper.eq("ID", id);
        Object obj = super.getOne(wrapper, false);
        return obj;
    }


}
