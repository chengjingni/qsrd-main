package com.vdata.cloud.datacenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.vdata.cloud.admin.rpc.BaseUserController;
import com.vdata.cloud.common.annotion.BussinessLog;
import com.vdata.cloud.common.constant.Constants;
import com.vdata.cloud.common.exception.BusinessException;
import com.vdata.cloud.common.vo.DataResult;
import com.vdata.cloud.datacenter.entity.AlarmInformation;
import com.vdata.cloud.datacenter.entity.AlarmPoint;
import com.vdata.cloud.datacenter.entity.OperationLog;
import com.vdata.cloud.datacenter.service.IAlarmPointService;
import com.vdata.cloud.datacenter.service.IAlarmService;
import com.vdata.cloud.datacenter.util.ULogUtils;
import com.vdata.cloud.datacenter.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ProjectName: sjzl-master
 * @Package: com.vdata.cloud.datacenter.controller
 * @ClassName: AlarmController
 * @Author: HK
 * @Description:
 * @Date: 2021/7/21 14:45
 * @Version: 1.0
 */
@Log4j2
@Api(value = "报警相关", tags = "报警相关接口")
@Controller
@RestController
@RequestMapping("alarm")
public class AlarmController extends BaseUserController {
    @Autowired
    private IAlarmService alarmService;

    @Autowired
    private ULogUtils uLogUtils;


    @Autowired
    private IAlarmPointService alarmPointService;


    @BussinessLog(value = "报警信息列表")
    @ApiOperation(value = "报警列表")
    @PostMapping(value = "/page")
    public DataResult<IPage<AlarmInformationVO>> page(@RequestBody AlarmPageVO alarmPageVO) {
        DataResult<IPage<AlarmInformationVO>> result = new DataResult<IPage<AlarmInformationVO>>();
        try {
            IPage<AlarmInformationVO> page = alarmService.page(alarmPageVO);
            result.setData(page);
            result.setCode(Constants.RETURN_NORMAL);
            result.setMessage("查询报警列表成功");
        } catch (BusinessException e) {
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage(e.getMessage());
            log.error("查询报警列表失败" + alarmPageVO.toString(), e);
            uLogUtils.save("报警信息列表", Thread.currentThread(), request, e);
        } catch (Exception e) {
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage("查询报警列表失败");
            log.error("查询报警列表失败" + alarmPageVO.toString(), e);
            uLogUtils.save("报警信息列表", Thread.currentThread(), request, e);
        }
        return result;
    }


  /*  @BussinessLog(value = "报警信息列表")
    @ApiOperation(value = "报警列表")
    @PostMapping(value = "/export")
    public DataResult export(@RequestBody AlarmPageVO alarmPageVO, HttpServletResponse response) {
        DataResult result = new DataResult();
        try {

            alarmService.export(alarmPageVO, response);
//            result.setData(page);
            result.setCode(Constants.RETURN_NORMAL);
            result.setMessage("导出成功");
        } catch (BusinessException e) {
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage(e.getMessage());
            log.error("导出失败" + alarmPageVO.toString(), e);
            uLogUtils.save("导出失败", Thread.currentThread(), request, e);
        } catch (Exception e) {
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage("导出失败");
            log.error("导出失败" + alarmPageVO.toString(), e);
            uLogUtils.save("报警信息列表", Thread.currentThread(), request, e);
        }
        return result;
    }*/

    @BussinessLog(value = "报警信息列表")
    @ApiOperation(value = "报警列表")
    @GetMapping(value = "/export")
    public DataResult exportGet(AlarmPageVO alarmPageVO, HttpServletResponse response) {
        DataResult result = new DataResult();
        try {

            alarmService.export(alarmPageVO, response);
//            result.setData(page);
            result.setCode(Constants.RETURN_NORMAL);
            result.setMessage("导出成功");
        } catch (BusinessException e) {
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage(e.getMessage());
            log.error("导出失败" + alarmPageVO.toString(), e);
            uLogUtils.save("导出失败", Thread.currentThread(), request, e);
        } catch (Exception e) {
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage("导出失败");
            log.error("导出失败" + alarmPageVO.toString(), e);
            uLogUtils.save("报警信息列表", Thread.currentThread(), request, e);
        }
        return result;
    }


