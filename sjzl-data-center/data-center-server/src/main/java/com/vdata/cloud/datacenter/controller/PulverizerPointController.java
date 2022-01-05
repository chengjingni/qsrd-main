package com.vdata.cloud.datacenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.vdata.cloud.admin.rpc.BaseUserController;
import com.vdata.cloud.common.annotion.BussinessLog;
import com.vdata.cloud.common.constant.Constants;
import com.vdata.cloud.common.exception.BusinessException;
import com.vdata.cloud.common.vo.DataResult;
import com.vdata.cloud.datacenter.entity.PulverizerPoint;
import com.vdata.cloud.datacenter.service.IPointRunService;
import com.vdata.cloud.datacenter.service.IPulverizerPointService;
import com.vdata.cloud.datacenter.util.ULogUtils;
import com.vdata.cloud.datacenter.vo.HisPointDataVO;
import com.vdata.cloud.datacenter.vo.PointVO;
import com.vdata.cloud.datacenter.vo.PulverizerPointPageVO;
import com.vdata.cloud.datacenter.vo.PulverizerPointVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ProjectName: qsrd
 * @Package: com.vdata.cloud.datacenter.controller
 * @ClassName: PulverizerPointController
 * @Author: HK
 * @Description:
 * @Date: 2021/7/20 13:47
 * @Version: 1.0
 */
@Log4j2
@Api(value = "燃煤机管理", tags = "数据字典管理接口")
@Controller
@RestController
@RequestMapping("pulverizer")
public class PulverizerPointController extends BaseUserController {
    @Autowired
    private IPulverizerPointService pulverizerPointService;

    @Autowired
    private ULogUtils uLogUtils;


    @Autowired
    private IPointRunService pointRunService;

    @BussinessLog(value = "添加燃煤机数据")
    @ApiOperation(value = "添加燃煤机数据")
    @PostMapping(value = "/insert")
    public DataResult insert(@RequestBody PulverizerPoint pulverizerPoint) {
        DataResult result = new DataResult();
        try {
            pulverizerPoint.setUpdateUser(userName());
            pulverizerPoint.setCreateUser(userName());
            pulverizerPointService.insert(pulverizerPoint);
            result.setCode(Constants.RETURN_NORMAL);
            result.setMessage("添加燃煤机数据成功");
        } catch (BusinessException e) {
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage(e.getMessage());
            log.error("添加燃煤机数据失败" + pulverizerPoint.toString(), e);
            uLogUtils.save("添加燃煤机数据", Thread.currentThread(), request, e);
        } catch (Exception e) {
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage("添加燃煤机数据失败");
            log.error("添加燃煤机数据失败" + pulverizerPoint.toString(), e);
            uLogUtils.save("添加燃煤机数据", Thread.currentThread(), request, e);
        }
        return result;
    }


    @BussinessLog(value = "删除磨煤机")
    @ApiOperation(value = "删除磨煤机")
    @GetMapping(value = "/delete")
    public DataResult delete(@RequestParam Integer id) {
        DataResult result = new DataResult();
        try {

            pulverizerPointService.delete(id);
            result.setCode(Constants.RETURN_NORMAL);
            result.setMessage("删除磨煤机成功");
        } catch (BusinessException e) {
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage(e.getMessage());
            log.error("删除磨煤机成功失败", e);
            uLogUtils.save("删除磨煤机成功失败", Thread.currentThread(), request, e);
        } catch (Exception e) {
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage("删除磨煤机成功失败");
            log.error("删除磨煤机成功失败", e);
            uLogUtils.save("添加燃煤机数据", Thread.currentThread(), request, e);
        }
        return result;
    }


    @BussinessLog(value = "修改燃煤机数据")
    @ApiOperation(value = "修改燃煤机数据")
    @PostMapping(value = "/update")
    public DataResult update(@RequestBody PulverizerPoint pulverizerPoint) {
        DataResult result = new DataResult();
        try {
            pulverizerPoint.setUpdateUser(userName());
            pulverizerPointService.updatev1(pulverizerPoint);
//            pulverizerPointService.listSaveRedisByDcs();
            result.setCode(Constants.RETURN_NORMAL);
            result.setMessage("修改燃煤机数据成功");
        } catch (BusinessException e) {
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage(e.getMessage());
            log.error("修改燃煤机数据失败" + pulverizerPoint.toString(), e);
            uLogUtils.save("修改燃煤机数据", Thread.currentThread(), request, e);

        } catch (Exception e) {
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage("修改燃煤机数据失败");
            log.error("修改燃煤机数据失败" + pulverizerPoint.toString(), e);
            uLogUtils.save("修改燃煤机数据", Thread.currentThread(), request, e);

        }
        return result;
    }

