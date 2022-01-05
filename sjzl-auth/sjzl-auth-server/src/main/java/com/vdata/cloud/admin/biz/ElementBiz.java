package com.vdata.cloud.admin.biz;

import com.vdata.cloud.admin.entity.Element;
import com.vdata.cloud.admin.mapper.ElementMapper;
import com.vdata.cloud.common.rest.BaseBiz;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class ElementBiz extends BaseBiz<ElementMapper, Element> {
    protected final static String ELEMENT_CACHE_NAME = "permission:menu";

    @Resource
    private ElementMapper mapper;

    //@Cache(key = "permission:ele:u{1}")
//    @Cacheable(value = ELEMENT_CACHE_NAME,key = "#userId")
    public List<Element> getAuthorityElementByUserId(int userId) {
        return mapper.selectAuthorityElementByUserId(userId);
    }

    public List<Element> getAuthorityElementByUserId(int userId, String menuId) {
        return mapper.selectAuthorityMenuElementByUserId(userId, menuId);
    }

    //@Cache(key = "permission:ele")
    @Cacheable(value = ELEMENT_CACHE_NAME, key = "'all'")
    public List<Element> getAllElementPermissions() {
        return mapper.selectAllElementPermissions();
    }

    //@CacheClear(keys = {"permission:ele", "permission"})
    @CachePut(value = ELEMENT_CACHE_NAME, key = "{entity.id}")
    public void insertSelective(Element entity) {
        mapper.insert(entity);
    }


    //@CacheClear(keys = {"permission:ele", "permission"})
    @Caching(evict = {
            @CacheEvict(value = ELEMENT_CACHE_NAME, key = "'all'"),
            @CacheEvict(value = ELEMENT_CACHE_NAME, key = "#{entity.id}")
    })
    public void updateSelectiveById(Element entity) {
        mapper.updateById(entity);
    }
}
