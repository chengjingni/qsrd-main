package com.vdata.cloud.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.vdata.cloud.admin.biz.GroupBiz;
import com.vdata.cloud.admin.constant.AdminCommonConstant;
import com.vdata.cloud.admin.entity.Group;
import com.vdata.cloud.admin.rpc.BaseController;
import com.vdata.cloud.admin.vo.AuthorityMenuTree;
import com.vdata.cloud.admin.vo.GroupTree;
import com.vdata.cloud.admin.vo.GroupUsers;
import com.vdata.cloud.admin.vo.GroupVO;
import com.vdata.cloud.common.constant.Constants;
import com.vdata.cloud.common.exception.BusinessException;
import com.vdata.cloud.common.msg.GroupRestResponse;
import com.vdata.cloud.common.msg.ObjectRestResponse;
import com.vdata.cloud.common.util.CommonUtil;
import com.vdata.cloud.common.util.TreeUtil;
import com.vdata.cloud.common.vo.DataResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Log4j2
@Controller
@RequestMapping("group")
@Api(value = "组", tags = "部门角色相关接口")
@RequiredArgsConstructor
public class GroupController extends BaseController<GroupBiz, Group> {

    @NonNull
    private GroupBiz groupBiz;

    @ApiOperation(value = "新增接口")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    @Override
    public ObjectRestResponse<Group> add(@RequestBody @Validated Group entity) {
        if (CommonUtil.isEmpty(entity.getId())) {
            entity.setId(CommonUtil.randomStr(10, true));
        }
        return super.add(entity);
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public List<Group> list(String name, String groupType) {
        if (StringUtils.isBlank(name) && StringUtils.isBlank(groupType)) {
            return new ArrayList<Group>();
        }
        LambdaQueryWrapper<Group> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Group::getName, name);
        wrapper.eq(Group::getGroupType, groupType);
        return groupBiz.list(wrapper);
    }


    @RequestMapping(value = "/{id}/user", method = RequestMethod.PUT)
    @ResponseBody
    public ObjectRestResponse modifiyUsers(@PathVariable String id, String members, String leaders) {
        try {
            groupBiz.modifyGroupUsers(id, members, leaders);
            return new GroupRestResponse(Constants.RETURN_NORMAL, "查询成功").rel(true);

        } catch (Exception e) {
            return new GroupRestResponse(Constants.RETURN_UNNORMAL, "查询失败").rel(true);
        }

    }

    @RequestMapping(value = "/{id}/user", method = RequestMethod.GET)
    @ResponseBody
    public ObjectRestResponse<GroupUsers> getUsers(@PathVariable String id) {
        try {
            return new GroupRestResponse<GroupUsers>(Constants.RETURN_NORMAL, "查询成功").rel(true).data(groupBiz.getGroupUsers(id));
        } catch (Exception e) {
            return new GroupRestResponse<GroupUsers>(Constants.RETURN_UNNORMAL, "查询失败").rel(true);
        }
    }

    @RequestMapping(value = "/{id}/authority/menu", method = RequestMethod.POST)
    @ResponseBody
    public ObjectRestResponse modifyMenuAuthority(@PathVariable String id, String menuTrees) {
        try {
            String[] menus = menuTrees.trim().split(",");
            groupBiz.modifyAuthorityMenu(id, menus);
            return new GroupRestResponse(Constants.RETURN_NORMAL, "修改成功").rel(true);
        } catch (Exception e) {
            return new GroupRestResponse(Constants.RETURN_UNNORMAL, "修改失败").rel(true);
        }
    }

    @RequestMapping(value = "/{id}/authority/menu", method = RequestMethod.GET)
    @ResponseBody
    public ObjectRestResponse<List<AuthorityMenuTree>> getMenuAuthority(@PathVariable String id) {
        try {
            return new GroupRestResponse(Constants.RETURN_NORMAL, "查询成功").rel(true).data(groupBiz.getAuthorityMenu(id));
        } catch (Exception e) {
            return new GroupRestResponse(Constants.RETURN_UNNORMAL, "查询失败").rel(true).data(groupBiz.getAuthorityMenu(id));

        }
    }

