package com.vdata.cloud.datacenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.vdata.cloud.admin.rpc.BaseUserController;
import com.vdata.cloud.common.annotion.BussinessLog;
import com.vdata.cloud.common.constant.Constants;
import com.vdata.cloud.common.vo.DataResult;
import com.vdata.cloud.datacenter.entity.DcsServerInfo;
import com.vdata.cloud.datacenter.mapper.DcsServerInfoMapper;
import com.vdata.cloud.datacenter.service.IDcsServerService;
import com.vdata.cloud.datacenter.util.GetPulverizerPointUtils;
import com.vdata.cloud.datacenter.util.ULogUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @ProjectName: sjzl-master
 * @Package: com.vdata.cloud.datacenter.controller
 * @ClassName: DcsController
 * @Author: HK
 * @Description:
 * @Date: 2021/7/21 14:19
 * @Version: 1.0
 */
@Log4j2
@Api(value = "dcs服务信息", tags = "dcs服务信息接口")
@Controller
@RestController
@RequestMapping("dcs")
public class DcsController extends BaseUserController {

    @Autowired
    private DcsServerInfoMapper dcsServerInfoMapper;

    @Autowired
    private IDcsServerService dcsServerService;


    @Autowired
    private ULogUtils uLogUtils;


    @Autowired
    private GetPulverizerPointUtils getPulverizerPointUtils;


    @Autowired
    private RedisTemplate redisTemplate;

    @BussinessLog(value = "修改dcs服务信息")
    @ApiOperation(value = "修改dcs服务信息")
    @PostMapping(value = "/update")
    public DataResult insert(@RequestBody DcsServerInfo dcsServerInfo) {
        DataResult result = new DataResult();
        try {
            dcsServerInfoMapper.update(dcsServerInfo);
       

            getPulverizerPointUtils.setIpAndPort(dcsServerInfo.getIp(), dcsServerInfo.getPort());
            result.setCode(Constants.RETURN_NORMAL);
            result.setMessage("修改dcs服务信息成功");
        } catch (Exception e) {
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage("修改dcs服务信息失败");
            log.error("修改dcs服务信息失败" + dcsServerInfo.toString(), e);
            uLogUtils.save("修改dcs服务信息", Thread.currentThread(), request, e);
        }
        return result;
    }

    @BussinessLog(value = "查询dcs服务信息")
    @ApiOperation(value = "查询dcs服务信息")
    @GetMapping(value = "/get")
    public DataResult get() {
        DataResult result = new DataResult();
        try {
            DcsServerInfo dcsServerInfo = dcsServerInfoMapper.selectOne(new LambdaQueryWrapper<>());
            result.setData(dcsServerInfo);
            result.setCode(Constants.RETURN_NORMAL);
            result.setMessage("查询dcs服务信息成功");
        } catch (Exception e) {
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage("查询dcs服务信息失败");
            log.error("查询dcs服务信息失败", e);
            uLogUtils.save("修改dcs服务信息", Thread.currentThread(), request, e);
        }
        return result;
    }

    @BussinessLog(value = "重启dcs服务")
    @ApiOperation(value = "重启dcs服务")
    @GetMapping(value = "/restart")
    public DataResult restart() {
        DataResult result = new DataResult();
        try {

            dcsServerService.restart();
            result.setCode(Constants.RETURN_NORMAL);
            result.setMessage("重启dcs服务成功");
        } catch (Exception e) {
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage("重启dcs服务失败");
            log.error("重启dcs服务失败", e);
            uLogUtils.save("重启dcs服务", Thread.currentThread(), request, e);
        }
        return result;
    }
}
