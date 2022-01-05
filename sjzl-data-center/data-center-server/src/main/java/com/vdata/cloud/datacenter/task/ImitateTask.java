package com.vdata.cloud.datacenter.task;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.vdata.cloud.common.constant.Constants;
import com.vdata.cloud.common.vo.DataResult;
import com.vdata.cloud.datacenter.constants.CommonConstans;
import com.vdata.cloud.datacenter.entity.BaseDict;
import com.vdata.cloud.datacenter.entity.PointHisHour;
import com.vdata.cloud.datacenter.entity.PointRun;
import com.vdata.cloud.datacenter.entity.PulverizerRunning;
import com.vdata.cloud.datacenter.mapper.PulverizerRunningMapper;
import com.vdata.cloud.datacenter.util.RedisTemplateUtil;
import com.vdata.cloud.datacenter.vo.PointRunTimeVO;
import com.vdata.cloud.datacenter.vo.PulverizerPointRedisVO;
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
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Integer.valueOf;
import static java.math.BigDecimal.ROUND_HALF_UP;

/**
 * @ProjectName: sjzl-master
 * @Package: com.vdata.cloud.datacenter.task
 * @ClassName: ImitateTask
 * @Author: LF
 * @Description:模拟传感器数据
 * @Date: 2021/07/22 13:39
 * @Version: 1.0
 */
@Log4j2
@Component
@EnableScheduling
public class ImitateTask {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private SimpMessagingTemplate wsTemplate;

    @Autowired
    private RedisTemplateUtil redisTemplateUtil;

    @Autowired
    private PulverizerRunningMapper pulverizerRunningMapper;


    /*磨煤机点位以及磨煤机存reds缓存*/
//    @Scheduled(cron = "*/1 * * * * ?")//每秒执行一次
    public void runData() throws Exception {
        Random r = new Random();
        Calendar calendar = Calendar.getInstance();


        Date time = new Date();
        Map<String, Object> baseDictMap = redisTemplateUtil.hmget(CommonConstans.BASE_DICT_REDIS);
        Map<String, Object> pulverizerPointMap = redisTemplateUtil.hmget(CommonConstans.PULVERIZER_POINT_REDIS);
        baseDictMap.keySet().stream()
                .filter(str -> str.split("\\|")[0].equals("pulverizer"))
                .map(str -> str.split("\\|")[1])
                .collect(Collectors.toList())
                .forEach(
                        pulverizerCode -> {
                            calendar.setTime(time);
                            BaseDict baseDict = JSON.toJavaObject(JSON.parseObject(baseDictMap.get("pulverizer|" + pulverizerCode).toString()), BaseDict.class);
                            PointRun pointRun = PointRun.builder()
                                    .id(String.valueOf(System.currentTimeMillis()))
                                    .time(time)
                                    .pulverizerCode(pulverizerCode)
                                    .pulverizerName(baseDict.getValue())
                                    .hour(calendar.get(Calendar.HOUR_OF_DAY))
                                    .build();
                            calendar.set(Calendar.HOUR_OF_DAY, 0);
                            calendar.set(Calendar.MINUTE, 0);
                            calendar.set(Calendar.SECOND, 0);
                            calendar.set(Calendar.MILLISECOND, 0);
                            Date date = calendar.getTime();
                            pointRun.setDate(date);

                            LinkedHashMap<String, Double> point = new LinkedHashMap<>();
                            String prefix = "no";

                            List<String> nos = pulverizerPointMap.keySet().stream()
                                    .filter(str -> str.split("\\|")[0].equals(pulverizerCode))
                                    .map(str -> str.split("\\|")[1])
                                    .sorted(Comparator.comparing(s -> valueOf(s)))
                                    .collect(Collectors.toList());
                            for (int i = 0; i < nos.size(); i++) {
                                point.put(prefix + nos.get(i), randomValue(r));
                            }

                            pointRun.setPoint(point);


                           /* for (int i = 0; i < nos.size(); i++) {
                                point.put(prefix + nos.get(i), 0D);

                            }*/

                            pullWebSocket(pointRun);

                            mongoTemplate.save(pointRun);
                        }
                );


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

        result.setData(list);
        result.setCode(Constants.RETURN_NORMAL);
        result.setMessage("获取磨煤机运行数据成功");

        log.info("【推送消息】开始执行：{}", DateUtil.formatDateTime(new Date()));
        log.info(result);
        wsTemplate.convertAndSend("/topic/" + CommonConstans.PULVERIZER_WEBSOCKET + "/" + pointRun.getPulverizerCode(), result);
        log.info("【推送消息】执行结束：{}", DateUtil.formatDateTime(new Date()));
    }


    private double randomValue(Random r) {
        float v = r.nextFloat() * 100;
        return BigDecimal.valueOf(v).setScale(3, ROUND_HALF_UP).doubleValue();
    }

