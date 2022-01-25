package com.vdata.cloud.datacenter.service.impl;

import com.alibaba.fastjson.JSON;
import com.vdata.cloud.common.exception.BusinessException;
import com.vdata.cloud.datacenter.constants.CommonConstans;
import com.vdata.cloud.datacenter.entity.PointHisHour;
import com.vdata.cloud.datacenter.entity.PointRun;
import com.vdata.cloud.datacenter.service.IPointRunService;
import com.vdata.cloud.datacenter.task.GetDataTask;
import com.vdata.cloud.datacenter.util.GetPulverizerPointUtils;
import com.vdata.cloud.datacenter.util.RedisTemplateUtil;
import com.vdata.cloud.datacenter.vo.*;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Integer.valueOf;

/**
 * @ProjectName: sjzl-master
 * @Package: com.vdata.cloud.datacenter.service.impl
 * @ClassName: PointRunServiceImpl
 * @Author: HK
 * @Description:
 * @Date: 2021/7/29 16:00
 * @Version: 1.0
 */
@Log4j2
@Service
public class PointRunServiceImpl implements IPointRunService {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private IPointRunService pointRunService;


    @Autowired
    private RedisTemplateUtil redisTemplateUtil;

    @Autowired
    private GetPulverizerPointUtils getPulverizerPointUtils;

    @Autowired
    private GetDataTask getDataTask;

    @Override

    public Map<String, Object> getPointRuns(String pulverizerCode, String startTimeStr, String endTimeStr, Long start, Integer count, int orderByDesc) throws ParseException {
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        //查询列表数据
        Query query = new Query();
//        query.addCriteria(Criteria.where("pulverizer_code").is(pulverizerCode).and("date").is(timeFormat.parse(startTimeStr)));
        if (StringUtils.isNotEmpty(startTimeStr) && StringUtils.isEmpty(endTimeStr)) {
            query.addCriteria(Criteria.where("time").gte(timeFormat.parse(startTimeStr)));
        }
        if (StringUtils.isNotEmpty(endTimeStr) && StringUtils.isEmpty(startTimeStr)) {
            query.addCriteria(Criteria.where("time").lte(timeFormat.parse(endTimeStr)));
        }
        if (StringUtils.isNotEmpty(startTimeStr) && StringUtils.isNotEmpty(endTimeStr)) {
            query.addCriteria(Criteria.where("time").gte(timeFormat.parse(startTimeStr)).lte(timeFormat.parse(endTimeStr)));
        }


        if (StringUtils.isNotBlank(pulverizerCode)) {
            query.addCriteria(Criteria.where("pulverizer_code").is(pulverizerCode));
        }

        //存储总条数总页码相关信息
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("size", count);
        resultMap.put("current", start);
        long totalCount = mongoTemplate.count(query, PointRun.class);
        resultMap.put("total", totalCount);
        resultMap.put("pages", (totalCount - 1) / count + 1);


        if (start != null) {
//            query.skip(start);
            query.skip((start - 1) * count);
        }
        if (count != null) {
            query.limit(count);
        }

        if (orderByDesc == 1) {
            query.with(Sort.by(
//                Sort.Order.desc("time")
                    Sort.Order.desc("id")
            ));
        }


   /*     if (startTimeStr.equals(endTimeStr)) {

            Aggregation aggregation = Aggregation.newAggregation(PointRun.class);
            GroupOperation hour = Aggregation.group("hour");
            for (int i = 1; i <= 13; i++) {
                hour.avg("point." + i).as("point." + i);
            }

            AggregationResults<PointRun> aggregate = mongoTemplate.aggregate(aggregation, PointRun.class, PointRun.class);
            return aggregate.getMappedResults();
        }*/


        //类型  点位名称

        List<PointRun> pointRunList = mongoTemplate.find(query, PointRun.class);
        Map<String, Object> pulverizerPointMap = redisTemplateUtil.hmget(CommonConstans.PULVERIZER_POINT_REDIS);
        pointRunList.stream().map(
                pointRun -> {
                    Map<String, Map<String, Double>> pointMap = new LinkedHashMap<>();
                    PulverizerPointRedisVO pulverizerPointRedisVO = null;

                    List<String> nos = pulverizerPointMap.keySet().stream()
                            .filter(str -> str.split("\\|")[0].equals(pulverizerCode))
                            .map(str -> str.split("\\|")[1])
                            .sorted(Comparator.comparing(s -> valueOf(s)))
                            .collect(Collectors.toList());
                    for (int i = 0; i < nos.size(); i++) {
                        String key = pulverizerCode + "|" + nos.get(i);
                        pulverizerPointRedisVO = JSON.toJavaObject(JSON.parseObject(pulverizerPointMap.get(key).toString()), PulverizerPointRedisVO.class);

                    }
//                    pointRun.setPulverizerCode(pulverizerPointRedisVO.getPulverizerCode());
                    pointRun.setPointMap(pointMap);
                    return pointRun;
                }
        ).collect(Collectors.toList());

        resultMap.put("data", pointRunList);
        return resultMap;
    }

