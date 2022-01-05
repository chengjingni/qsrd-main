package com.vdata.cloud.admin.biz;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vdata.cloud.admin.dto.UserDTO;
import com.vdata.cloud.admin.entity.BaseUserGroupRel;
import com.vdata.cloud.admin.entity.User;
import com.vdata.cloud.admin.mapper.BaseUserGroupRelMapper;
import com.vdata.cloud.admin.mapper.UserMapper;
import com.vdata.cloud.admin.vo.UserVo;
import com.vdata.cloud.common.context.BaseContextHandler;
import com.vdata.cloud.common.exception.BaseException;
import com.vdata.cloud.common.rest.BaseBiz;
import com.vdata.cloud.common.util.CommonUtil;
import com.vdata.cloud.common.util.MD5Utils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
@CacheConfig(cacheNames = "user:")
public class UserBiz extends BaseBiz<UserMapper, User> {

    @NonNull
    private UserMapper mapper;
    @NonNull
    private BaseUserGroupRelMapper relMapper;

    @NonNull
    private ObjectMapper objectMapper;


    @Caching(put = {
            @CachePut(key = "'['+#result.getId()+']'", unless = "#result==null", condition = "#result!=null")
    })
    public UserVo save(UserDTO userDTO) {

        User user = new User();
        BeanUtils.copyProperties(userDTO, user);

        user.setCreateUserId(BaseContextHandler.getUserID());
        String username = user.getUsername();
        int count = this.count(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (count > 0) {
            throw new BaseException("用户已经存在");
        }
        user.setPassword(MD5Utils.MD5(user.getPassword()));

        mapper.insert(user);

        /*新增部门以及角色*/
        BaseUserGroupRel groupRel = new BaseUserGroupRel();
        groupRel.setFkBaseUser(user.getId());


        if (user.getRole() != null) {
            groupRel.setFkBaseGroup(user.getRole().get("id"));
            groupRel.setGroupType(user.getRole().get("groupType"));
            relMapper.insert(groupRel);
            user.setRoleId(user.getRole().get("id"));
        }
        if (user.getOrg() != null) {
            groupRel.setFkBaseGroup(user.getOrg().get("id"));
            groupRel.setGroupType(user.getOrg().get("groupType"));
            relMapper.insert(groupRel);
            user.setOrgId(user.getOrg().get("id"));
        }
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(user, userVo);
        return userVo;

    }

    @Override
    public QueryWrapper<User> pageListSetWrapper(QueryWrapper<User> wrapper, Map<String, Object> params) {
        String name = CommonUtil.objToStr(params.getOrDefault("name", ""));
        if (StringUtils.isNotBlank(name)) {
            wrapper.like("username", name).or(or -> or.like("nickname", name));
        }

        wrapper.select(User.class, v -> !v.getColumn().contains("role_id") && !v.getColumn().contains("org_id") && !v.getColumn().contains("password"));
        wrapper.eq("status", 1);
        return wrapper;
    }

    /**
     * 根据用户名获取用户信息
     *
     * @param username
     * @return
     */
    @Cacheable(key = "#root.methodName+'_['+#username+']'", unless = "#result==null")
    public User getUserByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        return mapper.selectOne(wrapper);
    }

    public List<User> userList(IPage<User> page, Map<String, Object> params) {
        return mapper.userList(page, params);
    }

