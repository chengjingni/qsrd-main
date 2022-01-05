package com.vdata.cloud.datacenter.task;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.vdata.cloud.common.constant.Constants;
import com.vdata.cloud.common.vo.DataResult;
import com.vdata.cloud.datacenter.constants.CommonConstans;
import com.vdata.cloud.datacenter.entity.BaseDict;
import com.vdata.cloud.datacenter.entity.PointHisHour;
import com.vdata.cloud.datacenter.entity.PointRun;
import com.vdata.cloud.datacenter.service.IAlarmService;
import com.vdata.cloud.datacenter.service.ISyncPointService;
import com.vdata.cloud.datacenter.util.GetPulverizerPointUtils;
import com.vdata.cloud.datacenter.util.RedisTemplateUtil;
import com.vdata.cloud.datacenter.vo.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ProjectName: qsrd-main
 * @Package: com.vdata.cloud.datacenter.task
 * @ClassName: GetDataTask
 * @Author: HK
 * @Description: 获取数据
 * @Date: 2021/11/8 15:36
 * @Version: 1.0
 */
@Log4j2
@Component
@EnableScheduling
public class GetDataTask {


    @Autowired
    private GetPulverizerPointUtils getPulverizerPointUtils;


    @Autowired
    private RedisTemplateUtil redisTemplateUtil;


    @Autowired
    private ISyncPointService syncPointService;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private SimpMessagingTemplate wsTemplate;

    public static final String pointRunMongo = "point_run";

    @Autowired
    private IAlarmService alarmService;
    /**
     * 定时获取更新点位信息
     */
/*
    @Scheduled(cron = "0/30 * * * * ? ")
    public void getPoints() {

        //获取更新点位信息
        List<SyncPoint> testPointAll = getPulverizerPointUtils.getTestPointAll();


        List<String> names = syncPointService.list(new LambdaQueryWrapper<SyncPoint>().select(SyncPoint::getName))
                .stream().map(syncPoint -> syncPoint.getName()).collect(Collectors.toList());


        List<SyncPoint> updates = testPointAll.stream().filter(syncPoint -> names.contains(syncPoint.getName())).collect(Collectors.toList());
        List<SyncPoint> saves = testPointAll.stream().filter(syncPoint -> !names.contains(syncPoint.getName())).collect(Collectors.toList());

        syncPointService.updateBatchById(updates);

        syncPointService.saveBatch(saves);

    }
*/

