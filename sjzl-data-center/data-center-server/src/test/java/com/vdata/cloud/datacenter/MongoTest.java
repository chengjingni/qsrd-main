package com.vdata.cloud.datacenter;

import com.alibaba.fastjson.JSON;
import com.vdata.cloud.common.util.UUIDUtils;
import com.vdata.cloud.datacenter.constants.CommonConstans;
import com.vdata.cloud.datacenter.entity.PointRun;
import com.vdata.cloud.datacenter.entity.PulverizerRunning;
import com.vdata.cloud.datacenter.mapper.PulverizerPointMapper;
import com.vdata.cloud.datacenter.mapper.PulverizerRunningMapper;
import com.vdata.cloud.datacenter.repository.SysOperationLogRepository;
import com.vdata.cloud.datacenter.service.IPulverizerPointService;
import com.vdata.cloud.datacenter.task.ImitateTask;
import com.vdata.cloud.datacenter.util.RedisTemplateUtil;
import com.vdata.cloud.datacenter.vo.PulverizerPointRedisVO;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static java.math.BigDecimal.ROUND_HALF_UP;

/**
 * @ProjectName: sjzl-master
 * @Package: com.vdata.cloud.datacenter
 * @ClassName: MongoTest
 * @Author: HK
 * @Description:
 * @Date: 2021/7/26 17:09
 * @Version: 1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class MongoTest {

    @Autowired
    private SysOperationLogRepository sysOperationLogRepository;

    @Autowired
    private MongoTemplate mongoTemplate;


    @Test
    public void testSave() {

        List<AggregationOperation> list = new ArrayList<>();


        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("pulverizer_code").is("1").and("hour").is(14)),
                Aggregation.group("hour")
                        .first("pulverizerCode").as("pulverizer_code")
                        .first("pulverizerCode").as("pulverizer_name")
                        .avg("point.no1").as("no1")
                        .avg("point.no2").as("no2")
                        .avg("point.no2").as("no3")
                        .avg("point.no2").as("no4")
                        .avg("point.no2").as("no5")
                        .avg("point.no2").as("no6")
                        .avg("point.no2").as("no7")
                        .avg("point.no2").as("no8")
                        .avg("point.no2").as("no9")
                        .avg("point.no2").as("no10")
                        .avg("point.no2").as("no11")
                        .avg("point.no2").as("no12")
                        .avg("point.no2").as("no13")
        );

        AggregationResults<HashMap> documentList = mongoTemplate.aggregate(aggregation, PointRun.class, HashMap.class);

        for (HashMap mappedResult : documentList.getMappedResults()) {
            for (Object o : mappedResult.keySet()) {
                System.out.println(o + ":" + mappedResult.get(o) + "\t");
            }
            System.out.println();
        }
    }

    @Test
    public void testSave2() {

        Query query = new Query();
        query.fields().include("point.no1");
        List<HashMap> point_run1 = mongoTemplate.find(query, HashMap.class, "point_run1");
        for (HashMap mappedResult : point_run1) {
            for (Object key : mappedResult.keySet()) {
                System.out.println(key + ":" + mappedResult.get(key) + "\t");
            }
            System.out.println();

        }
    }


    @Autowired
    private PulverizerPointMapper pulverizerPointMapper;
    @Autowired
    private IPulverizerPointService pulverizerPointService;

    @Autowired
    private RedisTemplateUtil redisTemplateUtil;

    @Test
    public void test4() {
        pulverizerPointService.listSaveRedis();


        Map<String, Object> hmget = redisTemplateUtil.hmget(CommonConstans.PULVERIZER_POINT_REDIS);
        for (String s : hmget.keySet()) {
            System.out.println(s + "+" + JSON.toJavaObject(JSON.parseObject(hmget.get(s).toString()), PulverizerPointRedisVO.class));
        }
    }

    @Autowired
    private PulverizerRunningMapper pulverizerRunningMapper;


    @Test
    public void test5() {
        Map<String, Integer> map = new HashMap<>();

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


    @Test
    public void test6() {
        List list = redisTemplateUtil.lGetRange(CommonConstans.PULVERIZER_RUNNING_LIST_REDIS, 0, 4);
        for (Object o : list) {
            System.out.println(o.toString());
        }


    }


    @Test
    public void test7() {
        String prefix = "no";
        Random random = new Random();
        List<Map<String, Double>> mapList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Map<String, Double> stringDoubleMap = new LinkedHashMap<>();
            stringDoubleMap.put(prefix + i, randomValue(random));
            mapList.add(stringDoubleMap);

        }

        Map<String, Object> map = new HashMap<>();
        map.put("arr", mapList);
        map.put("index", random.nextInt(9));
        System.out.println(JSON.toJSON(map).toString());
        mongoTemplate.save(map, "test");
    }

    @Test
    public void test7_1() {
        String prefix = "no";
        Random random = new Random();
        Map<String, Double> stringDoubleMap = new LinkedHashMap<>();
        for (int i = 0; i < 100; i++) {
            stringDoubleMap.put(prefix + i, randomValue(random));

        }
        Map<String, Object> map = new HashMap<>();
        map.put("arr", stringDoubleMap);
        map.put("index", random.nextInt(9));
        map.put("_id", UUIDUtils.generateShortUuid());
        System.out.println(JSON.toJSON(map).toString());
        mongoTemplate.save(map, "test1");
    }

    @Test
    public void test7_Main() {
        for (int i = 0; i < 100; i++) {
            test7_1();
        }
    }


    @Test
    public void test8() {
        GroupOperation.GroupOperationBuilder index = Aggregation.group("index").avg("arr.no2");
        GroupOperation.GroupOperationBuilder no2 = index.as("no2").avg("arr.no3");


        GroupOperation.GroupOperationBuilder no3 = no2.as("no3").avg("arr.no4");
        GroupOperation no4 = no3.as("no4");


        GroupOperation.GroupOperationBuilder first = Aggregation.group("index").sum("no0");
        GroupOperation groupOperation = null;
        String prefiex = "no";
        for (int i = 1; i < 100; i++) {
            if (i == 1) {
                first = first.as("no0").sum("arr." + prefiex + i);
            } else if (i == 99) {
                groupOperation = first.as(prefiex + (i - 1)).sum("arr." + prefiex + i).as(prefiex + i);
            } else {
                first = first.as(prefiex + (i - 1)).sum("arr." + prefiex + i);
            }

        }
//        GroupOperation group = Aggregation.group();

        Aggregation aggregation = Aggregation.newAggregation(
                groupOperation,
//                no3,
                Aggregation.sort(Sort.Direction.ASC, "_id")
        );
        AggregationResults<Map> test = mongoTemplate.aggregate(aggregation, "test1", Map.class);
        List<Map> mappedResults = test.getMappedResults();
        for (Map mappedResult : mappedResults) {
            for (Object key : mappedResult.keySet()) {

                System.out.println("key:" + key + "    value:" + mappedResult.get(key));
            }
            System.out.println("----------------------------");
        }
    }


    private double randomValue(Random r) {
        float v = r.nextFloat() * 100;
        return BigDecimal.valueOf(v).setScale(3, ROUND_HALF_UP).doubleValue();
    }


    @Autowired
    private ImitateTask imitateTask;

    @Test
    public void runDataTest() throws Exception {
     /*   for (int i = 0; i < 20; i++) {
            imitateTask.runData();
            Thread.sleep(1000);
        }
*/
//        imitateTask.statisticsData();


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
            imitateTask.generateHour(c);
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


    @Test
    public void saveRedis() {
        pulverizerPointService.listSaveRedis();
    }

}
