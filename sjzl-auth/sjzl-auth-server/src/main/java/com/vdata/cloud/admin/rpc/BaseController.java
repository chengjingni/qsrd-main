package com.vdata.cloud.admin.rpc;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vdata.cloud.admin.rpc.service.PermissionService;
import com.vdata.cloud.auth.common.util.jwt.IJWTInfo;
import com.vdata.cloud.client.jwt.UserAuthUtil;
import com.vdata.cloud.common.constant.Constants;
import com.vdata.cloud.common.context.BaseContextHandler;
import com.vdata.cloud.common.msg.ObjectRestResponse;
import com.vdata.cloud.common.msg.TableResultResponse;
import com.vdata.cloud.common.util.CommonUtil;
import com.vdata.cloud.common.util.EntityUtils;
import com.vdata.cloud.common.vo.DataResult;
import com.vdata.cloud.common.vo.UserVO;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Slf4j
public class BaseController<Service extends ServiceImpl, Entity> {
    @Autowired
    protected HttpServletRequest request;
    @Autowired
    protected Service baseBiz;
/*
    @Autowired
    private IUserService userService;
*/


    @Autowired
    private UserAuthUtil userAuthUtil;

    @Autowired
    private PermissionService permissionService;


    public UserVO getUserByToken(String token) throws Exception {
        IJWTInfo ijwtInfo = userAuthUtil.getInfoFromToken(token);
        return new UserVO(ijwtInfo.getUniqueName(), ijwtInfo.getId(), ijwtInfo.getName());
    }


    @ApiOperation(value = "新增接口（标准）")
    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseBody
    public ObjectRestResponse<Entity> add(@RequestBody @Validated Entity entity) {
        ObjectRestResponse<Entity> entityObjectRestResponse = new ObjectRestResponse<>();
        try {
            setUser();
            EntityUtils.setCreatAndUpdatInfo(entity);
            baseBiz.save(entity);
            entityObjectRestResponse.setCode(Constants.RETURN_NORMAL);
            entityObjectRestResponse.setMessage("新增成功");
        } catch (Exception e) {
            entityObjectRestResponse.setCode(Constants.RETURN_UNNORMAL);
            entityObjectRestResponse.setMessage("新增失败");
            log.error("新增失败", e);
        }

        return entityObjectRestResponse;
    }

    @ApiOperation(value = "根据id查询接口（标准）")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ObjectRestResponse<Entity> get(@PathVariable Object id) {
        ObjectRestResponse<Entity> entityObjectRestResponse = new ObjectRestResponse<>();
        try {

            QueryWrapper<Entity> wrapper = new QueryWrapper<>();
            wrapper.eq("ID", id);
            Object o = baseBiz.getOne(wrapper);
            entityObjectRestResponse.data((Entity) o);
            entityObjectRestResponse.setCode(Constants.RETURN_NORMAL);
            entityObjectRestResponse.setMessage("明细查询成功！");
        } catch (Exception e) {
            log.error("明细查询出错！", e);
            entityObjectRestResponse.setCode(Constants.RETURN_UNNORMAL);
            entityObjectRestResponse.setMessage("明细查询出错!");
        }
        return entityObjectRestResponse;
    }

    @ApiOperation(value = "修改", hidden = true)
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public ObjectRestResponse<Entity> update(@RequestBody Entity entity) {
        ObjectRestResponse<Entity> entityObjectRestResponse = new ObjectRestResponse<>();
        try {
            setUser();
            EntityUtils.setUpdatedInfo(entity);
            baseBiz.updateById(entity);
            entityObjectRestResponse.setCode(Constants.RETURN_NORMAL);
            entityObjectRestResponse.setMessage("更新成功!");
        } catch (Exception e) {
            log.error("更新出错！", e);
            entityObjectRestResponse.setCode(Constants.RETURN_UNNORMAL);
            entityObjectRestResponse.setMessage("更新出错!");
        }

        return entityObjectRestResponse;
    }

    @ApiOperation(value = "根据id修改接口（标准）")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public ObjectRestResponse<Entity> updated(@RequestBody Entity entity) {
        ObjectRestResponse<Entity> entityObjectRestResponse = new ObjectRestResponse<>();
        try {
            EntityUtils.setUpdatedInfo(entity);
            baseBiz.updateById(entity);
            entityObjectRestResponse.setCode(Constants.RETURN_NORMAL);
            entityObjectRestResponse.setMessage("更新成功!");
        } catch (Exception e) {
            log.error("更新出错！", e);
            entityObjectRestResponse.setCode(Constants.RETURN_UNNORMAL);
            entityObjectRestResponse.setMessage("更新出错!");
        }
        return entityObjectRestResponse;
    }

    @ApiOperation(value = "根据id删除接口（标准）")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public ObjectRestResponse<Entity> remove(@PathVariable Object id) {
        try {
            QueryWrapper<Entity> wrapper = new QueryWrapper<>();
            wrapper.eq("ID", id);
            baseBiz.remove(wrapper);
            return new ObjectRestResponse<Entity>(Constants.RETURN_NORMAL, "删除成功!");
        } catch (Exception e) {
            log.error("列表查询出错！", e);
            return new ObjectRestResponse(Constants.RETURN_UNNORMAL, "删除出错!");
        }
    }