    /**
     * 获取实时数据推送到前端
     */
//    @Scheduled(cron = "*/30 * * * * ?")//三十秒执行一次
    @Scheduled(cron = "*/5 * * * * ?")//三十秒执行一次
    public void getDatas() throws ParseException {
        Map<String, Object> map = redisTemplateUtil.hmget(CommonConstans.PULVERIZER_POINT_REDIS);
        //获得启动的点位信息
        List<PulverizerPointRedisVO> pulverizerPointRedisVOS = map.values().stream().map(
                obj -> {
                    PulverizerPointRedisVO pulverizerPointRedisVO = JSON.toJavaObject(JSON.parseObject(obj.toString()), PulverizerPointRedisVO.class);
                    return pulverizerPointRedisVO;
                }
        ).filter(pulverizerPointRedisVO -> pulverizerPointRedisVO.getEnable() == 1).collect(Collectors.toList());
        //获得点位信息的id
        List<String> tagNames = pulverizerPointRedisVOS.stream()
                .map(pulverizerPointRedisVO -> pulverizerPointRedisVO.getDcsDataIdentifier())
                .collect(Collectors.toList());


        List<Map<String, Object>> realTimeDatas = getPulverizerPointUtils.getRealTimeDatas(tagNames);

        log.info("realTimeDatas:"+realTimeDatas.size());

        List<PulverizerPointTempDataVO> pulverizerPointTempDataVOS = new ArrayList<>();
        for (int i = 0; i < pulverizerPointRedisVOS.size(); i++) {
            Map<String, Object> stringObjectMap = realTimeDatas.get(i);
            SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = timeFormat.format(new Date());
            double value = 0;
            if (stringObjectMap != null) {
                time = (String) stringObjectMap.get("time");
//                value = (double) stringObjectMap.get("value");
                value = Double.valueOf(stringObjectMap.get("value").toString());
            }
            PulverizerPointTempDataVO pulverizerPointTempDataVO = new PulverizerPointTempDataVO();
            BeanUtils.copyProperties(pulverizerPointRedisVOS.get(i), pulverizerPointTempDataVO);
            pulverizerPointTempDataVO.setTime(time);
            pulverizerPointTempDataVO.setValue(value);
            pulverizerPointTempDataVOS.add(pulverizerPointTempDataVO);
        }


        Map<String, List<PulverizerPointTempDataVO>> pulverizerPointTempDataVOMap = pulverizerPointTempDataVOS.stream()
//                .collect(Collectors.groupingBy(pulverizerPointTempDataVO -> pulverizerPointTempDataVO.getPulverizerCode() + "|" + pulverizerPointTempDataVO.getTime()));
                //去除时间分组
                .collect(Collectors.groupingBy(pulverizerPointTempDataVO -> pulverizerPointTempDataVO.getPulverizerCode() ));
        //磨煤机代码列表
//        List<String> pulverizerCodes = pulverizerPointTempDataVOMap.keySet().stream().map(s -> s.split("\\|")[0]).distinct().collect(Collectors.toList());
        List<PointRun> pointRunList = new ArrayList<>();
        for (String key : pulverizerPointTempDataVOMap.keySet()) {
            String[] splitKey = key.split("\\|");
            String pulverizerCode = splitKey[0];

//            String time = splitKey[1];
            Map<String, Object> dateMap = dateformat();
            Object hget = redisTemplateUtil.hget(CommonConstans.BASE_DICT_REDIS, "pulverizer|" + pulverizerCode);
            BaseDict baseDict = JSON.toJavaObject(JSON.parseObject(hget.toString()), BaseDict.class);
            PointRun pointRun = PointRun.builder()
                    .id(pulverizerCode + "-" + dateMap.get("neatDateStr"))
                    .date((Date) dateMap.get("date"))
                    .time((Date) dateMap.get("time"))
                    .pulverizerName(baseDict.getValue())
                    .hour((Integer) dateMap.get("hour"))
                    .pulverizerCode(pulverizerCode)
                    .build();
            LinkedHashMap<String, Double> linkedMap = new LinkedHashMap();
            for (PulverizerPointTempDataVO pulverizerPointTempDataVO : pulverizerPointTempDataVOMap.get(key)) {

                double value = pulverizerPointTempDataVO.getValue();
                linkedMap.put("no" + pulverizerPointTempDataVO.getNo(), value);

                //发送报警信息
                double up = pulverizerPointTempDataVO.getUpperLimit().doubleValue();
                double low = pulverizerPointTempDataVO.getLowerLimit().doubleValue();
                if (pulverizerPointTempDataVO.getAlarm() == 1) {
                    if (value > up || value < low) {
                        String alarmDescription = "";
                        if (value > up) {
                            alarmDescription = "超出上限,上限值为:" + up + ",当前值为:" + value;
                        }
                        if (value < low) {
                            alarmDescription = "低于下限,下限值为:" + low + ",当前值为:" + value;
                        }
                        alarmService.alarmCreate((Date) dateMap.get("time"), pulverizerPointTempDataVO.getId(), "1", alarmDescription, String.valueOf(value), pulverizerPointTempDataVO.getPositionCode());
                    }
                }

            }
            pointRun.setPoint(linkedMap);
//            pullWebSocket(pointRun);
            pointRunList.add(pointRun);
        }

        pullWebSocket(pointRunList);


    }

    //批量获取历史数据

    /**
     * 批量获取数据
     */
    @Scheduled(cron = "30 * * * * ?")//每一分钟执行一次
    public void getBatchDatas() throws ParseException {
        Map<String, Object> map = redisTemplateUtil.hmget(CommonConstans.PULVERIZER_POINT_REDIS);
        //获得启动的点位信息
        List<PulverizerPointRedisVO> pulverizerPointRedisVOS = map.values().stream().map(
                obj -> {
                    PulverizerPointRedisVO pulverizerPointRedisVO = JSON.toJavaObject(JSON.parseObject(obj.toString()), PulverizerPointRedisVO.class);
                    return pulverizerPointRedisVO;
                }
        ).filter(pulverizerPointRedisVO -> pulverizerPointRedisVO.getEnable() == 1).collect(Collectors.toList());
        //获得点位信息的id
        List<String> tagNames = pulverizerPointRedisVOS.stream()
                .map(pulverizerPointRedisVO -> pulverizerPointRedisVO.getDcsDataIdentifier())
                .collect(Collectors.toList());


        Date currentDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.MINUTE, -1);
        Date startDate = calendar.getTime();
        QueryHistoryVO queryHistoryVO = new QueryHistoryVO();
        queryHistoryVO.setInterval(1);
        queryHistoryVO.setStTime(startDate.getTime() / 1000L);
        queryHistoryVO.setEdTime(currentDate.getTime() / 1000L);


