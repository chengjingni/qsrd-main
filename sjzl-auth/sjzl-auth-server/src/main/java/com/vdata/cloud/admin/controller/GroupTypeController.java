package com.vdata.cloud.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.vdata.cloud.admin.biz.GroupBiz;
import com.vdata.cloud.admin.biz.GroupTypeBiz;
import com.vdata.cloud.admin.entity.Group;
import com.vdata.cloud.admin.entity.GroupType;
import com.vdata.cloud.admin.rpc.BaseController;
import com.vdata.cloud.common.constant.Constants;
import com.vdata.cloud.common.exception.BusinessException;
import com.vdata.cloud.common.msg.ObjectRestResponse;
import com.vdata.cloud.common.vo.DataResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "组", tags = "部门角色类型相关接口")
@Slf4j
@Controller
@RequestMapping("groupType")
public class GroupTypeController extends BaseController<GroupTypeBiz, GroupType> {

    @Autowired
    private GroupBiz groupBiz;

    @ApiOperation(value = "根据id删除")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public ObjectRestResponse remove(@PathVariable Object id) {
        try {
            int count = groupBiz.count((new LambdaQueryWrapper<Group>().eq(Group::getGroupType, id)));
            if (count > 0) {
                throw new BusinessException("存在子集，无法删除！");
            }
            QueryWrapper<GroupType> wrapper = new QueryWrapper<>();
            wrapper.eq("ID", id);
            baseBiz.remove(wrapper);
            return new ObjectRestResponse<>(Constants.RETURN_NORMAL, "删除成功!");
        } catch (BusinessException e) {
            return new ObjectRestResponse<>(Constants.RETURN_UNNORMAL, e.getMessage());
        } catch (Exception e) {
            return new ObjectRestResponse<>(Constants.RETURN_UNNORMAL, "删除失败!");
        }
    }

    @ApiOperation(value = "组类型列表", notes = "组类型列表")
    @ApiImplicitParam(name = "type", value = "", required = false, dataType = "String")
    @GetMapping(value = "/getGroupTypeList")
    @ResponseBody
    public DataResult getGroupTypeList(@RequestParam String type) {
        DataResult result = new DataResult();
        try {
            QueryWrapper<GroupType> wrapper = new QueryWrapper<>();
            if ("0".equals(type)) {
                //查询系统机构
                wrapper.eq("ID", 4);
            } else if ("1".equals(type)) {
                //查询角色
                wrapper.eq("ID", 1);
            }
            List<GroupType> resList = baseBiz.list(wrapper);
            result.setData(resList);
            result.setCode(Constants.RETURN_NORMAL);
            result.setMessage("列表查询成功");
        } catch (Exception e) {
            log.error("列表查询失败", e);
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage("列表查询失败");
        }
        return result;
    }


}