    @RequestMapping(value = "/{id}/authority/element/add", method = RequestMethod.POST)
    @ResponseBody
    public ObjectRestResponse addElementAuthority(@PathVariable String id, String menuId, int elementId) {
        try {
            groupBiz.modifyAuthorityElement(id, menuId, elementId);
            return new GroupRestResponse(Constants.RETURN_NORMAL, "查询成功").rel(true);
        } catch (Exception e) {
            return new GroupRestResponse(Constants.RETURN_UNNORMAL, "查询失败").rel(true);
        }
    }

    @RequestMapping(value = "/{id}/authority/element/remove", method = RequestMethod.POST)
    @ResponseBody
    public ObjectRestResponse removeElementAuthority(@PathVariable String id, String menuId, int elementId) {
        try {
            groupBiz.removeAuthorityElement(id, menuId, elementId);
            return new GroupRestResponse(Constants.RETURN_NORMAL, "查询成功").rel(true);
        } catch (Exception e) {
            return new GroupRestResponse(Constants.RETURN_UNNORMAL, "查询失败").rel(true);
        }
    }

    @RequestMapping(value = "/{id}/authority/element", method = RequestMethod.GET)
    @ResponseBody
    public ObjectRestResponse<List<Integer>> getElementAuthority(@PathVariable String id) {
        try {
            return new GroupRestResponse(Constants.RETURN_NORMAL, "查询成功").rel(true).data(groupBiz.getAuthorityElement(id));

        } catch (Exception e) {
            return new GroupRestResponse(Constants.RETURN_UNNORMAL, "查询失败").rel(true).data(groupBiz.getAuthorityElement(id));
        }
    }