    @Override
    public Map<String, Object> getHourPointRuns(String pulverizerCode, String startTimeStr, String endTimeStr, Long start, Integer count) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");


        List<AggregationOperation> list = new ArrayList<>();

        Criteria criteria = Criteria.where("date").gte(dateFormat.parse(startTimeStr)).lte(dateFormat.parse(endTimeStr))
                .and("pulverizer_code").is(pulverizerCode);
        list.add(Aggregation.match(criteria
        ));
        //存储总条数总页码相关信息
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("size", count);
        resultMap.put("current", start);
        long totalCount = mongoTemplate.count(Query.query(criteria), PointRun.class);
        resultMap.put("total", totalCount);
        resultMap.put("pages", (totalCount - 1) / count + 1);

        if (start != null) {
//            list.add(Aggregation.skip(start));
            list.add(Aggregation.skip((start - 1) * count));
        }
        if (count != null) {
            list.add(Aggregation.limit(count));
        }

        list.add(Aggregation.sort(Sort.by(
//                Sort.Order.desc("time")
                Sort.Order.desc("id")
        )));


        Aggregation aggregation = Aggregation.newAggregation(list);
        AggregationResults<PointHisHour> aggregate = mongoTemplate.aggregate(aggregation, PointHisHour.class, PointHisHour.class);
        List<PointHisHour> mappedResults = aggregate.getMappedResults();
        Map<String, Object> pulverizerPointMap = redisTemplateUtil.hmget(CommonConstans.PULVERIZER_POINT_REDIS);
        mappedResults.stream().map(
                pointHisHour -> {
                    Map<String, Map<String, Double>> pointMap = new LinkedHashMap<>();
                    String pulverizerCode1 = pointHisHour.getPulverizerCode();
                    PulverizerPointRedisVO pulverizerPointRedisVO = null;
                    List<String> nos = pulverizerPointMap.keySet().stream()
                            .filter(str -> str.split("\\|")[0].equals(pulverizerCode))
                            .map(str -> str.split("\\|")[1])
                            .sorted(Comparator.comparing(s -> valueOf(s)))
                            .collect(Collectors.toList());
                    for (int i = 0; i < nos.size(); i++) {
                        String key = pulverizerCode + "|" + nos.get(i);
                        pulverizerPointRedisVO = JSON.toJavaObject(JSON.parseObject(pulverizerPointMap.get(key).toString()), PulverizerPointRedisVO.class);

                    }
                    pointHisHour.setPulverizerName(pulverizerPointRedisVO.getPulverizerValue());
                    pointHisHour.setPointMap(pointMap);
                    return pointHisHour;
                }
        ).collect(Collectors.toList());

