package com.vdata.cloud.admin.biz;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.vdata.cloud.admin.constant.AdminCommonConstant;
import com.vdata.cloud.admin.entity.BaseUserGroupRel;
import com.vdata.cloud.admin.entity.Group;
import com.vdata.cloud.admin.entity.Menu;
import com.vdata.cloud.admin.entity.ResourceAuthority;
import com.vdata.cloud.admin.mapper.*;
import com.vdata.cloud.admin.vo.AuthorityMenuTree;
import com.vdata.cloud.admin.vo.GroupUsers;
import com.vdata.cloud.admin.vo.GroupVO;
import com.vdata.cloud.common.exception.BusinessException;
import com.vdata.cloud.common.exception.CommonException;
import com.vdata.cloud.common.rest.BaseBiz;
import com.vdata.cloud.common.util.CommonUtil;
import com.vdata.cloud.common.util.UUIDUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

import static com.vdata.cloud.admin.biz.ElementBiz.ELEMENT_CACHE_NAME;
import static com.vdata.cloud.admin.biz.MenuBiz.MENU_CACHE_NAME;


@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class GroupBiz extends BaseBiz<GroupMapper, Group> {
    private final static String GROUP_CACHE_NAME = "permission:group";

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ResourceAuthorityMapper resourceAuthorityMapper;
    @Autowired
    private MenuMapper menuMapper;

    @Autowired
    private BaseUserGroupRelMapper baseUserGroupRelMapper;


    @Resource
    private GroupMapper mapper;


    public boolean updateById(Group entity) {
        if (AdminCommonConstant.ROOT_G.equals(entity.getParentId())) {
            entity.setPath("/" + entity.getCode());
        } else {
            LambdaQueryWrapper<Group> wrapper = new LambdaQueryWrapper();
            wrapper.eq(Group::getId, entity.getParentId());
            Group parent = mapper.selectOne(wrapper);
            entity.setPath(parent.getPath() + "/" + entity.getCode());
        }
        return super.updateById(entity);
    }

    /**
     * 获取群组关联用户
     *
     * @param groupId
     * @return
     */
    public GroupUsers getGroupUsers(String groupId) {
        return new GroupUsers(userMapper.selectMemberByGroupId(groupId), userMapper.selectLeaderByGroupId(groupId));
    }

    /**
     * 变更群主所分配用户
     *
     * @param groupId
     * @param members
     * @param leaders
     */
    //@CacheClear(pre = "permission")
    @CacheEvict(cacheNames = GROUP_CACHE_NAME, key = "groupId")
    public void modifyGroupUsers(String groupId, String members, String leaders) {
        mapper.deleteGroupLeadersById(groupId);
        mapper.deleteGroupMembersById(groupId);
        if (StringUtils.isNotBlank(members)) {
            String[] mem = members.split(",");
            for (String m : mem) {
                mapper.insertGroupMembersById(groupId, Integer.parseInt(m));
            }
        }
        if (StringUtils.isNotBlank(leaders)) {
            String[] mem = leaders.split(",");
            for (String m : mem) {
                mapper.insertGroupLeadersById(groupId, Integer.parseInt(m));
            }
        }
    }

    /**
     * 变更群组关联的菜单
     *
     * @param groupId
     * @param menus
     */
    //@CacheClear(keys = {"permission:menu", "permission:u"})
    @Caching(evict = {
            @CacheEvict(value = MENU_CACHE_NAME, key = "'all'"),
            @CacheEvict(value = "permission:u")
    })
    public void modifyAuthorityMenu(String groupId, String[] menus) {
        resourceAuthorityMapper.deleteByAuthorityIdAndResourceType(groupId, AdminCommonConstant.RESOURCE_TYPE_MENU);
        LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
        List<Menu> menuList = menuMapper.selectList(wrapper);
        Map<String, String> map = new HashMap<>();
        for (Menu menu : menuList) {
            map.put(menu.getId(), menu.getParentId());
        }
        Set<String> relationMenus = new HashSet<>();
        relationMenus.addAll(Arrays.asList(menus));
        ResourceAuthority authority = null;
        for (String menuId : menus) {
            findParentID(map, relationMenus, menuId);
        }
        for (String menuId : relationMenus) {
            authority = new ResourceAuthority(AdminCommonConstant.AUTHORITY_TYPE_GROUP, AdminCommonConstant.RESOURCE_TYPE_MENU);
            authority.setAuthorityId(groupId + "");
            authority.setResourceId(menuId);
            authority.setParentId("-1");
            resourceAuthorityMapper.insert(authority);
        }
    }

    private void findParentID(Map<String, String> map, Set<String> relationMenus, String id) {
        /*if (AdminCommonConstant.ROOT.equals(id)) {
            return;
        }*/
        if (StringUtils.equalsAny(id, "", AdminCommonConstant.ROOT)) {
            return;
        }
        String parentId = map.get(id);

        relationMenus.add(parentId);
        findParentID(map, relationMenus, parentId);
    }

    /**
     * 分配资源权限
     *
     * @param groupId
     * @param menuId
     * @param elementId
     */
    //@CacheClear(keys = {"permission:ele", "permission:u"})
    @Caching(evict = {
            @CacheEvict(value = ELEMENT_CACHE_NAME, key = "'all'"),
            @CacheEvict(value = "permission:u")
    })
    public void modifyAuthorityElement(String groupId, String menuId, int elementId) {
        ResourceAuthority authority = new ResourceAuthority(AdminCommonConstant.AUTHORITY_TYPE_GROUP, AdminCommonConstant.RESOURCE_TYPE_BTN);
        authority.setAuthorityId(groupId + "");
        authority.setResourceId(elementId + "");
        authority.setParentId("-1");
        resourceAuthorityMapper.insert(authority);
    }

    /**
     * 移除资源权限
     *
     * @param groupId
     * @param menuId
     * @param elementId
     */
    //@CacheClear(keys = {"permission:ele", "permission:u"})
    @Caching(evict = {
            @CacheEvict(value = ELEMENT_CACHE_NAME, key = "'all'"),
            @CacheEvict(value = "permission:u")
    })
    public void removeAuthorityElement(String groupId, String menuId, int elementId) {
        LambdaQueryWrapper<ResourceAuthority> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ResourceAuthority::getAuthorityId, groupId + "");
        wrapper.eq(ResourceAuthority::getResourceId, elementId + "");
        wrapper.eq(ResourceAuthority::getParentId, "-1");
        resourceAuthorityMapper.delete(wrapper);
    }


    /**
     * 获取群主关联的菜单
     *
     * @param groupId
     * @return
     */
    public List<AuthorityMenuTree> getAuthorityMenu(String groupId) {
        List<Menu> menus = menuMapper.selectMenuByAuthorityId(String.valueOf(groupId), AdminCommonConstant.AUTHORITY_TYPE_GROUP);
        List<AuthorityMenuTree> trees = new ArrayList<AuthorityMenuTree>();
        AuthorityMenuTree node = null;
        for (Menu menu : menus) {
            node = new AuthorityMenuTree();
            node.setText(menu.getTitle());
            BeanUtils.copyProperties(menu, node);
            trees.add(node);
        }
        return trees;
    }

    /**
     * 获取群组关联的资源
     *
     * @param groupId
     * @return
     */
    public List<Integer> getAuthorityElement(String groupId) {
        LambdaQueryWrapper<ResourceAuthority> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ResourceAuthority::getAuthorityId, groupId + "");
        wrapper.eq(ResourceAuthority::getAuthorityType, AdminCommonConstant.AUTHORITY_TYPE_GROUP);
        wrapper.eq(ResourceAuthority::getResourceType, AdminCommonConstant.RESOURCE_TYPE_BTN);

        List<ResourceAuthority> authorities = resourceAuthorityMapper.selectList(wrapper);
        List<Integer> ids = new ArrayList<Integer>();
        for (ResourceAuthority auth : authorities) {
            ids.add(Integer.parseInt(auth.getResourceId()));
        }
        return ids;
    }

    public void deleteGroup(String id) throws Exception {
        try {
            int count = baseUserGroupRelMapper.selectCount((new LambdaQueryWrapper<BaseUserGroupRel>().eq(BaseUserGroupRel::getFkBaseGroup, id)));
            if (count > 0) {
                throw new BusinessException("组群被使用，无法删除！");
            }
            mapper.deleteById(id);
        } catch (BusinessException e) {
            throw new BusinessException(e.getMessage());
        } catch (Exception e) {
            log.error("删除组群出错！,id:" + id, e);
            throw new CommonException(e);
        }

    }

    /**
     * 新增组群
     *
     * @param groupVO
     * @throws Exception
     */
    public void insertGroup(GroupVO groupVO) throws Exception {
        try {
            int count = mapper.selectCount(new LambdaQueryWrapper<Group>().eq(Group::getCode, groupVO.getCode()));
            if (count > 0) {
                throw new BusinessException("编码已存在，请重新输入");
            }
            Group group = new Group();
            BeanUtils.copyProperties(groupVO, group);
            group.setId(UUIDUtils.randomStr(32, false));
            mapper.insert(group);
        } catch (BusinessException e) {
            throw new BusinessException(e.getMessage());
        } catch (Exception e) {
            log.error("新增组群出错！,groupVO:" + groupVO, e);
            throw new CommonException(e);
        }
    }

    /**
     * 修改组群
     *
     * @param groupVO
     * @throws Exception
     */
    public void updateGroup(GroupVO groupVO) throws Exception {
        try {
            int count = mapper.selectCount(new LambdaQueryWrapper<Group>().eq(Group::getCode, groupVO.getCode()).ne(Group::getId, groupVO.getId()));
            if (count > 0) {
                throw new BusinessException("编码已存在，请重新输入");
            }
            Group group = new Group();
            BeanUtils.copyProperties(groupVO, group);
            mapper.updateById(group);
        } catch (BusinessException e) {
            throw new BusinessException(e.getMessage());
        } catch (Exception e) {
            log.error("新增组群出错！,groupVO:" + groupVO, e);
            throw new CommonException(e);
        }
    }


    public List<Map<String, Object>> getUserGroupInfo(Integer id) {
        return mapper.getUserGroupInfo(id);
    }


    public boolean updateUserGroup(Integer userId, String roleId, String orgId) {

        if (CommonUtil.isNotEmpty(roleId)) {
            BaseUserGroupRel baseUserGroupRel = new BaseUserGroupRel();
            baseUserGroupRel.setFkBaseGroup(roleId);
            baseUserGroupRel.setFkBaseUser(userId);
            baseUserGroupRel.setGroupType("1");


            LambdaQueryWrapper<BaseUserGroupRel> eq = new LambdaQueryWrapper<BaseUserGroupRel>()
                    .eq(BaseUserGroupRel::getFkBaseUser, userId)
                    .eq(BaseUserGroupRel::getGroupType, "1");
            if (baseUserGroupRelMapper.selectCount(eq) > 0) {
                int update = baseUserGroupRelMapper.update(baseUserGroupRel,
                        eq
                );
            } else {
                baseUserGroupRelMapper.insert(baseUserGroupRel);
            }


        }

        if (CommonUtil.isNotEmpty(orgId)) {
            BaseUserGroupRel baseUserGroupRel = new BaseUserGroupRel();
            baseUserGroupRel.setFkBaseGroup(orgId);
            baseUserGroupRel.setFkBaseUser(userId);
            baseUserGroupRel.setGroupType("4");
            LambdaQueryWrapper<BaseUserGroupRel> eq = new LambdaQueryWrapper<BaseUserGroupRel>()
                    .eq(BaseUserGroupRel::getFkBaseUser, userId)
                    .eq(BaseUserGroupRel::getGroupType, "4");
            if (baseUserGroupRelMapper.selectCount(eq) > 0) {
                int update = baseUserGroupRelMapper.update(baseUserGroupRel,
                        eq
                );
            } else {
                baseUserGroupRelMapper.insert(baseUserGroupRel);
            }
        }

        return true;
    }
}
