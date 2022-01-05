package com.vdata.cloud.datacenter.controller;

import com.vdata.cloud.admin.rpc.BaseUserController;
import com.vdata.cloud.common.annotion.BussinessLog;
import com.vdata.cloud.common.constant.Constants;
import com.vdata.cloud.common.exception.BusinessException;
import com.vdata.cloud.common.vo.DataResult;
import com.vdata.cloud.datacenter.service.IAlarmService;
import com.vdata.cloud.datacenter.util.ULogUtils;
import com.vdata.cloud.datacenter.vo.AlarmAnalyseQueryVO;
import com.vdata.cloud.datacenter.vo.RealTimeAlarmVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @ProjectName: sjzl-master
 * @Package: com.vdata.cloud.datacenter.controller
 * @ClassName: AlarmAnalyse
 * @Author: HK
 * @Description:
 * @Date: 2021/7/27 14:39
 * @Version: 1.0
 */
@Log4j2
@Api(value = "报警相关", tags = "报警相关接口")
@Controller
@RestController
@RequestMapping("alarmAnalyse")
public class AlarmAnalyseController extends BaseUserController {
    @Autowired
    private IAlarmService alarmService;

    @Autowired
    private ULogUtils uLogUtils;

    @BussinessLog(value = "报警趋势统计")
    @ApiOperation(value = "报警趋势统计")
    @PostMapping(value = "/trendOfStatistical")
    public DataResult trendOfStatistical(@RequestBody AlarmAnalyseQueryVO alarmAnalyseQueryVO) {
        DataResult result = new DataResult();
        try {

            Map<String, List<Integer>> resultMap = new LinkedHashMap<>();
            resultMap = alarmService.trendOfStatistical(alarmAnalyseQueryVO);
            result.setData(resultMap);
            result.setCode(Constants.RETURN_NORMAL);
            result.setMessage("报警趋势统计成功");
        } catch (BusinessException e) {
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage(e.getMessage());
            log.error("报警趋势统计失败" + alarmAnalyseQueryVO.toString(), e);
            uLogUtils.save("报警趋势统计", Thread.currentThread(), request, e);
        } catch (Exception e) {
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage("报警趋势统计失败");
            log.error("报警趋势统计失败" + alarmAnalyseQueryVO.toString(), e);
            uLogUtils.save("报警趋势统计", Thread.currentThread(), request, e);
        }
        return result;
    }


    @BussinessLog(value = "故障统计磨煤机")
    @ApiOperation(value = "故障统计磨煤机")
    @PostMapping(value = "/hisTrueFaultByPulverizer")
    public DataResult hisTrueFaultByPulverizer(@RequestBody AlarmAnalyseQueryVO alarmAnalyseQueryVO) {
        DataResult result = new DataResult();
        try {

            Map<String, Map<String, Integer>> resultMap = alarmService.hisTrueFaultByPulverizer(alarmAnalyseQueryVO);
            result.setData(resultMap);
            result.setCode(Constants.RETURN_NORMAL);
            result.setMessage("故障统计成功");
        } catch (BusinessException e) {
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage(e.getMessage());
            log.error("故障统计失败" + alarmAnalyseQueryVO.toString(), e);
            uLogUtils.save("故障统计", Thread.currentThread(), request, e);
        } catch (Exception e) {
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage("故障统计失败");
            log.error("故障统计失败" + alarmAnalyseQueryVO.toString(), e);
            uLogUtils.save("故障统计", Thread.currentThread(), request, e);
        }
        return result;
    }


    @BussinessLog(value = "故障统计")
    @ApiOperation(value = "故障统计磨煤机")
    @PostMapping(value = "/faultStatistics")
    public DataResult hisTrueFaultBy(@RequestBody AlarmAnalyseQueryVO alarmAnalyseQueryVO) {
        DataResult result = new DataResult();
        try {

            Map<String, Map<String, Integer>> resultMap = alarmService.faultStatistics(alarmAnalyseQueryVO);
            result.setData(resultMap);
            result.setCode(Constants.RETURN_NORMAL);
            result.setMessage("故障统计成功");
        } catch (BusinessException e) {
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage(e.getMessage());
            log.error("故障统计失败" + alarmAnalyseQueryVO.toString(), e);
            uLogUtils.save("故障统计", Thread.currentThread(), request, e);
        } catch (Exception e) {
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage("故障统计失败");
            log.error("故障统计失败" + alarmAnalyseQueryVO.toString(), e);
            uLogUtils.save("故障统计", Thread.currentThread(), request, e);
        }
        return result;
    }


    @BussinessLog(value = "实时报警")
    @ApiOperation(value = "实时报警")
    @GetMapping(value = "/realTimeAlarm")
    public DataResult realTimeAlarm() {
        DataResult result = new DataResult();
        try {
            List<RealTimeAlarmVO> realTimeAlarmVOS = alarmService.realTimeAlarm();
            result.setData(realTimeAlarmVOS);
            result.setCode(Constants.RETURN_NORMAL);
            result.setMessage("获取实时报警成功");
        } catch (BusinessException e) {
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage(e.getMessage());
            log.error("获取实时报警失败", e);
            uLogUtils.save("实时报警", Thread.currentThread(), request, e);
        } catch (Exception e) {
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage("获取实时报警失败");
            log.error("获取实时报警失败", e);
            uLogUtils.save("实时报警", Thread.currentThread(), request, e);
        }
        return result;
    }


    @BussinessLog(value = "燃煤机实时报警")
    @ApiOperation(value = "燃煤机实时报警")
    @GetMapping(value = "/pulverizerRealTimeAlarm")
    public DataResult pulverizerRealTimeAlarm(@RequestParam("pulverizerCode") String pulverizerCode) {
        DataResult result = new DataResult();
        try {
            List<Map<String, Object>> maps = alarmService.pulverizerRealTimeAlarm(pulverizerCode);
            result.setData(maps);
            result.setCode(Constants.RETURN_NORMAL);
            result.setMessage("获取燃煤机实时报警成功");
        } catch (BusinessException e) {
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage(e.getMessage());
            log.error("获取燃煤机实时报警成功", e);
            uLogUtils.save("获取燃煤机实时报警", Thread.currentThread(), request, e);
        } catch (Exception e) {
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage("获取燃煤机实时报警失败");
            log.error("获取燃煤机实时报警失败", e);
            uLogUtils.save("获取燃煤机实时报警", Thread.currentThread(), request, e);
        }
        return result;
    }


    @BussinessLog(value = "报警年份")
    @ApiOperation(value = "报警年份")
    @GetMapping(value = "/years")
    public DataResult years() {
        DataResult result = new DataResult();
        try {
            List<String> years = alarmService.years();
            result.setData(years);
            result.setCode(Constants.RETURN_NORMAL);
            result.setMessage("获取报警年份成功");
        } catch (BusinessException e) {
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage(e.getMessage());
            log.error("获取报警年份失败", e);
            uLogUtils.save("报警年份", Thread.currentThread(), request, e);
        } catch (Exception e) {
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage("获取报警年份失败");
            log.error("获取报警年份失败", e);
            uLogUtils.save("报警年份", Thread.currentThread(), request, e);
        }
        return result;
    }


}