    @BussinessLog(value = "报警统计")
    @ApiOperation(value = "报警统计")
    @GetMapping(value = "/count")
    public DataResult<Map<String, Long>> count() {
        DataResult<Map<String, Long>> result = new DataResult<>();
        try {
            Map<String, Long> count = alarmService.count();
            result.setData(count);
            result.setCode(Constants.RETURN_NORMAL);
            result.setMessage("报警统计成功");
        } catch (Exception e) {
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage("报警统计失败");
            log.error("报警统计失败", e);
            uLogUtils.save("报报警统计", Thread.currentThread(), request, e);
        }
        return result;
    }


    @BussinessLog(value = "报警次数详情列表")
    @ApiOperation(value = "报警次数详情")
    @PostMapping(value = "/countDetailList")
    public DataResult<IPage<AbnormalDetailVO>> countDetailList(@RequestBody AbnormalDetailPageVO abnormalDetailPageVO) {
        DataResult<IPage<AbnormalDetailVO>> result = new DataResult<IPage<AbnormalDetailVO>>();
        try {
            IPage<AbnormalDetailVO> page = alarmService.countDetailList(abnormalDetailPageVO);
            result.setData(page);
            result.setCode(Constants.RETURN_NORMAL);
            result.setMessage("查询报警列表成功");
        } catch (BusinessException e) {
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage(e.getMessage());
            log.error("查询报警列表失败" + abnormalDetailPageVO, e);
            uLogUtils.save("报警次数详情列表", Thread.currentThread(), request, e);
        } catch (Exception e) {
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage("查询报警列表失败");
            log.error("查询报警列表失败" + abnormalDetailPageVO, e);
            uLogUtils.save("报警次数详情列表", Thread.currentThread(), request, e);
        }
        return result;
    }


    @BussinessLog(value = "报警信息处理")
    @ApiOperation(value = "管理")
    @PostMapping(value = "/manage")
    public DataResult manage(@RequestBody AlarmInformation alarmInformation) {
        DataResult result = new DataResult();
        try {
            alarmService.manage(alarmInformation, null);
            result.setCode(Constants.RETURN_NORMAL);
            result.setMessage("成功");
        } catch (BusinessException e) {
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage(e.getMessage());
            log.error("报警处理失败" + alarmInformation.toString(), e);
            uLogUtils.save("报警信息处理", Thread.currentThread(), request, e);
        } catch (Exception e) {
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage("报警处理失败");
            log.error("报警处理失败" + alarmInformation.toString(), e);
            uLogUtils.save("报警信息处理", Thread.currentThread(), request, e);
        }
        return result;
    }

    @BussinessLog(value = "操作记录列表")
    @ApiOperation(value = "操作记录列表")
    @PostMapping(value = "/operationLogList")
    public DataResult<IPage<OperationLog>> operationLogList(@RequestBody AbnormalDetailPageVO abnormalDetailPageVO) {
        DataResult<IPage<OperationLog>> result = new DataResult<IPage<OperationLog>>();
        try {
            IPage<OperationLog> page = alarmService.operationLogList(abnormalDetailPageVO);
            result.setData(page);
            result.setCode(Constants.RETURN_NORMAL);
            result.setMessage("成功");
        } catch (BusinessException e) {
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage(e.getMessage());
            log.error("报警处理失败" + abnormalDetailPageVO.toString(), e);
            uLogUtils.save("操作记录列表", Thread.currentThread(), request, e);
        } catch (Exception e) {
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage("报警处理失败");
            log.error("报警处理失败" + abnormalDetailPageVO.toString(), e);
            uLogUtils.save("操作记录列表", Thread.currentThread(), request, e);
        }
        return result;
    }


