package com.vdata.cloud.admin.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.vdata.cloud.admin.biz.MenuBiz;
import com.vdata.cloud.admin.biz.UserBiz;
import com.vdata.cloud.admin.dto.UserDTO;
import com.vdata.cloud.admin.entity.Menu;
import com.vdata.cloud.admin.entity.User;
import com.vdata.cloud.admin.rpc.service.PermissionService;
import com.vdata.cloud.admin.validator.groups.UserAddGroup;
import com.vdata.cloud.admin.validator.groups.UserUpdateGroup;
import com.vdata.cloud.admin.vo.FrontUser;
import com.vdata.cloud.admin.vo.MenuTree;
import com.vdata.cloud.admin.vo.UserVo;
import com.vdata.cloud.common.constant.Constants;
import com.vdata.cloud.common.util.CommonUtil;
import com.vdata.cloud.common.util.MD5Utils;
import com.vdata.cloud.common.vo.DataResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Api(value = "用户", tags = "用户相关接口")
@Log4j2
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    @NonNull
    private PermissionService permissionService;

    @NonNull
    private MenuBiz menuBiz;

    @NonNull
    private UserBiz userBiz;


    @ApiOperation(value = "新增接口")
    @PostMapping(value = "/save")
    public DataResult add(@RequestBody @Validated(UserAddGroup.class) UserDTO userDTO) {

        DataResult dataResult = new DataResult<>();

        try {
            userBiz.save(userDTO);
            dataResult.setCode(Constants.RETURN_NORMAL);
            dataResult.setMessage("新增成功");
        } catch (Exception e) {
            dataResult.setCode(Constants.RETURN_UNNORMAL);
            dataResult.setMessage(StringUtils.isBlank(e.getMessage()) ? "新增失败" : e.getMessage());
            log.error("新增失败", e);
        }
        return dataResult;
    }

    @ApiOperation(value = "修改接口")
    @PostMapping("/update")
    public DataResult updateUser(@RequestBody @Validated(UserUpdateGroup.class) UserDTO userDTO) {

        DataResult result = new DataResult();
        try {
            userBiz.update(userDTO);
            result.setCode(Constants.RETURN_NORMAL);
            result.setMessage("用户修改成功");
        } catch (Exception e) {
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage(StringUtils.isBlank(e.getMessage()) ? "修改失败" : e.getMessage());
            log.error("修改失败", e);
        }
        return result;
    }

    @ApiOperation(value = "修改密码接口")
    @PostMapping("/updatepwd")
    public DataResult updatepwd(@RequestBody User entity) {
        DataResult result = new DataResult();
        try {
            entity.setPassword(MD5Utils.MD5(entity.getPassword()));
            userBiz.updatepwd(entity);
            result.setCode(Constants.RETURN_NORMAL);
            result.setMessage("修改密码成功");
        } catch (Exception e) {
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage(StringUtils.isBlank(e.getMessage()) ? "修改密码失败" : e.getMessage());
            log.error("修改密码失败", e);
        }
        return result;
    }

    /**
     * 删除用户
     *
     * @param id
     * @return
     */
    @PostMapping("/delete/{id}")
    public DataResult deleteUser(@PathVariable("id") Integer id) {

        DataResult result = new DataResult();
        try {
            userBiz.delete(id);
            result.setCode(Constants.RETURN_NORMAL);
            result.setMessage("用户删除成功");
        } catch (Exception e) {
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage(StringUtils.isBlank(e.getMessage()) ? "删除失败" : e.getMessage());
            log.error("删除失败", e);
        }
        return result;
    }


    @ApiOperation(value = "用户详情")
    @GetMapping(value = "/detail/{id}")
    public DataResult userPage(@PathVariable("id") String id) {
        DataResult result = new DataResult();
        try {
            UserVo userVo = userBiz.getUserById(id);
            result.setData(userVo);
            result.setCode(Constants.RETURN_NORMAL);
            result.setMessage("用户详情查询成功");
        } catch (Exception e) {
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage("用户详情查询失败");
            log.error("用户详情查询失败", e);
        }
        return result;
    }


    @GetMapping(value = "/front/info")
    public ResponseEntity<?> getUserInfo(String token) throws Exception {
        log.info("/front/info is go");
        FrontUser userInfo = permissionService.getUserInfo(token);
        if (userInfo == null) {
            return ResponseEntity.status(401).body(false);
        } else {
            return ResponseEntity.ok(userInfo);
        }
    }

    @GetMapping(value = "/front/menus")
    public List<MenuTree> getMenusByUsername(String token) throws Exception {
        return permissionService.getMenusByUsername(token);
    }

    @GetMapping(value = "/front/menu/all")
    public List<Menu> getAllMenus() throws Exception {
        return menuBiz.selectListAll();
    }


    @ApiOperation(value = "用户列表查询")
    @GetMapping(value = "/userPage")
    public DataResult userPage(@RequestParam Map<String, Object> params) {
        DataResult result = new DataResult();
        try {
            IPage page = userBiz.pageList(params);

            List<UserVo> userVoList = (List<UserVo>) page.getRecords().stream().map(u -> {
                UserVo vo = new UserVo();
                BeanUtils.copyProperties(u, vo);
                return vo;
            }).collect(Collectors.toList());
            page.setRecords(userVoList);

            result.setData(page);
            result.setCode(Constants.RETURN_NORMAL);
            result.setMessage("用户列表查询成功");
        } catch (Exception e) {
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage("用户列表查询失败");
            log.error("用户列表查询失败", e);
        }
        return result;
    }

    @ApiOperation(value = "消息列表", notes = "消息列表")
    @PostMapping(value = "/userList")
    @ApiImplicitParam(name = "params", defaultValue = "{\n" +
            "    \"page\": 1,\n" +
            "    \"limit\": 10\n" +
            "}", value = "page代表页数，limit代表条数,可以填写其他字段(驼峰形式)进行匹配" + "例子:\t" + "{\n" +
            "    \"page\": \"1\",\n" +
            "    \"limit\": \"10\",\n" +
            "}", paramType = "body")
    public DataResult userList(@RequestBody Map<String, Object> params) {
        DataResult result = new DataResult();
        try {
            long current = CommonUtil.nvl(params.get("page"), 0);
            long size = CommonUtil.nvl(params.get("limit"), 10);
            IPage<User> page = new Page(current, size);
            page.setRecords(userBiz.userList(page, params));
            result.setData(page);
            result.setCode(Constants.RETURN_NORMAL);
            result.setMessage("列表查询成功");
        } catch (Exception e) {
            log.error("列表查询失败", e);
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage("列表查询失败");
        }
        return result;
    }

    @GetMapping(value = "getUserByToken")
    public DataResult getUserByToken(@RequestParam("token") String token) throws Exception {
        DataResult result = new DataResult();
        try {
            FrontUser u = permissionService.getUserInfo(token);
            Map<String, Object> resMap = new HashMap<>();
            resMap.put("username", u.getUsername());
            resMap.put("password", u.getPassword());
            result.setData(resMap);
            result.setCode(Constants.RETURN_NORMAL);
            result.setMessage("查询成功!");
        } catch (Exception e) {
            log.error("token错误", e);
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage("token失效");
        }
        return result;
    }

}