        resultMap.put("data", mappedResults);
        return resultMap;
    }


    /**
     * 按照指定时间进行填充
     *
     * @param startDate      开始时间
     * @param endDate        结束时间
     * @param pulverizerCode 磨煤机代码
     * @param no             点位编码
     */
    @Override
    public void getHisPointRunByDate(Date startDate, Date endDate, String pulverizerCode, Integer no) throws ParseException {
        //获取redis缓存
        Map<String, Object> map = redisTemplateUtil.hmget(CommonConstans.PULVERIZER_POINT_REDIS);
        Object o = map.get(pulverizerCode + "|" + no);
        PulverizerPointRedisVO pulverizerPointRedisVO = JSON.toJavaObject(JSON.parseObject(o.toString()), PulverizerPointRedisVO.class);
        int enable = pulverizerPointRedisVO.getEnable();
        if (enable == 0) {
            log.info("点位未启动  pulverizerCode:" + pulverizerCode + " no:" + no);
            return;
        }
        //获得dcs信息
        String dcsDataIdentifier = pulverizerPointRedisVO.getDcsDataIdentifier();

        long stdl = startDate.getTime() / 1000;
        long edtl = endDate.getTime() / 1000;


        QueryHistoryVO queryHistoryVO = new QueryHistoryVO();
        queryHistoryVO.setStTime(stdl);
        queryHistoryVO.setEdTime(edtl);
        queryHistoryVO.setInterval(1);
        queryHistoryVO.setTagName(dcsDataIdentifier);
        //调用远程接口
        List<Map<String, Object>> maps = getPulverizerPointUtils.postHistoryDatas(queryHistoryVO);

        for (Map<String, Object> stringObjectMap : maps) {
            String time = stringObjectMap.get("time").toString();
            Map<String, Object> dateMap = getDataTask.dateformat(time);
            double value = Double.valueOf(stringObjectMap.get("value").toString());
            PointRun pointRun = PointRun.builder()
                    .id(pulverizerCode + "-" + dateMap.get("neatDateStr"))
                    .date((Date) dateMap.get("date"))
                    .time((Date) dateMap.get("time"))
                    .pulverizerName(pulverizerPointRedisVO.getPulverizerValue())
                    .hour((Integer) dateMap.get("hour"))
                    .pulverizerCode(pulverizerCode)
                    .dcsDataIdentifier(pulverizerPointRedisVO.getDcsDataIdentifier())
                    .build();

            LinkedHashMap<String, Double> linkedMap = new LinkedHashMap();
            linkedMap.put("no" + no, value);
            pointRun.setPoint(linkedMap);


            //更新条件
            Query query = new Query();
            query.addCriteria(Criteria.where("id").is(pointRun.getId()));
            //更新字段
            //存储数据到mongo
            Update update = new Update();
            update.set("pulverizer_code", pointRun.getPulverizerCode());
            update.set("pulverizer_name", pointRun.getPulverizerName());
            update.set("dcsDataIdentifier", pointRun.getDcsDataIdentifier());
            update.set("hour", pointRun.getHour());
            update.set("date", pointRun.getDate());
            update.set("time", pointRun.getTime());
            update.set("point.no" + no, value);
            mongoTemplate.upsert(query, update, PointRun.class);
        }

    }

    @Override
    public void datafilling() throws ParseException {
        Map<String, Object> pointStatusMap = redisTemplateUtil.hmget(CommonConstans.POINT_STATUS_REDIS);

        try {
            Map<String, Object> pointMap = redisTemplateUtil.hmget(CommonConstans.PULVERIZER_POINT_REDIS);

            //设置脚本状态为正在运行
            setPointStatus(pointStatusMap, true);

            List<PulverizerPointRedisVO> pulverizerPointRedisVOS = pointMap.values().stream().map(
                    obj -> {
                        PulverizerPointRedisVO pulverizerPointRedisVO = JSON.toJavaObject(JSON.parseObject(obj.toString()), PulverizerPointRedisVO.class);
                        return pulverizerPointRedisVO;
                    }
            ).filter(pulverizerPointRedisVO -> pulverizerPointRedisVO.getEnable() == 1).collect(Collectors.toList());
            long oldtime = pointStatusMap.values().stream()
                    .filter(o -> {
                        PointStatusVO pointStatusVO = JSON.toJavaObject(JSON.parseObject(o.toString()), PointStatusVO.class);
                        if (pointMap.get(pointStatusVO.getId()) == null) {
                            return false;
                        }

                        PulverizerPointRedisVO pulverizerPointRedisVO = JSON.toJavaObject(JSON.parseObject(pointMap.get(pointStatusVO.getId()).toString()), PulverizerPointRedisVO.class);
                        if (pulverizerPointRedisVO.getEnable() == 1) {
                            return true;
                        }


                        return pointStatusVO.getRunDate().getTime() > 946656000000L;
                    })
                    .mapToLong(o -> JSON.toJavaObject(JSON.parseObject(o.toString()), PointStatusVO.class).getRunDate().getTime())
                    .min().getAsLong() / 1000;


            long tempTime = oldtime;
            long currentTime = System.currentTimeMillis() / 1000L;

            QueryHistoryVO queryHistoryVO = new QueryHistoryVO();

            while (tempTime < currentTime) {
                queryHistoryVO.setInterval(1);
                queryHistoryVO.setStTime(tempTime);
                queryHistoryVO.setEdTime(tempTime += 1 * 60 * 5);

                if (queryHistoryVO.getEdTime() > currentTime) {
                    queryHistoryVO.setEdTime(currentTime);
                    //获取数据
                    List<PulverizerPointTempDataVO> pulverizerPointTempDataVOS = getDataTask.getHisData(pulverizerPointRedisVOS, queryHistoryVO);
                    //保存数据
                    getDataTask.saveDataBatch(pulverizerPointTempDataVOS, true);
                    break;
                } else {
                    //获取数据
                    List<PulverizerPointTempDataVO> pulverizerPointTempDataVOS = getDataTask.getHisData(pulverizerPointRedisVOS, queryHistoryVO);
                    //保存数据
                    getDataTask.saveDataBatch(pulverizerPointTempDataVOS, true);
                }
            }


//            hourStat(oldtime, currentTime);

        } catch (Exception e) {
            log.info("初始化历史数据失败", e);
        } finally {
            setPointStatus(pointStatusMap, false);
        }
    }

    @Override
    public void hourStat(long oldtime, long currentTime) throws ParseException {
        SimpleDateFormat dateHourformat = new SimpleDateFormat("yyyyMMddHH");
        long startTime = oldtime * 1000L;
        long endTime = currentTime * 1000L;


        startTime = dateHourformat.parse(dateHourformat.format(new Date(startTime))).getTime();
        endTime = dateHourformat.parse(dateHourformat.format(new Date(endTime))).getTime();
        long tempDate = startTime;

        while (tempDate <= endTime) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date(tempDate));
            getDataTask.generateHour(calendar);
            tempDate += 1000 * 60 * 60;
        }

    }

    @Override
    public void deleteDataByYearUp(int year) {
        //获得当前时间
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -year);

        Date time = calendar.getTime();

        Query query = new Query();
        //todo  删除条件还没写完
        query.addCriteria(Criteria.where("date").lt(time));
        mongoTemplate.remove(query, PointRun.class);
    }


    @Override