    @RequestMapping(value = "/tree", method = RequestMethod.GET)
    @ResponseBody
    public List<GroupTree> tree(String name, String groupType, String type) {
        if (StringUtils.isBlank(name) && StringUtils.isBlank(groupType)) {
            return new ArrayList<GroupTree>();
        }
        LambdaQueryWrapper<Group> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(name)) {
            wrapper.like(Group::getName, name);
        }
        if (StringUtils.isNotBlank(groupType)) {
            wrapper.eq(Group::getGroupType, groupType);
        }
        if (StringUtils.isNotBlank(type)) {
            wrapper.eq(Group::getType, type);
        }
        List<GroupTree> tree = getTree(groupBiz.list(wrapper), AdminCommonConstant.ROOT_G);
        return tree;
    }

    @RequestMapping(value = "/newTree", method = RequestMethod.GET)
    @ResponseBody
    public DataResult<GroupTree> newTree(String name, String groupType) {
        DataResult dataResult = new DataResult();
        try {
            if (StringUtils.isBlank(name) && StringUtils.isBlank(groupType)) {
                dataResult.setData(new ArrayList<GroupTree>());
                dataResult.setCode(Constants.RETURN_NORMAL);
                dataResult.setMessage("查询成功");
            }
            LambdaQueryWrapper<Group> wrapper = new LambdaQueryWrapper<>();
            if (StringUtils.isNotBlank(name)) {
                wrapper.like(Group::getName, name);
            }
            if (StringUtils.isNotBlank(groupType)) {
                wrapper.eq(Group::getGroupType, groupType);
            }
            List<GroupTree> tree = getTree(groupBiz.list(wrapper), AdminCommonConstant.ROOT_G);
            dataResult.setData(tree);
            dataResult.setCode(Constants.RETURN_NORMAL);
            dataResult.setMessage("查询成功");
        } catch (Exception e) {
            dataResult.setCode(Constants.RETURN_NORMAL);
            dataResult.setMessage("查询失败");
        }
        return dataResult;
    }


    @ApiOperation(value = "根据id删除接口")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    @ResponseBody
    public DataResult<Group> deleteGroup(@PathVariable("id") String id) {
        DataResult result = new DataResult();
        try {
            groupBiz.deleteGroup(id);
            result.setCode(Constants.RETURN_NORMAL);
            result.setMessage("删除成功!");
        } catch (BusinessException e) {
            log.error(e.getMessage(), e);
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage(e.getMessage());
        } catch (Exception e) {
            log.error("删除失败！", e);
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage("删除失败");
        }
        return result;
    }


    private List<GroupTree> getTree(List<Group> groups, String root) {
        List<GroupTree> trees = new ArrayList<GroupTree>();
        GroupTree node = null;
        for (Group group : groups) {
            node = new GroupTree();
            node.setLabel(group.getName());
            node.setType(group.getType());
            BeanUtils.copyProperties(group, node);
            trees.add(node);
        }
        return TreeUtil.bulid(trees, root);
    }

    @ApiOperation(value = "新增群组")
    @ApiImplicitParam(name = "GroupVO", value = "群组", required = true, dataType = "GroupVO")
    @PostMapping(value = "/insertGroup")
    @ResponseBody
    public DataResult insertGroup(@Validated @RequestBody GroupVO groupVO) {
        DataResult result = new DataResult();
        try {
            groupBiz.insertGroup(groupVO);
            result.setCode(Constants.RETURN_NORMAL);
            result.setMessage("新增成功!");
        } catch (BusinessException e) {
            log.error(e.getMessage(), e);
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage(e.getMessage());
        } catch (Exception e) {
            log.error("新增失败！", e);
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage("新增失败");
        }
        return result;
    }

    @ApiOperation(value = "修改群组")
    @ApiImplicitParam(name = "GroupVO", value = "群组", required = true, dataType = "GroupVO")
    @PostMapping(value = "/updateGroup")
    @ResponseBody
    public DataResult updateGroup(@Validated @RequestBody GroupVO groupVO) {
        DataResult result = new DataResult();
        try {
            groupBiz.updateGroup(groupVO);
            result.setCode(Constants.RETURN_NORMAL);
            result.setMessage("修改成功!");
        } catch (BusinessException e) {
            log.error(e.getMessage(), e);
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage(e.getMessage());
        } catch (Exception e) {
            log.error("修改失败！", e);
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage("修改失败");
        }
        return result;
    }


    @ApiOperation(value = "获取用户组详情")
    @ApiImplicitParam(name = "id", value = "", required = true, dataType = "Integer")
    @GetMapping(value = "/getUserGroupInfo")
    @ResponseBody
    public DataResult getUserGroupInfo(@RequestParam("id") Integer id) {
        DataResult result = new DataResult();
        try {
            List<Map<String, Object>> list = groupBiz.getUserGroupInfo(id);
            result.setData(list);
            result.setCode(Constants.RETURN_NORMAL);
            result.setMessage("获取用户组详情成功");
        } catch (Exception e) {
            log.error("获取用户组详情失败", e);
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage("获取用户组详情失败");
        }
        return result;
    }


    @ApiOperation(value = "用户组修改")
    @ApiImplicitParam(name = "id", value = "", required = true, dataType = "Integer")
    @PostMapping(value = "/updateUserGroup")
    @ResponseBody
    public DataResult getUserGroupInfo(@RequestParam("userId") Integer userId,
                                       @RequestParam(value = "roleId", required = false) String roleId,
                                       @RequestParam(value = "orgId", required = false) String orgId) {
        DataResult result = new DataResult();
        try {
            groupBiz.updateUserGroup(userId, roleId, orgId);

            result.setCode(Constants.RETURN_NORMAL);
            result.setMessage("用户组修改成功");
        } catch (Exception e) {
            log.error("用户组修改失败", e);
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage("用户组修改失败");
        }
        return result;
    }

}