    private BigDecimal randomValue_(Random r) {
        float v = r.nextFloat() * 100;
        return BigDecimal.valueOf(v).setScale(3, ROUND_HALF_UP);
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

        log.info(startTimeStr);
        log.info(dateFormat.parse(startTimeStr).toString());
        log.info(endTimeStr);
        log.info(dateFormat.parse(endTimeStr).toString());


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

    SimpleDateFormat dateHourformat = new SimpleDateFormat("yyyyMMddhh");

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
        List<String> pulverizers = baseDictMap.keySet().stream()
                .filter(str -> str.split("\\|")[0].equals("pulverizer"))
                .map(str -> str.split("\\|")[1])
                .collect(Collectors.toList());

        for (int i = 0; i < pulverizers.size(); i++) {


            List<AggregationOperation> aggregationOperations = new ArrayList<>();
            aggregationOperations.add(Aggregation.match(Criteria.where("pulverizer_code").is(pulverizers.get(i)).and("date").is(date).and("hour").is(hour)));
            GroupOperation.GroupOperationBuilder first = Aggregation.group("hour")
                    .first("pulverizer_code").as("pulverizer_code")
                    .first("pulverizer_name");
            GroupOperation groupOperation = null;

            int finalI = i;
            List<String> nos = pulverizerPointMap.keySet().stream()
                    .filter(str -> str.split("\\|")[0].equals(pulverizers.get(finalI)))
                    .map(str -> str.split("\\|")[1])
                    .sorted(Comparator.comparing(s -> valueOf(s)))
                    .collect(Collectors.toList());
            for (int j = 0; j < nos.size(); j++) {
                if (j == 0) {
                    first = first.as("pulverizer_name").avg("point.no" + nos.get(j));
                } else if (j == nos.size() - 1) {
                    groupOperation = first.as("no" + nos.get(j - 1)).avg("point.no" + nos.get(j)).as("no" + nos.get(j));
                } else {
                    first = first.as("no" + nos.get(j - 1)).avg("point.no" + nos.get(j));
                }
            }
            aggregationOperations.add(groupOperation);
            aggregationOperations.add(Aggregation.sort(Sort.Direction.ASC, "_id"));
            Aggregation aggregation = Aggregation.newAggregation(aggregationOperations);

            AggregationResults<Map> documentList = mongoTemplate.aggregate(aggregation, "point_run2", Map.class);
            List<Map> mappedResults = documentList.getMappedResults();

            PointHisHour pointHisHour = null;
            LinkedHashMap<String, Double> point = new LinkedHashMap<>();
            if (mappedResults.size() != 1) {
                log.info(i + "号燃煤机" + hour + "小时没有数据");
                BaseDict baseDict = JSON.toJavaObject(JSON.parseObject(baseDictMap.get("pulverizer|" + pulverizers.get(i)).toString()), BaseDict.class);
                pointHisHour = PointHisHour.builder()
                        .id(pulverizers.get(i) + dateHourformat.format(time))
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
                    .id(pulverizers.get(i) + dateHourformat.format(time))
                    .date(date)
                    .time(time)
                    .hour(String.valueOf(hour))
                    .pulverizerName(resultMap.get("pulverizer_name").toString())
                    .pulverizerCode(resultMap.get("pulverizer_code").toString())
                    .build();


            for (int j = 0; j < nos.size(); j++) {

                String key = "no" + nos.get(j);
                Object value = resultMap.get(key);
                point.put(key, Double.valueOf(value.toString()));
            }
            pointHisHour.setPoint(point);

            mongoTemplate.save(pointHisHour);
        }
    }


    private double round3(Object value) {
        return BigDecimal.valueOf(Double.valueOf(value.toString())).setScale(3, ROUND_HALF_UP).doubleValue();
    }


    /**
     * 每天一点统计持续时间
     */
//    @Scheduled(cron = "* 1 0 * * ?  ")//每秒执行一次
    public void runTimeCount() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        Date date = calendar.getTime();
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("date").is(date)),
                Aggregation.group("pulverizer_code").count().as("count")
        );
        AggregationResults<Map> aggregate = mongoTemplate.aggregate(aggregation, PointRun.class, Map.class);
        List<Map> mappedResults = aggregate.getMappedResults();

        Map<String, Integer> map = new HashMap<>();
        for (Map mappedResult : mappedResults) {
            String pulverizerCode = mappedResult.get("_id").toString();
            Integer count = valueOf(mappedResult.get("count").toString());
            map.put(pulverizerCode, count);
        }
        for (int i = 1; i <= 5; i++) {
            if (!map.containsKey(String.valueOf(i))) {
                pulverizerRunningMapper.zero(String.valueOf(i));
            } else {
                pulverizerRunningMapper.addDay(String.valueOf(i));
            }
        }
        List<PulverizerRunning> pulverizerRunnings = pulverizerRunningMapper.selectList(null);
        List<String> jsons = pulverizerRunnings.stream().map(pulverizerRunning -> {
            String json = JSON.toJSON(pulverizerRunning).toString();
            return json;
        }).collect(Collectors.toList());

        redisTemplateUtil.lSet(CommonConstans.PULVERIZER_RUNNING_LIST_REDIS, (List) jsons);


    }


}