    @ApiOperation(value = "查询所有数据接口", hidden = true)
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    @ResponseBody
    public List<Entity> all() {

        LambdaQueryWrapper<Entity> wrapper = new LambdaQueryWrapper();

        return baseBiz.list(wrapper);
    }


    @ApiOperation(value = "查询所有数据接口", hidden = true)
    @RequestMapping(value = "/newAll", method = RequestMethod.GET)
    @ResponseBody
    public DataResult<Entity> newAll() {
        DataResult result = new DataResult();
        try {
            LambdaQueryWrapper<Entity> wrapper = new LambdaQueryWrapper();

            List list = baseBiz.list(wrapper);
            result.setData(list);
            result.setCode(Constants.RETURN_NORMAL);
            result.setMessage("判断代码是否存在");
        } catch (Exception e) {
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage("判断代码是否存在");
            log.error("判断代码是否存在", e);
        }

        return result;
    }

    @ApiOperation(value = "查询所有数据接口（标准）")
    @RequestMapping(value = "/listAll", method = RequestMethod.GET)
    @ResponseBody
    public DataResult<Entity> listAll() {
        DataResult result = new DataResult();
        try {
            QueryWrapper<Entity> wrapper = new QueryWrapper();
            wrapper.eq("del_lbl", 0);
            List list = baseBiz.list(wrapper);
            result.setCode(Constants.RETURN_NORMAL);
            result.setMessage("代码标准版本删除成功");
            result.setData(list);
        } catch (Exception e) {
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage("代码标准版本删除失败");
        }
        return result;
    }


    @ApiOperation(value = "分页查询接口", hidden = true)
    @RequestMapping(value = "/page", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public TableResultResponse<Entity> list(@RequestParam Map<String, Object> params) {
        try {
            //查询列表数据
            long current = CommonUtil.objToLong(CommonUtil.nvl(params.get("page"), "1"));
            long size = CommonUtil.objToLong(CommonUtil.nvl(params.get("limit"), "10"));
            IPage<Entity> page = new Page(current, size);
            QueryWrapper<Entity> wrapper = new QueryWrapper<>();
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                String key = entry.getKey();
                if ("page,limit,sort,order".indexOf(key) == -1) {
                    wrapper.like(CommonUtil.camelToUnderline(key), entry.getValue());
                }
            }

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

            page = baseBiz.page(page, wrapper);
            return new TableResultResponse<Entity>(page.getTotal(), page.getRecords())
                    .setCode(Constants.RETURN_NORMAL)
                    .setMessages("分页查询成功");
        } catch (Exception e) {
            log.error("列表查询出错！", e);
            return new TableResultResponse(Constants.RETURN_UNNORMAL, "列表查询出错!");
        }
    }

    @ApiOperation(value = "分页查询接口(标准)")
    /*何康 2020-09-11  添加公用方法 开始*/
    @RequestMapping(value = "/pageList", method = RequestMethod.POST)
    @ApiImplicitParam(name = "params", defaultValue = "{\n" +
            "    \"page\": 1,\n" +
            "    \"limit\": 10\n" +
            "}", value = "page代表页数，limit代表条数,可以填写其他字段(驼峰形式)进行匹配" + "例子:\t" + "{\n" +
            "    \"page\": \"1\",\n" +
            "    \"limit\": \"10\"\n" +
            "}", paramType = "body")
    @ResponseBody
    public TableResultResponse<Entity> pageList(@RequestBody Map<String, Object> params) {

        try {
            //查询列表数据
            long current = CommonUtil.objToLong(CommonUtil.nvl(params.get("page") + "", "0"));
            long size = CommonUtil.objToLong(CommonUtil.nvl(params.get("limit") + "", "0"));
            IPage<Entity> page = new Page(current, size);
            QueryWrapper<Entity> wrapper = new QueryWrapper<>();
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

            page = baseBiz.page(page, wrapper);

            TableResultResponse<Entity> entityTableResultResponse = new TableResultResponse<>(page.getTotal(), page.getRecords());
            entityTableResultResponse.setCode(Constants.RETURN_NORMAL);
            //entityTableResultResponse.setMessage("代码标准版本删除成功");
            return entityTableResultResponse;
        } catch (Exception e) {
            log.error("列表查询出错！", e);
            return new TableResultResponse(Constants.RETURN_UNNORMAL, "列表查询出错!");
        }
    }
    /*何康 2020-09-11  添加公用方法 结束*/

    public String userName() {
        setUser();
        return BaseContextHandler.getUsername();
    }

    private void setUser() {
        try {
            if (CommonUtil.isEmpty(BaseContextHandler.getUserID()) || CommonUtil.isEmpty(BaseContextHandler.getUsername())) {
                Cookie[] cookies = request.getCookies();
                if (CommonUtil.isNotEmpty(cookies)) {
                    Optional<Cookie> first = Arrays.stream(cookies).filter(cookie -> "Admin-Token".equals(cookie.getName())).findFirst();
                    if (first.isPresent()) {
                        String token = first.get().getValue();
                        UserVO user = getUserByToken(token);
                        BaseContextHandler.setUsername(user.getUsername());
                        BaseContextHandler.setUserID(user.getUserId());
                    }
                }
            }
        } catch (Exception e) {
            log.error("用户获取失败", e);
        }
    }

    public Integer userId() {
        setUser();
        String userId = BaseContextHandler.getUserID();
        if (CommonUtil.isNotEmpty(userId)) {
            return Integer.parseInt(userId);
        } else {
            return null;
        }
    }

}