        List<PulverizerPointTempDataVO> pulverizerPointTempDataVOS = getHisData(pulverizerPointRedisVOS, queryHistoryVO);

        saveDataBatch(pulverizerPointTempDataVOS, false);
    }

    public List<PulverizerPointTempDataVO> getHisData(List<PulverizerPointRedisVO> pulverizerPointRedisVOS, QueryHistoryVO queryHistoryVO) {
        return pulverizerPointRedisVOS.stream().map(
                pulverizerPointRedisVO -> {
                    queryHistoryVO.setTagName(pulverizerPointRedisVO.getDcsDataIdentifier());
                    List<Map<String, Object>> historyDatas = getPulverizerPointUtils.getHistoryDatas(queryHistoryVO);


                    return historyDatas.stream().filter(historyData -> historyData != null).map(historyData -> {
                        String time = (String) historyData.get("time");
//                        double value = (Double) historyData.get("value");
                        double value = Double.valueOf(historyData.get("value").toString());
                        PulverizerPointTempDataVO pulverizerPointTempDataVO = new PulverizerPointTempDataVO();
                        BeanUtils.copyProperties(pulverizerPointRedisVO, pulverizerPointTempDataVO);
                        pulverizerPointTempDataVO.setTime(time);
                        pulverizerPointTempDataVO.setValue(value);
                        return pulverizerPointTempDataVO;
                    });

                }
        ).flatMap(m -> m).collect(Collectors.toList());
    }

    public void saveDataBatch(List<PulverizerPointTempDataVO> pulverizerPointTempDataVOS, boolean isOne) throws ParseException {
        Map<String, List<PulverizerPointTempDataVO>> pulverizerPointTempDataVOMap = pulverizerPointTempDataVOS.stream()
                .collect(Collectors.groupingBy(pulverizerPointTempDataVO -> pulverizerPointTempDataVO.getPulverizerCode() + "|" + pulverizerPointTempDataVO.getTime()));
        //磨煤机代码列表
//        List<String> pulverizerCodes = pulverizerPointTempDataVOMap.keySet().stream().map(s -> s.split("\\|")[0]).distinct().collect(Collectors.toList());

        for (String key : pulverizerPointTempDataVOMap.keySet()) {
            String[] splitKey = key.split("\\|");
            String pulverizerCode = splitKey[0];
            String time = splitKey[1];
            Map<String, Object> dateMap = dateformat(time);
            Object hget = redisTemplateUtil.hget(CommonConstans.BASE_DICT_REDIS, "pulverizer|" + pulverizerCode);
            BaseDict baseDict = JSON.toJavaObject(JSON.parseObject(hget.toString()), BaseDict.class);
            PointRun pointRun = PointRun.builder()
                    .id(pulverizerCode + "-" + dateMap.get("neatDateStr"))
                    .date((Date) dateMap.get("date"))
                    .time((Date) dateMap.get("time"))
                    .pulverizerName(baseDict.getValue())
                    .hour((Integer) dateMap.get("hour"))
                    .pulverizerCode(pulverizerCode)
                    .build();
            LinkedHashMap<String, Double> linkedMap = new LinkedHashMap();
            for (PulverizerPointTempDataVO pulverizerPointTempDataVO : pulverizerPointTempDataVOMap.get(key)) {

                linkedMap.put("no" + pulverizerPointTempDataVO.getNo(), pulverizerPointTempDataVO.getValue());

                //记录获取时间
                if (!isOne) {

                    saveRunDate(pulverizerPointTempDataVO.getNo(), pulverizerCode, (Date) dateMap.get("time"));
                }

            }
            pointRun.setPoint(linkedMap);
            mongoTemplate.save(pointRun);
        }
    }


    //保存当前点位运行时间
    public void saveRunDate(int no, String pulverizerCode, Date time) {


        String key = pulverizerCode + "|" + no;

        PointStatusVO old = JSON.toJavaObject(JSON.parseObject((String) redisTemplateUtil.hget(CommonConstans.POINT_STATUS_REDIS, key)), PointStatusVO.class);

        if (old != null) {
            if (old.getRunDate().getTime() < time.getTime()) {
                old.setRunDate(time);

                String json = JSON.toJSON(old).toString();
                redisTemplateUtil.hset(CommonConstans.POINT_STATUS_REDIS, key, json);
            }
        } else {

            old = new PointStatusVO();
            old.setId(key);
            old.setEnable(false);
            old.setRunDate(time);
            String json = JSON.toJSON(old).toString();
            redisTemplateUtil.hset(CommonConstans.POINT_STATUS_REDIS, key, json);
        }


    }


    /**
     * 时间转换
     */
    public Map<String, Object> dateformat(String dateStr) throws ParseException {
        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat neatdateformat = new SimpleDateFormat("yyyyMMddHHmmss");
        SimpleDateFormat dateHourformat = new SimpleDateFormat("yyyyMMddHH");
        Date time = timeFormat.parse(dateStr);
        String date_ = dateformat.format(time);
        Date date = dateformat.parse(date_);
        String neatDateStr = neatdateformat.format(time);
        Map<String, Object> map = new HashMap<>();
        map.put("date", date);
        map.put("time", time);
        map.put("neatDateStr", neatDateStr);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        map.put("hour", calendar.get(Calendar.HOUR_OF_DAY));

        return map;
    }


    /**
     * 时间转换
     */
    public Map<String, Object> dateformat() throws ParseException {
        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat neatdateformat = new SimpleDateFormat("yyyyMMddHHmmss");
        SimpleDateFormat dateHourformat = new SimpleDateFormat("yyyyMMddHH");
        Date time = new Date();
        String date_ = dateformat.format(time);
        Date date = dateformat.parse(date_);
        String neatDateStr = neatdateformat.format(time);
        Map<String, Object> map = new HashMap<>();
        map.put("date", date);
        map.put("time", time);
        map.put("neatDateStr", neatDateStr);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        map.put("hour", calendar.get(Calendar.HOUR_OF_DAY));

        return map;
    }



    private void pullWebSocket(PointRun pointRun) {
        DataResult result = new DataResult();
        Map<String, Double> point = pointRun.getPoint();
        Map<String, Map<String, Double>> pointMap = new LinkedHashMap<>();

        List<PointRunTimeVO> list = new ArrayList<>();
        String pulverizerCode = pointRun.getPulverizerCode();
        PulverizerPointRedisVO pulverizerPointRedisVO = null;
        Map<String, Object> hmget = redisTemplateUtil.hmget(CommonConstans.PULVERIZER_POINT_REDIS);


        for (String key : point.keySet()) {
            String no = key.replaceAll("no", "");
            String rKey = pulverizerCode + "|" + no;

            pulverizerPointRedisVO = JSON.toJavaObject(JSON.parseObject(hmget.get(rKey).toString()), PulverizerPointRedisVO.class);
            PointRunTimeVO pointRunTimeVO = new PointRunTimeVO();
            BeanUtils.copyProperties(pulverizerPointRedisVO, pointRunTimeVO);
            pointRunTimeVO.setTime(pointRun.getTime());
            pointRunTimeVO.setValue(point.get(key).doubleValue());
            list.add(pointRunTimeVO);
        }


        /*封装一层*/
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("data", list);
        resultMap.put("type", CommonConstans.SocketType.RUNDATA.getValue());
        result.setData(resultMap);
        result.setCode(Constants.RETURN_NORMAL);
        result.setMessage("获取磨煤机运行数据成功");


        log.info("【推送消息】开始执行：{}", DateUtil.formatDateTime(new Date()));
        log.info(result);
//        wsTemplate.convertAndSend("/topic/" + CommonConstans.PULVERIZER_WEBSOCKET + "/" + pointRun.getPulverizerCode(), result);
        wsTemplate.convertAndSend("/topic/server", result);
        log.info("【推送消息】执行结束：{}", DateUtil.formatDateTime(new Date()));
    }


    private void pullWebSocket(List<PointRun> pointRuns) {

        Map<String, Object> map = new HashMap<>();

        DataResult result = new DataResult();

        for (PointRun pointRun : pointRuns) {
            Map<String, Double> point = pointRun.getPoint();

            List<PointRunTimeVO> list = new ArrayList<>();
            String pulverizerCode = pointRun.getPulverizerCode();
            PulverizerPointRedisVO pulverizerPointRedisVO = null;
            Map<String, Object> hmget = redisTemplateUtil.hmget(CommonConstans.PULVERIZER_POINT_REDIS);


            for (String key : point.keySet()) {
                String no = key.replaceAll("no", "");
                String rKey = pulverizerCode + "|" + no;

                pulverizerPointRedisVO = JSON.toJavaObject(JSON.parseObject(hmget.get(rKey).toString()), PulverizerPointRedisVO.class);
                PointRunTimeVO pointRunTimeVO = new PointRunTimeVO();
                BeanUtils.copyProperties(pulverizerPointRedisVO, pointRunTimeVO);
                pointRunTimeVO.setTime(pointRun.getTime());
                pointRunTimeVO.setValue(point.get(key).doubleValue());
                list.add(pointRunTimeVO);
            }

            map.put(pulverizerCode, list);
        }



        /*封装一层*/
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("data", map);
        resultMap.put("type", CommonConstans.SocketType.RUNDATA.getValue());
        result.setData(resultMap);
        result.setCode(Constants.RETURN_NORMAL);
        result.setMessage("获取磨煤机运行数据成功");


        log.info("【推送消息】开始执行：{}", DateUtil.formatDateTime(new Date()));
        log.info(result);
//        wsTemplate.convertAndSend("/topic/" + CommonConstans.PULVERIZER_WEBSOCKET + "/" + pointRun.getPulverizerCode(), result);
        wsTemplate.convertAndSend("/topic/server", result);
        log.info("【推送消息】执行结束：{}", DateUtil.formatDateTime(new Date()));
    }


    //    @Scheduled(cron = "0 0 0/1 * * ?")
    public void statisticsData() throws Exception {//每小时执行一次
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - 1);
        generateHour(calendar);
    }


    /**
     * 每天执行一次  统计前一个月遗漏的小时统计
     *
     * @throws ParseException
     */