    @BussinessLog(value = "关联点位")
    @ApiOperation(value = "关联点位")
    @PostMapping(value = "/joinPoint")
    public DataResult<IPage<OperationLog>> joinPoint(@RequestBody AlarmPointJoinVO alarmPointVO) {
        DataResult<IPage<OperationLog>> result = new DataResult<IPage<OperationLog>>();
        List<AlarmPoint> alarmPointList = alarmPointVO.getAlarmPointList();

        try {

            String alarmCode = alarmPointVO.getAlarmCode();
            if (StringUtils.isEmpty(alarmCode)) {
                throw new BusinessException("没有传入报警类型");
            }

            if (alarmPointList == null || alarmPointList.size() <= 0) {
                LambdaQueryWrapper<AlarmPoint> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(AlarmPoint::getAlarmCode, alarmCode);
                alarmPointService.remove(queryWrapper);
            } else {
                alarmPointList = alarmPointList.stream().map(alarmPoint -> {
                    alarmPoint.setAlarmCode(alarmCode);
                    return alarmPoint;
                }).collect(Collectors.toList());
                alarmPointService.joinPoint(alarmPointList);
            }
            result.setCode(Constants.RETURN_NORMAL);
            result.setMessage("关联点位成功");
        } catch (BusinessException e) {
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage(e.getMessage());
            log.error("关联点位失败", e);
            uLogUtils.save("关联点位失败", Thread.currentThread(), request, e);
        } catch (Exception e) {
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage("关联点位失败");
            log.error("关联点位失败", e);
            uLogUtils.save("关联点位失败", Thread.currentThread(), request, e);
        }
        return result;
    }


    @BussinessLog(value = "关联点位列表")
    @ApiOperation(value = "关联点位列表")
    @RequestMapping(value = "/joinPointList")
    public DataResult joinPointList() {
        DataResult result = new DataResult();
        try {
            List<AlarmPointVO> alarmPointVOS = alarmPointService.joinPointList();
            result.setData(alarmPointVOS);
            result.setCode(Constants.RETURN_NORMAL);
            result.setMessage("查询关联点位列表成功");
        } catch (BusinessException e) {
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage(e.getMessage());
            log.error("查询关联点位列表失败", e);
            uLogUtils.save("查询关联点位列表失败", Thread.currentThread(), request, e);
        } catch (Exception e) {
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage("查询关联点位列表失败");
            log.error("查询关联点位列表失败", e);
            uLogUtils.save("查询关联点位列表失败", Thread.currentThread(), request, e);
        }
        return result;
    }


    //触发报警
    @BussinessLog(value = "触发报警")
    @ApiOperation(value = "触发报警")
    @RequestMapping(value = "/triggeringAlarm")
    public DataResult triggeringAlarm(@RequestBody TriggeringAlarmVO triggeringAlarmVO) {
        DataResult result = new DataResult();
        try {
            if (StringUtils.isBlank(triggeringAlarmVO.getKey())) {
                throw new BusinessException("验证码不能为空");
            }
            if (StringUtils.isBlank(triggeringAlarmVO.getWarningCode())) {
                throw new BusinessException("报警编码不能为空");
            }
            if (StringUtils.isBlank(triggeringAlarmVO.getPulverizerCode())) {
                throw new BusinessException("磨煤机编码不能为空");
            }
            alarmService.triggeringAlarm(triggeringAlarmVO);
            result.setCode(Constants.RETURN_NORMAL);
            result.setMessage("触发报警成功");
        } catch (BusinessException e) {
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage(e.getMessage());
            log.error("触发报警失败", e);
            uLogUtils.save("触发报警失败", Thread.currentThread(), request, e);
        } catch (Exception e) {
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage("触发报警失败");
            log.error("触发报警失败", e);
            uLogUtils.save("触发报警失败", Thread.currentThread(), request, e);
        }
        return result;
    }
}
