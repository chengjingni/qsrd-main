package com.vdata.cloud.admin.biz;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.vdata.cloud.admin.constant.AdminCommonConstant;
import com.vdata.cloud.admin.entity.Menu;
import com.vdata.cloud.admin.entity.ResourceAuthority;
import com.vdata.cloud.admin.mapper.MenuMapper;
import com.vdata.cloud.admin.mapper.ResourceAuthorityMapper;
import com.vdata.cloud.common.exception.BusinessException;
import com.vdata.cloud.common.exception.CommonException;
import com.vdata.cloud.common.rest.BaseBiz;
import com.vdata.cloud.common.util.CommonUtil;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;


@Service
@Transactional(rollbackFor = Exception.class)
public class MenuBiz extends BaseBiz<MenuMapper, Menu> {
    protected final static String MENU_CACHE_NAME = "permission:menu";

    @Resource
    private MenuMapper mapper;
    @Resource
    private ResourceAuthorityMapper resourceAuthorityMapper;

    //@Cache(key = "permission:menu")
//    @Cacheable(value = MENU_CACHE_NAME, key = "'all'")
    public List<Menu> selectListAll() {
        LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
        return super.list(wrapper);
    }

    //@CacheClear(keys = {"permission:menu", "permission"})
    @Caching(evict = {
            @CacheEvict(value = MENU_CACHE_NAME, key = "'all'"),
            @CacheEvict(value = MENU_CACHE_NAME, key = "{entity.id}")
    }
    )
    public void insertSelective(Menu entity) {
        if (AdminCommonConstant.ROOT.equals(entity.getParentId())) {
            entity.setPath("/" + entity.getCode());
        } else {
            Menu parent = getParentMenu(entity.getParentId());
            entity.setPath(parent.getPath() + "/" + entity.getCode());
        }
        if (CommonUtil.isEmpty(entity.getId())) {
            entity.setId(CommonUtil.randomStr(10, true));
        }
        mapper.insert(entity);
    }

    public Menu getParentMenu(String parentId) {
        LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Menu::getId, parentId);
        return mapper.selectOne(wrapper);
    }

    //@CacheClear(keys = {"permission:menu", "permission"})
    @Caching(evict = {
            @CacheEvict(value = MENU_CACHE_NAME, key = "'all'"),
            @CacheEvict(value = MENU_CACHE_NAME, key = "{entity.id}")
    }
    )
    public boolean updateById(Menu entity) {
        if (AdminCommonConstant.ROOT.equals(entity.getParentId())) {
            entity.setPath("/" + entity.getCode());
        } else {
            Menu parent = getParentMenu(entity.getParentId());
            entity.setPath(parent.getPath() + "/" + entity.getCode());
        }
        return super.updateById(entity);
    }

    //@CacheClear(keys = {"permission:menu", "permission"})
    @Caching(evict = {
            @CacheEvict(value = MENU_CACHE_NAME, key = "'all'"),
            @CacheEvict(value = MENU_CACHE_NAME, key = "{entity.id}")
    }
    )
    public void updateSelectiveById(Menu entity) {
        super.updateById(entity);
    }

    /**
     * 获取用户可以访问的菜单
     *
     * @param id
     * @return
     */
//    @Cache(key = "permission:menu:u{1}")
    public List<Menu> getUserAuthorityMenuByUserId(int id) {
        return mapper.selectAuthorityMenuByUserId(id);
    }

    /**
     * 根据用户获取可以访问的系统
     *
     * @param id
     * @return
     */
    public List<Menu> getUserAuthoritySystemByUserId(int id) {
        return mapper.selectAuthoritySystemByUserId(id);
    }

    public void deleteMenu(String id) throws Exception {
        try {
            int count = resourceAuthorityMapper.selectCount((new LambdaQueryWrapper<ResourceAuthority>().eq(ResourceAuthority::getResourceId, id)).eq(ResourceAuthority::getResourceType, "menu"));
            if (count > 0) {
                throw new BusinessException("菜单被使用，无法删除！");
            }
            mapper.deleteById(id);
        } catch (BusinessException e) {
            throw new BusinessException(e.getMessage());
        } catch (Exception e) {
            throw new CommonException(e);
        }

    }

}