    @Cacheable(key = "'['+#id+']'", unless = "#result==null")
    public UserVo getUserById(String id) {

        List<Map<String, String>> list = mapper.selectAllById(id);

        UserVo userVo = null;
        for (Map<String, String> map : list) {
            try {
                String user = objectMapper.writeValueAsString(map);

                if (userVo == null) {
                    userVo = objectMapper.readValue(user, UserVo.class);
                }

                if (StringUtils.equals(map.get("code"), "role")) {
                    userVo.setRoleId(map.get("baseGroup"));
                } else if (StringUtils.equals(map.get("code"), "depart")) {
                    userVo.setOrgId(map.get("baseGroup"));
                }

            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

        }
        return userVo;
    }


    @Caching(
            put = {
                    @CachePut(key = "'['+#userDTO.getId()+']'", condition = "#result!=null"),
            },
            evict = {
                    @CacheEvict(key = "'getUserByUsername_['+#userDTO.getUsername()+']'", beforeInvocation = false)
            }
    )
    public UserVo update(UserDTO userDTO) {

        int id = userDTO.getId();
        int count = this.count(new LambdaQueryWrapper<User>().eq(User::getId, id));

        if (count < 1) {
            throw new BaseException("该用户不存在: " + id);
        }

        User user = new User();
        BeanUtils.copyProperties(userDTO, user);

        user.setUpdateUserId(BaseContextHandler.getUserID());

        if (StringUtils.isNotBlank(user.getPassword())) {
            user.setPassword(MD5Utils.MD5(user.getPassword()));
        } else {
            user.setPassword(null);
        }

        this.updateById(user);

        /*修改部门以及角色*/
        List<BaseUserGroupRel> baseUserGroupRelList = relMapper.selectList(new LambdaQueryWrapper<BaseUserGroupRel>().eq(BaseUserGroupRel::getFkBaseUser, user.getId()));

        if (baseUserGroupRelList.size() > 0) {
            relMapper.deleteBatchIds(baseUserGroupRelList.stream().map(b -> b.getId()).collect(Collectors.toList()));
        }

        Map<String, String> org = user.getOrg();
        Map<String, String> role = user.getRole();

        BaseUserGroupRel orgRel;
        BaseUserGroupRel roleRel;
        List<BaseUserGroupRel> list = new ArrayList<>();
        if (org != null) {
            orgRel = new BaseUserGroupRel();
            orgRel.setFkBaseUser(user.getId());
            orgRel.setFkBaseGroup(org.get("id"));
            orgRel.setGroupType(org.get("groupType"));
            list.add(orgRel);
            user.setOrgId(org.get("id"));
        }

        if (role != null) {
            roleRel = new BaseUserGroupRel();
            roleRel.setFkBaseUser(user.getId());
            roleRel.setFkBaseGroup(role.get("id"));
            roleRel.setGroupType(role.get("groupType"));
            list.add(roleRel);
            user.setRoleId(role.get("id"));
        }

        if (list.size() > 0) {
            relMapper.insertBatch(list);
        }
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(user, userVo);
        return userVo;
    }


    @Caching(
            evict = {
                    @CacheEvict(key = "'['+#entity.getId()+']'", condition = "#result"),
                    @CacheEvict(key = "'getUserByUsername_['+#entity.getUsername()+']'", beforeInvocation = false)
            }
    )
    public boolean updatepwd(User entity) {

        /*何康  2020-10-30  修改为MD5加密  start */
        //修改前
//        String password = new BCryptPasswordEncoder(UserConstant.PW_ENCORDER_SALT).encode(entity.getPassword());
        //修改后
//        String password = MD5Utils.MD5(entity.getPassword());
        /*何康  2020-10-30  修改为MD5加密  end */

        User user = new User();
        user.setId(entity.getId());
        user.setPassword(entity.getPassword());
        boolean updateById = this.updateById(user);
        return updateById;
    }

    /*删除用户*/

    @Caching(
            evict = {
                    @CacheEvict(key = "'['+#id+']'", condition = "#result==1", beforeInvocation = false),
            }
    )
    public int delete(Integer id) {

        //删除用户角色部门相关
        List<BaseUserGroupRel> groupRelList = relMapper.selectList(
                new LambdaQueryWrapper<BaseUserGroupRel>().eq(BaseUserGroupRel::getFkBaseUser, id).select(BaseUserGroupRel::getId));

        if (groupRelList.size() > 0) {
            List<Integer> list = groupRelList.stream().map(rel -> rel.getId()).collect(Collectors.toList());
            relMapper.deleteBatchIds(list);
        }

        int count = this.count(new LambdaQueryWrapper<User>().eq(User::getId, id));

        if (count < 1) {
            throw new BaseException("该用户不存在!");
        }

        User deleteUser = new User();
        deleteUser.setId(id);
        deleteUser.setUpdateUserId(BaseContextHandler.getUserID());
        deleteUser.setStatus(0);
        return mapper.updateById(deleteUser);
    }
}