    @BussinessLog(value = "查询燃煤机数据")
    @ApiOperation(value = "查询燃煤机数据")
    @PostMapping(value = "/page")
    public DataResult page(@RequestBody PulverizerPointPageVO pulverizerPointPageVO) {
        DataResult result = new DataResult();
        try {
            IPage<PulverizerPointVO> page = pulverizerPointService.page(pulverizerPointPageVO);
            result.setData(page);
            result.setCode(Constants.RETURN_NORMAL);
            result.setMessage("查询燃煤机数据成功" + userName() + userId());
        } catch (BusinessException e) {
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage(e.getMessage());
            log.error("查询燃煤机数据失败" + pulverizerPointPageVO.toString(), e);
            uLogUtils.save("查询燃煤机数据", Thread.currentThread(), request, e);
        } catch (Exception e) {
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage("查询燃煤机数据失败");
            log.error("查询燃煤机数据失败" + pulverizerPointPageVO.toString(), e);
            uLogUtils.save("查询燃煤机数据", Thread.currentThread(), request, e);

        }
        return result;
    }

    @BussinessLog(value = "查询燃煤机数据")
    @ApiOperation(value = "查询单个点位信息")
    @GetMapping(value = "/get")
    public DataResult get(int id) {
        DataResult result = new DataResult();
        try {
            PulverizerPointVO pulverizerPointVO = pulverizerPointService.get(id);
            result.setData(pulverizerPointVO);
            result.setCode(Constants.RETURN_NORMAL);
            result.setMessage("查询燃煤机数据成功");
        } catch (BusinessException e) {
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage(e.getMessage());
            log.error("查询燃煤机数据失败" + id, e);
            uLogUtils.save("查询单个点位信息", Thread.currentThread(), request, e);
        } catch (Exception e) {
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage("查询燃煤机数据失败");
            log.error("查询燃煤机数据失败" + id, e);
            uLogUtils.save("查询单个点位信息", Thread.currentThread(), request, e);
        }
        return result;
    }

    @BussinessLog(value = "磨煤机点位历史数据拉取")
    @ApiOperation(value = "磨煤机点位历史数据拉取")
    @PostMapping(value = "/pullHisPointData")
    public DataResult pullHisPointData(@RequestBody HisPointDataVO hisPointDataVO) {
        DataResult result = new DataResult();
        try {
            if (hisPointDataVO.getPointVOs().size() == 0) {
                throw new BusinessException("拉取任务为0");
            }


            pointRunService.pullHisPointData(hisPointDataVO);

            result.setCode(Constants.RETURN_NORMAL);
            result.setMessage("磨煤机点位历史数据拉取成功");
        } catch (BusinessException e) {
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage(e.getMessage());
            log.error("磨煤机点位历史数据拉取失败", e);
            uLogUtils.save("磨煤机点位历史数据拉取失败", Thread.currentThread(), request, e);
        } catch (Exception e) {
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage("磨煤机点位历史数据拉取失败");
            log.error("磨煤机点位历史数据拉取失败", e);
            uLogUtils.save("磨煤机点位历史数据拉取失败", Thread.currentThread(), request, e);
        }
        return result;
    }


    @BussinessLog(value = "测试接口")
    @ApiOperation(value = "测试接口")
    @PostMapping(value = "/test")
    public HisPointDataVO test() {

        LambdaQueryWrapper<PulverizerPoint> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PulverizerPoint::getEnable, 1);
        List<PointVO> pointVOS = pulverizerPointService.list(wrapper)
                .stream().map(pulverizerPoint -> {
                    PointVO pointVO = new PointVO();
                    pointVO.setNo(pulverizerPoint.getNo());
                    pointVO.setPulverizerCode(pulverizerPoint.getPulverizerCode());
                    return pointVO;
                }).collect(Collectors.toList());


        HisPointDataVO hisPointDataVO = new HisPointDataVO();
        hisPointDataVO.setStartDate("2021-12-13 00:01:00");
        hisPointDataVO.setEndDate("2021-12-13 00:02:00");
        hisPointDataVO.setPointVOs(pointVOS);
//        pointRunService.pullHisPointData(hisPointDataVO);
        return hisPointDataVO;
    }


    @BussinessLog(value = "获得磨煤机dcs列表")
    @ApiOperation(value = "获得磨煤机dcs列表")
    @PostMapping(value = "/listPointDcsLog")
    public DataResult listPointDcsLog(@RequestBody Map<String, Object> map) {


        DataResult result = new DataResult();
        try {
            int pointId = Integer.valueOf(map.get("pointId").toString());
            long page = Long.valueOf(map.get("page").toString());
            long size = Long.valueOf(map.get("size").toString());
            result.setData(pulverizerPointService.listPointDcsLog(pointId, page, size));


            result.setCode(Constants.RETURN_NORMAL);
            result.setMessage("获得磨煤机dcs列表成功");
        } catch (BusinessException e) {
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage(e.getMessage());
            log.error("获得磨煤机dcs列表失败", e);
            uLogUtils.save("获得磨煤机dcs列表失败", Thread.currentThread(), request, e);
        } catch (Exception e) {
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage("获得磨煤机dcs列表失败");
            log.error("获得磨煤机dcs列表失败", e);
            uLogUtils.save("获得磨煤机dcs列表失败", Thread.currentThread(), request, e);
        }
        return result;
    }

}
