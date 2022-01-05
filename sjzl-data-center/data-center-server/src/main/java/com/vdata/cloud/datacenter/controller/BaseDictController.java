package com.vdata.cloud.datacenter.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.vdata.cloud.admin.rpc.BaseUserController;
import com.vdata.cloud.common.annotion.BussinessLog;
import com.vdata.cloud.common.constant.Constants;
import com.vdata.cloud.common.exception.BusinessException;
import com.vdata.cloud.common.vo.DataResult;
import com.vdata.cloud.datacenter.entity.BaseDict;
import com.vdata.cloud.datacenter.service.IBaseDictService;
import com.vdata.cloud.datacenter.util.ULogUtils;
import com.vdata.cloud.datacenter.vo.BaseDictPageVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ProjectName: wru-master
 * @Package: com.vdata.cloud.datacenter.controller
 * @ClassName: SysOperationLogController
 * @Author: HK
 * @Description: 数据字典管理
 * @Date: 2020/11/4 10:54
 * @Version: 1.0
 */
@Log4j2
@Api(value = "数据字典管理", tags = "数据字典管理接口")
@Controller
@RestController
@RequestMapping("basedict")
public class BaseDictController extends BaseUserController {
    @Autowired
    private IBaseDictService baseDictService;

    @Autowired
    private ULogUtils uLogUtils;

/*    @ApiOperation(value = "添加数据字典")
    @PostMapping(value = "/insert")
    public DataResult insert(@RequestBody BaseDict baseDict) {
        DataResult result = new DataResult();
        try {
            baseDictService.insert(baseDict);
            result.setCode(Constants.RETURN_NORMAL);
            result.setMessage("添加数据字典成功");
        } catch (BusinessException e) {
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage(e.getMessage());
            log.error("添加数据字典失败" + baseDict.toString(), e);
        } catch (Exception e) {
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage("添加数据字典失败");
            log.error("添加数据字典失败" + baseDict.toString(), e);
        }
        return result;
    }*/

    @BussinessLog(value = "添加数据字典")
    @ApiOperation(value = "添加数据字典")
    @PostMapping(value = "/insert")
    public DataResult insert(@RequestBody List<BaseDict> baseDicts) {
        DataResult result = new DataResult();
        try {
            for (BaseDict baseDict : baseDicts) {
                baseDict.setCreateUser(userName());
                baseDict.setUpdateUser(userName());
            }

            baseDictService.insert(baseDicts);
            result.setCode(Constants.RETURN_NORMAL);
            result.setMessage("添加数据字典成功");
        } catch (BusinessException e) {
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage(e.getMessage());
            log.error("添加数据字典失败", e);
            uLogUtils.save("添加数据字典", Thread.currentThread(), request, e);
        } catch (Exception e) {
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage("添加数据字典失败");
            log.error("添加数据字典失败", e);
            uLogUtils.save("添加数据字典", Thread.currentThread(), request, e);
        }
        return result;
    }

    @BussinessLog(value = "删除数据字典")
    @ApiOperation(value = "删除数据字典")
    @GetMapping(value = "/delete")
    public DataResult delete(Integer id) {
        DataResult result = new DataResult();
        try {
            baseDictService.delete(id);
            result.setCode(Constants.RETURN_NORMAL);
            result.setMessage("删除数据字典成功");
        } catch (BusinessException e) {
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage(e.getMessage());
            log.error("删除数据字典失败", e);
            uLogUtils.save("删除数据字典", Thread.currentThread(), request, e);
        } catch (Exception e) {
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage("删除数据字典失败");
            log.error("删除数据字典失败", e);
            uLogUtils.save("删除数据字典", Thread.currentThread(), request, e);
        }
        return result;
    }

    @BussinessLog(value = "查询数据字典")
    @ApiOperation(value = "查询数据字典")
    @GetMapping(value = "/list")
    public DataResult list(String type) {
        DataResult result = new DataResult();
        try {
            List<BaseDict> baseDictList = baseDictService.list(type);

            result.setData(baseDictList);
            result.setCode(Constants.RETURN_NORMAL);
            result.setMessage("查询数据字典成功" + nickName());
        } catch (Exception e) {
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage("查询数据字典失败");
            log.error("查询数据字典失败", e);
            uLogUtils.save("查询数据字典", Thread.currentThread(), request, e);
        }
        return result;
    }

    @BussinessLog(value = "查询数据字典(分页)")
    @ApiOperation(value = "查询数据字典(分页)")
    @PostMapping(value = "/page")
    public DataResult page(@RequestBody BaseDictPageVO baseDictPageVO) {
        DataResult result = new DataResult();
        try {
            IPage<BaseDict> page = baseDictService.page(baseDictPageVO);
            result.setData(page);
            result.setCode(Constants.RETURN_NORMAL);
            result.setMessage("查询数据字典成功");
        } catch (Exception e) {
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage("查询数据字典失败");
            log.error("查询数据字典失败", e);
            uLogUtils.save("查询数据字典(分页)", Thread.currentThread(), request, e);
        }
        return result;
    }


  /*  @ApiOperation(value = "修改数据字典")
    @PostMapping(value = "/update")
    public DataResult update(@RequestBody BaseDict baseDict) {
        DataResult result = new DataResult();
        try {
            baseDictService.update(baseDict);
            result.setCode(Constants.RETURN_NORMAL);
            result.setMessage("修改数据字典成功");
        } catch (BusinessException e) {
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage(e.getMessage());
            log.error("修改数据字典失败", e);
        } catch (Exception e) {
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage("修改数据字典失败");
            log.error("修改数据字典失败", e);
        }
        return result;
    }*/

    @BussinessLog(value = "修改数据字典")
    @ApiOperation(value = "修改数据字典")
    @PostMapping(value = "/update")
    public DataResult update(@RequestBody List<BaseDict> baseDicts) {
        DataResult result = new DataResult();
        try {
            for (BaseDict baseDict : baseDicts) {
                baseDict.setCreateUser(userName());
                baseDict.setUpdateUser(userName());
            }
            baseDictService.update(baseDicts);
            result.setCode(Constants.RETURN_NORMAL);
            result.setMessage("修改数据字典成功");
        } catch (BusinessException e) {
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage(e.getMessage());
            log.error("修改数据字典失败", e);
            uLogUtils.save("修改数据字典", Thread.currentThread(), request, e);
        } catch (Exception e) {
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage("修改数据字典失败");
            log.error("修改数据字典失败", e);
            uLogUtils.save("修改数据字典", Thread.currentThread(), request, e);
        }
        return result;
    }

    @BussinessLog(value = "是否存在当前代码")
    @ApiOperation(value = "是否存在当前代码")
    @PostMapping(value = "/exists")
    public DataResult exists(@RequestBody BaseDict baseDict) {
        DataResult result = new DataResult();
        try {
            baseDictService.exists(baseDict);
            result.setCode(Constants.RETURN_NORMAL);
            result.setMessage("不存在");
        } catch (BusinessException e) {
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage(e.getMessage());
            log.error("当前字典代码或当前字典名称已经存在", e);
            uLogUtils.save("是否存在当前代码", Thread.currentThread(), request, e);
        } catch (Exception e) {
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage(e.getMessage());
            log.error("当前字典代码或当前字典名称已经存在", e);
            uLogUtils.save("是否存在当前代码", Thread.currentThread(), request, e);

        }
        return result;
    }

}