//    @Scheduled(cron = "0 1 0 * * ? ")
    public void generateHours() throws ParseException {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();


        Date endDate = calendar.getTime();
        String endTimeStr = dateFormat.format(endDate);
        calendar.add(Calendar.MONTH, -1);
        Date startDate = calendar.getTime();
        String startTimeStr = dateFormat.format(startDate);


        Query query = new Query();

        query.addCriteria(Criteria.where("date").gte(dateFormat.parse(startTimeStr)).lte(dateFormat.parse(endTimeStr)));
        List<Date> times = mongoTemplate.findDistinct(query, "time", "point_his_hour2", Date.class);


        List<Calendar> calendars = generateTimes(calendar, dateFormat.parse(startTimeStr), dateFormat.parse(endTimeStr), times);


        for (Calendar c : calendars) {
            generateHour(c);
        }
    }


    private List<Calendar> generateTimes(Calendar calendar, Date startDate, Date endDate, List<Date> times) {
        Date tmpDate = startDate;
        List<Calendar> calendars = new ArrayList<>();


        while (tmpDate.getTime() < endDate.getTime()) {

            calendar.setTime(tmpDate);
            if (!times.contains(tmpDate)) {
                Calendar tmpCalendar = Calendar.getInstance();
                tmpCalendar.setTime(tmpDate);
                calendars.add(tmpCalendar);
            }

            calendar.add(Calendar.HOUR, 1);
            tmpDate = calendar.getTime();
        }

        return calendars;
    }

    public void generateHour(Calendar calendar) {
        Integer hour = calendar.get(Calendar.HOUR_OF_DAY);

        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);


        Date time = calendar.getTime();
        log.info("time:" + time);
        calendar.set(Calendar.HOUR_OF_DAY, 0);

        Date date = calendar.getTime();
        log.info("date:" + date);


        Map<String, Object> baseDictMap = redisTemplateUtil.hmget(CommonConstans.BASE_DICT_REDIS);
        Map<String, Object> pulverizerPointMap = redisTemplateUtil.hmget(CommonConstans.PULVERIZER_POINT_REDIS);

        List<PulverizerPointRedisVO> pulverizerPointRedisVOS = pulverizerPointMap.values().stream().map(
                obj -> {
                    PulverizerPointRedisVO pulverizerPointRedisVO = JSON.toJavaObject(JSON.parseObject(obj.toString()), PulverizerPointRedisVO.class);
                    return pulverizerPointRedisVO;
                }
        ).filter(pulverizerPointRedisVO -> pulverizerPointRedisVO.getEnable() == 1).collect(Collectors.toList());


        List<String> pulverizers = pulverizerPointRedisVOS.stream().map(pulverizerPointRedisVO -> pulverizerPointRedisVO.getPulverizerCode()).distinct().collect(Collectors.toList());

        for (int i = 0; i < pulverizers.size(); i++) {


            List<AggregationOperation> aggregationOperations = new ArrayList<>();
            aggregationOperations.add(Aggregation.match(Criteria.where("pulverizer_code").is(pulverizers.get(i)).and("date").is(date).and("hour").is(hour)));
            GroupOperation.GroupOperationBuilder first = Aggregation.group("hour")
                    .first("pulverizer_code").as("pulverizer_code")
                    .first("pulverizer_name");
            GroupOperation groupOperation = null;

            int finalI = i;
            List<String> nos = pulverizerPointRedisVOS.stream().filter(pulverizerPointRedisVO -> pulverizerPointRedisVO.getPulverizerCode().equals(pulverizers.get(finalI))).map(pulverizerPointRedisVO -> String.valueOf(pulverizerPointRedisVO.getNo())).collect(Collectors.toList());
            for (int j = 0; j < nos.size(); j++) {
                if (j == 0) {
                    first = first.as("pulverizer_name").avg("point.no" + nos.get(j));
                } else if (j == nos.size() - 1) {
                    groupOperation = first.as("no" + nos.get(j - 1)).avg("point.no" + nos.get(j)).as("no" + nos.get(j));
                } else {
                    first = first.as("no" + nos.get(j - 1)).avg("point.no" + nos.get(j));
                }

                if (nos.size() == 1) {
                    groupOperation = first.as("no" + nos.get(j));
                }
            }

            aggregationOperations.add(groupOperation);
            aggregationOperations.add(Aggregation.sort(Sort.Direction.ASC, "_id"));
            Aggregation aggregation = Aggregation.newAggregation(aggregationOperations);

            AggregationResults<Map> documentList = mongoTemplate.aggregate(aggregation, pointRunMongo, Map.class);
            List<Map> mappedResults = documentList.getMappedResults();

            PointHisHour pointHisHour = null;
            LinkedHashMap<String, Double> point = new LinkedHashMap<>();
            SimpleDateFormat dateHourformat = new SimpleDateFormat("yyyyMMddHH");

            if (mappedResults.size() != 1) {
                log.info(i + "号燃煤机" + hour + "小时没有数据");
                BaseDict baseDict = JSON.toJavaObject(JSON.parseObject(baseDictMap.get("pulverizer|" + pulverizers.get(i)).toString()), BaseDict.class);

                //生成id
                pointHisHour = PointHisHour.builder()
                        .id(pulverizers.get(i) + "-" + dateHourformat.format(time))
                        .date(date)
                        .time(time)
                        .hour(String.valueOf(hour))
                        .pulverizerName(baseDict.getValue())
                        .pulverizerCode(pulverizers.get(i))
                        .build();
                for (int j = 0; j < nos.size(); j++) {

                    String key = "no" + nos.get(j);
                    point.put(key, 0D);
                }
                pointHisHour.setPoint(point);
                mongoTemplate.save(pointHisHour);
                continue;
            }
            Map resultMap = mappedResults.get(0);
            log.info("resultMap:" + resultMap);
            pointHisHour = PointHisHour.builder()
                    .id(pulverizers.get(i) + "-" + dateHourformat.format(time))
                    .date(date)
                    .time(time)
                    .hour(String.valueOf(hour))
                    .pulverizerName(resultMap.get("pulverizer_name").toString())
                    .pulverizerCode(resultMap.get("pulverizer_code").toString())
                    .build();


            for (int j = 0; j < nos.size(); j++) {

                String key = "no" + nos.get(j);
                Object value = resultMap.get(key);
                point.put(key, Double.valueOf(value == null ? "0" : value.toString()));
            }
            pointHisHour.setPoint(point);

            mongoTemplate.save(pointHisHour);
        }
    }


}
