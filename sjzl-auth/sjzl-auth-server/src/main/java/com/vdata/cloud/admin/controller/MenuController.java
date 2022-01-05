package com.vdata.cloud.admin.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.vdata.cloud.admin.biz.MenuBiz;
import com.vdata.cloud.admin.biz.UserBiz;
import com.vdata.cloud.admin.constant.AdminCommonConstant;
import com.vdata.cloud.admin.entity.Menu;
import com.vdata.cloud.admin.rpc.BaseController;
import com.vdata.cloud.admin.vo.AuthorityMenuTree;
import com.vdata.cloud.admin.vo.MenuTree;
import com.vdata.cloud.common.constant.Constants;
import com.vdata.cloud.common.exception.BusinessException;
import com.vdata.cloud.common.msg.ObjectRestResponse;
import com.vdata.cloud.common.util.CommonUtil;
import com.vdata.cloud.common.util.TreeUtil;
import com.vdata.cloud.common.vo.DataResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * ${DESCRIPTION}
 *
 * @author wanghaobin
 * @create 2017-06-12 8:49
 */
@Api(value = "菜单", tags = "菜单相关接口")
@Slf4j
@Controller
@RequestMapping("menu")
public class MenuController extends BaseController<MenuBiz, Menu> {
    @Autowired
    private UserBiz userBiz;

    @Autowired
    private MenuBiz menuBiz;

    @ApiOperation(value = "新增接口")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    @Override
    public ObjectRestResponse<Menu> add(@RequestBody @Validated Menu entity) {
        if (CommonUtil.isEmpty(entity.getId())) {
            entity.setId(CommonUtil.randomStr(32, false));
        }
        return super.add(entity);
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public List<Menu> list(String title) {
        LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(title)) {
            wrapper.like(Menu::getTitle, title);
        }
        return menuBiz.list(wrapper);
    }

    @RequestMapping(value = "/tree", method = RequestMethod.GET)
    @ResponseBody
    public List<MenuTree> getTree(String title) {
        LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(title)) {
            wrapper.like(Menu::getTitle, title);
        }
        return getMenuTree(menuBiz.list(wrapper), AdminCommonConstant.ROOT);
    }


    @RequestMapping(value = "/newTree", method = RequestMethod.GET)
    @ResponseBody
    public DataResult<MenuTree> newTree(String title) {
        DataResult dataResult = new DataResult();

        try {
            LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
            if (StringUtils.isNotBlank(title)) {
                wrapper.like(Menu::getTitle, title);
            }
            List<MenuTree> tree = getMenuTree(menuBiz.list(wrapper), AdminCommonConstant.ROOT);
            dataResult.setData(tree);
            dataResult.setCode(Constants.RETURN_NORMAL);
            dataResult.setMessage("查询成功");
        } catch (Exception e) {
            dataResult.setCode(Constants.RETURN_NORMAL);
            dataResult.setMessage("查询失败");
        }
        return dataResult;
    }


    @RequestMapping(value = "/system", method = RequestMethod.GET)
    @ResponseBody
    public List<Menu> getSystem() {
        LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Menu::getParentId, AdminCommonConstant.ROOT);
        return menuBiz.list(wrapper);
    }

    @RequestMapping(value = "/menuTree", method = RequestMethod.GET)
    @ResponseBody
    public List<MenuTree> listMenu(String parentId) {
        try {
            if (parentId == null) {
                parentId = this.getSystem().get(0).getId();
            }
        } catch (Exception e) {
            return new ArrayList<MenuTree>();
        }
        //List<MenuTree> trees = new ArrayList<MenuTree>();
        MenuTree node = null;
        LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
        Menu parent = menuBiz.getParentMenu(parentId);
        wrapper.like(Menu::getPath, parent.getPath());
        wrapper.ne(Menu::getId, parent.getId());
        return getMenuTree(menuBiz.list(wrapper), parent.getId());
    }

    @RequestMapping(value = "/authorityTree", method = RequestMethod.GET)
    @ResponseBody
    public List<AuthorityMenuTree> listAuthorityMenu() {
        List<AuthorityMenuTree> trees = new ArrayList<AuthorityMenuTree>();
        AuthorityMenuTree node = null;
        for (Menu menu : menuBiz.selectListAll()) {
            node = new AuthorityMenuTree();
            node.setText(menu.getTitle());
            BeanUtils.copyProperties(menu, node);
            trees.add(node);
        }
        return TreeUtil.bulid(trees, AdminCommonConstant.ROOT);
    }

    @RequestMapping(value = "/user/authorityTree", method = RequestMethod.GET)
    @ResponseBody
    public List<MenuTree> listUserAuthorityMenu(String parentId) {
        int userId = userBiz.getUserByUsername(userName()).getId();
        try {
            if (parentId == null) {
                parentId = this.getSystem().get(0).getId();
            }
        } catch (Exception e) {
            return new ArrayList<MenuTree>();
        }
        return getMenuTree(menuBiz.getUserAuthorityMenuByUserId(userId), parentId);
    }

    @RequestMapping(value = "/user/system", method = RequestMethod.GET)
    @ResponseBody
    public List<Menu> listUserAuthoritySystem() {
        int userId = userBiz.getUserByUsername(userName()).getId();
        return menuBiz.getUserAuthoritySystemByUserId(userId);
    }

    private List<MenuTree> getMenuTree(List<Menu> menus, String root) {
        List<MenuTree> trees = new ArrayList<MenuTree>();
        MenuTree node = null;
        for (Menu menu : menus) {
            node = new MenuTree();
            BeanUtils.copyProperties(menu, node);
            node.setLabel(menu.getTitle());
            trees.add(node);
        }
        return TreeUtil.bulid(trees, root);
    }

    @ApiOperation(value = "根据id删除接口")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    @ResponseBody
    public DataResult<Menu> deleteMenu(@PathVariable String id) {
        DataResult result = new DataResult();
        try {
            menuBiz.deleteMenu(id);
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


}
