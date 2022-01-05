package com.vdata.cloud.datacenter.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.vdata.cloud.admin.rpc.BaseController;
import com.vdata.cloud.common.annotion.BussinessLog;
import com.vdata.cloud.common.constant.Constants;
import com.vdata.cloud.common.constant.LogConstants;
import com.vdata.cloud.common.vo.DataResult;
import com.vdata.cloud.datacenter.entity.SysOperationLog;
import com.vdata.cloud.datacenter.service.ISysOperationLogService;
import com.vdata.cloud.datacenter.service.impl.SysOperationLogServiceImpl;
import com.vdata.cloud.datacenter.util.ULogUtils;
import com.vdata.cloud.datacenter.vo.ULogGroupVO;
import com.vdata.cloud.datacenter.vo.ULogVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ProjectName: wru-master
 * @Package: com.vdata.cloud.datacenter.controller
 * @ClassName: SysOperationLogController
 * @Author: HK
 * @Description: 操作日志记录
 * @Date: 2020/11/4 10:54
 * @Version: 1.0
 */
@Log4j2
@Api(value = "操作日志记录服务接口", tags = "操作日志记录服务接口")
@Controller
@RestController
@RequestMapping("ulog")
public class SysOperationLogController extends BaseController<SysOperationLogServiceImpl, SysOperationLog> {
    @Autowired
    ISysOperationLogService sysOperationLogService;

    @Autowired
    private ULogUtils uLogUtils;

    @BussinessLog(value = "查询操作日志")
    @ApiOperation(value = "查询操作日志")
    @PostMapping(value = "/queryList")
    public DataResult queryList(@RequestBody ULogVO uLogVO) {
        DataResult result = new DataResult();
        try {

//            IPage<ULogGroupVO> data = sysOperationLogService.queryList1(uLogVO);
            IPage<ULogGroupVO> data = sysOperationLogService.queryListM(uLogVO);

            result.setData(data);
            result.setCode(Constants.RETURN_NORMAL);
            result.setMessage("查询操作日志成功");


        } catch (Exception e) {

            log.error(e);
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage("查询操作日志成功失败");
            uLogUtils.save("查询操作日志", Thread.currentThread(), request, e);
        }
        return result;
    }


    @ApiOperation(value = "下拉框")
    @GetMapping(value = "/comboBox")
    public DataResult comboBox() {
        DataResult result = new DataResult();
        try {


            Map<String, Object> map = new HashMap<>();
            map.put("logType", Arrays.asList(LogConstants.LOGIN_LOG, LogConstants.U_LOG, LogConstants.ERROR_LOG));
            map.put("result", Arrays.asList(LogConstants.SUCCESSED, LogConstants.FAIL));

            result.setData(map);
            result.setCode(Constants.RETURN_NORMAL);
            result.setMessage("获取下拉框成功");


        } catch (Exception e) {

            log.error(e);
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage("获取下拉框成功");
            uLogUtils.save("下拉框", Thread.currentThread(), request, e);
        }
        return result;
    }

    @ApiOperation(value = "二级下拉框")
    @GetMapping(value = "/twoComboBox")
    public DataResult twoComboBox(@RequestParam("logType") String logType) {
        DataResult result = new DataResult();
        try {

            List<String> list = sysOperationLogService.twoComboBoxM(logType);

            result.setData(list);
            result.setCode(Constants.RETURN_NORMAL);
            result.setMessage("获取二级下拉框成功");


        } catch (Exception e) {

            log.error(e);
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage("获取二级下拉框成功");
            uLogUtils.save("二级下拉框", Thread.currentThread(), request, e);

        }
        return result;
    }

}
