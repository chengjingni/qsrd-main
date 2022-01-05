package com.vdata.cloud.datacenter.controller;

import com.alibaba.fastjson.JSON;
import com.vdata.cloud.common.constant.Constants;
import com.vdata.cloud.common.vo.DataResult;
import com.vdata.cloud.datacenter.constants.CommonConstans;
import com.vdata.cloud.datacenter.entity.PulverizerRunning;
import com.vdata.cloud.datacenter.service.IPointRunService;
import com.vdata.cloud.datacenter.service.IPulverizerPointService;
import com.vdata.cloud.datacenter.util.RedisTemplateUtil;
import com.vdata.cloud.datacenter.vo.PointListQuyerVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ProjectName: sjzl-master
 * @Package: com.vdata.cloud.datacenter.controller
 * @ClassName: PointRunController
 * @Author: LF
 * @Description: 点位运行数据（传感器监测数据）
 * @Date: 2021/07/22 15:06
 * @Version: 1.0
 */
@Log4j2
@Api(value = "运行数据管理", tags = "运行数据管理")
@Controller
@RestController
@RequestMapping("pointRun")
public class PointRunController {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private IPointRunService pointRunService;


    @Autowired
    private RedisTemplateUtil redisTemplateUtil;

    @Autowired
    private IPulverizerPointService pulverizerPointService;

    @ApiOperation(value = "数据量查询")
    @PostMapping(value = "/listCount")
    public DataResult listCount(
            @RequestParam("pulverizerCode") String pulverizerCode, @RequestParam(value = "startTime", required = false) String startTime, @RequestParam(value = "endTime", required = false) String endTime) {
        DataResult result = new DataResult();
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            //查询列表数据
            Query query = new Query();
            query.addCriteria(Criteria.where("pulverizer_code").is(pulverizerCode));
            if (StringUtils.isNotEmpty(startTime) && StringUtils.isEmpty(endTime)) {
                query.addCriteria(Criteria.where("date").gte(dateFormat.parse(startTime)));
            }
            if (StringUtils.isNotEmpty(endTime) && StringUtils.isEmpty(startTime)) {
                query.addCriteria(Criteria.where("date").lte(dateFormat.parse(endTime)));
            }

            if (StringUtils.isNotEmpty(startTime) && StringUtils.isNotEmpty(endTime)) {
                query.addCriteria(Criteria.where("date").gte(dateFormat.parse(startTime)).lte(dateFormat.parse(endTime)));
            }
            result.setData(mongoTemplate.count(query, Document.class, "point_run2"));
            result.setCode(Constants.RETURN_NORMAL);
            result.setMessage("数据量查询成功！");
        } catch (Exception e) {
            log.error("数据量查询出错！", e);
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage("数据量查询失败！");
        }
        return result;
    }

    @ApiOperation(value = "查询接口")
    @PostMapping(value = "/list")
    public DataResult list(
            @RequestBody PointListQuyerVO pointListQuyerVO
    ) {
        DataResult result = new DataResult();
        String startTimeStr = pointListQuyerVO.getStartTime();
        String endTimeStr = pointListQuyerVO.getEndTime();
        Integer count = pointListQuyerVO.getCount();
        Long start = pointListQuyerVO.getStart();
        String pulverizerCode = pointListQuyerVO.getPulverizerCode();
        int orderByDesc = pointListQuyerVO.getOrderByDesc();
        Map<String, Object> resultMap = null;
        try {
      /*      if (CommonUtil.isNotEmpty(startTimeStr) && CommonUtil.isNotEmpty(endTimeStr) && startTimeStr.equals(endTimeStr)) {
                resultMap = pointRunService.getPointRuns(pulverizerCode, startTimeStr, endTimeStr, start, count);

            } else {

                resultMap = pointRunService.getHourPointRuns(pulverizerCode, startTimeStr, endTimeStr, start, count);

            }*/
            //不计算小时平均值
            resultMap = pointRunService.getPointRuns(pulverizerCode, startTimeStr, endTimeStr, start, count, orderByDesc);
            result.setData(resultMap);
            result.setCode(Constants.RETURN_NORMAL);
            result.setMessage("列表查询成功！");
        } catch (Exception e) {
            log.error("列表查询出错！", e);
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage("列表查询失败！");
        }
        return result;
    }


    @ApiOperation(value = "统计数据")
    @PostMapping(value = "/statisticsData")
    public DataResult statisticsData(
            @RequestParam("pulverizerCode") String pulverizerCodes, @RequestParam("startTime") String startTime, @RequestParam("endTime") String endTime) {
        DataResult result = new DataResult();
        try {
            String[] pulverizerCode = pulverizerCodes.split(",");
            ArrayList<ArrayList<Map>> arrayLists = new ArrayList();
            if (startTime.equals(endTime)) {//统计某一天的平均值，按小时统计
                for (int i = 0; i < pulverizerCode.length; i++) {
                    //查询列表数据
                    Query query = new Query();
                    query.addCriteria(Criteria.where("pulverizer_code").is(pulverizerCode[i]));
                    query.addCriteria(Criteria.where("date").is(startTime));
                    List<Document> documentList = mongoTemplate.find(query, Document.class, "point_run");
                    query.with(Sort.by(
                            Sort.Order.desc("time")
                    ));
                    List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
                    for (Document document : documentList) {
                        Map message = new HashMap();
                        message.put("time", document.get("time"));
                        message.put("no1", document.get("no1"));
                        message.put("no2", document.get("no2"));
                        message.put("no3", document.get("no3"));
                        message.put("no4", document.get("no4"));
                        message.put("no5", document.get("no5"));
                        message.put("no6", document.get("no6"));
                        message.put("no7", document.get("no7"));
                        message.put("no8", document.get("no8"));
                        message.put("no9", document.get("no9"));
                        message.put("no10", document.get("no10"));
                        message.put("no11", document.get("no11"));
                        message.put("no12", document.get("no12"));
                        message.put("no13", document.get("no13"));
                        list.add(message);
                    }
                }
            } else {//统计某一段时间的平均值，按天统计

            }
            result.setCode(Constants.RETURN_NORMAL);
            result.setMessage("列表查询成功！");
        } catch (Exception e) {
            log.error("列表查询出错！", e);
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage("列表查询失败！");
        }
        return result;
    }


    @ApiOperation(value = "磨煤机运行")
    @GetMapping(value = "/pulverizerRunnings")
    public DataResult pulverizerRunnings() {
        DataResult result = new DataResult();
        try {
            List<Object> list = redisTemplateUtil.lGetRange(CommonConstans.PULVERIZER_RUNNING_LIST_REDIS, 0, 4);
            List<PulverizerRunning> collect = list.stream().map(o -> {
                PulverizerRunning pulverizerRunning = JSON.toJavaObject(JSON.parseObject(o.toString()), PulverizerRunning.class);
                return pulverizerRunning;
            }).collect(Collectors.toList());
            result.setData(collect);
            result.setCode(Constants.RETURN_NORMAL);
            result.setMessage("获取磨煤机运行成功！");
        } catch (Exception e) {
            log.error("获取磨煤机运行失败！", e);
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage("获取磨煤机运行失败！");
        }
        return result;
    }


}