//    @Async
    public void pullHisPointData(HisPointDataVO hisPointDataVO) {
        Map<String, Object> pointStatusMap = redisTemplateUtil.hmget(CommonConstans.POINT_STATUS_REDIS);
        Map<String, Object> pointMap = redisTemplateUtil.hmget(CommonConstans.PULVERIZER_POINT_REDIS);
        List<PointVO> pointVOs = hisPointDataVO.getPointVOs();
        //判断正在执行的点位
        List<String> execPoints = pointVOs.stream().filter(
                pointVO -> {
                    //对数据进行过滤
                    int no = pointVO.getNo();
                    String pulverizerCode = pointVO.getPulverizerCode();
                    Object pointStatusVOO = pointStatusMap.get(pulverizerCode + "|" + no);
                    if (pointStatusVOO == null) {
                        return false;
                    }
                    PointStatusVO pointStatusVO = JSON.toJavaObject(JSON.parseObject((String) pointStatusVOO), PointStatusVO.class);
                    Object pulverizerPointRedisVOO = pointMap.get(pulverizerCode + "|" + no);
                    if (pulverizerPointRedisVOO == null) {
                        return true;
                    }
                    PulverizerPointRedisVO pulverizerPointRedisVO = JSON.toJavaObject(JSON.parseObject((String) pulverizerPointRedisVOO), PulverizerPointRedisVO.class);
                    if (pulverizerPointRedisVO.getEnable() == 0) {
                        return true;
                    }

                    return pointStatusVO.isEnable();
                }
        ).map(pointVO -> {
            int no = pointVO.getNo();
            String pulverizerCode = pointVO.getPulverizerCode();
            return pulverizerCode + "|" + no;
        }).collect(Collectors.toList());

        if (execPoints.size() > 0) {
            throw new BusinessException("发现有问题的点位(1.正在执行任务,2.未启动采集,3.点位不存在):" + StringUtils.join(execPoints, ","));
        }
        //封装采集的对象
        List<PulverizerPointRedisVO> pulverizerPointRedisVOS = pointVOs.stream().map(pointVO -> {
            int no = pointVO.getNo();
            String pulverizerCode = pointVO.getPulverizerCode();
            String key = pulverizerCode + "|" + no;
            Object pulverizerPointRedisVOO = pointMap.get(key);
            return JSON.toJavaObject(JSON.parseObject((String) pulverizerPointRedisVOO), PulverizerPointRedisVO.class);
        }).collect(Collectors.toList());
        List<String> filterPoints = pointVOs.stream().map(pointVO -> {
            int no = pointVO.getNo();
            String pulverizerCode = pointVO.getPulverizerCode();
            return pulverizerCode + "|" + no;
        }).collect(Collectors.toList());

        try {
            //设置脚本状态为正在运行
            setPointStatus(pointStatusMap, filterPoints, true);
            String startDate = hisPointDataVO.getStartDate();
            String endDate = hisPointDataVO.getEndDate();
            SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            long oldtime = timeFormat.parse(startDate).getTime() / 1000L;


            long tempTime = oldtime;
            long currentTime = timeFormat.parse(endDate).getTime() / 1000L;

            //至少运行5分钟
            if (tempTime + (1 * 60 * 5) > currentTime) {
                throw new BusinessException("最少执行5分钟 startDate:" + startDate + "  endDate:" + endDate);
            }

            QueryHistoryVO queryHistoryVO = new QueryHistoryVO();
            while (tempTime < currentTime) {
                queryHistoryVO.setInterval(1);
                queryHistoryVO.setStTime(tempTime);
                queryHistoryVO.setEdTime(tempTime += 1 * 60 * 5);
                if (queryHistoryVO.getEdTime() > currentTime) {
                    queryHistoryVO.setEdTime(currentTime);
                    //获取数据
                    List<PulverizerPointTempDataVO> pulverizerPointTempDataVOS = getDataTask.getHisData(pulverizerPointRedisVOS, queryHistoryVO);
                    //保存数据
                    getDataTask.saveDataBatch(pulverizerPointTempDataVOS, true);
                    break;
                } else {
                    //获取数据
                    List<PulverizerPointTempDataVO> pulverizerPointTempDataVOS = getDataTask.getHisData(pulverizerPointRedisVOS, queryHistoryVO);
                    //保存数据
                    getDataTask.saveDataBatch(pulverizerPointTempDataVOS, true);
                }

            }
//            hourStat(oldtime, currentTime);
        } catch (BusinessException | ParseException e) {
            log.error("执行失败", e);
            throw new BusinessException("执行失败," + e.getMessage());
        } finally {
            setPointStatus(pointStatusMap, filterPoints, false);
        }
    }


    private void setPointStatus(Map<String, Object> pointStatusMap, boolean b) {
        for (String key : pointStatusMap.keySet()) {
            PointStatusVO pointStatusVO = JSON.toJavaObject(JSON.parseObject((String) pointStatusMap.get(key)), PointStatusVO.class);
            pointStatusVO.setEnable(b);
            String json = JSON.toJSON(pointStatusVO).toString();
            redisTemplateUtil.hset(CommonConstans.POINT_STATUS_REDIS, key, json);
        }
    }


    private void setPointStatus(Map<String, Object> pointStatusMap, List<String> filterPoint, boolean b) {
      /*  for (String key : pointStatusMap.keySet()) {
            PointStatusVO pointStatusVO = JSON.toJavaObject(JSON.parseObject((String) pointStatusMap.get(key)), PointStatusVO.class);
            pointStatusVO.setEnable(b);
            String json = JSON.toJSON(pointStatusVO).toString();
            redisTemplateUtil.hset(CommonConstans.POINT_STATUS_REDIS, key, json);
        }*/

        for (String key : filterPoint) {
            PointStatusVO pointStatusVO = JSON.toJavaObject(JSON.parseObject((String) pointStatusMap.get(key)), PointStatusVO.class);
            pointStatusVO.setEnable(b);
            String json = JSON.toJSON(pointStatusVO).toString();
            redisTemplateUtil.hset(CommonConstans.POINT_STATUS_REDIS, key, json);
        }
    }


}
