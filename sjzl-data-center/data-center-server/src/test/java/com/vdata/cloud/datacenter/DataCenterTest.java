package com.vdata.cloud.datacenter;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.vdata.cloud.common.exception.BusinessException;
import com.vdata.cloud.common.util.CommonUtil;
import com.vdata.cloud.datacenter.constants.CommonConstans;
import com.vdata.cloud.datacenter.entity.*;
import com.vdata.cloud.datacenter.mapper.*;
import com.vdata.cloud.datacenter.service.*;
import com.vdata.cloud.datacenter.task.GetDataTask;
import com.vdata.cloud.datacenter.util.GetPulverizerPointUtils;
import com.vdata.cloud.datacenter.util.RedisTemplateUtil;
import com.vdata.cloud.datacenter.vo.HisPointDataVO;
import com.vdata.cloud.datacenter.vo.PointVO;
import com.vdata.cloud.datacenter.vo.PulverizerPointRedisVO;
import com.vdata.cloud.datacenter.vo.QueryHistoryVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ProjectName: qsrd
 * @Package: com.vdata.cloud.datacenter
 * @ClassName: DataCenterTest
 * @Author: HK
 * @Description:
 * @Date: 2021/7/20 14:39
 * @Version: 1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class DataCenterTest {


    @Autowired
    private IBaseDictService baseDictService;


    @Autowired
    private GetPulverizerPointUtils getPulverizerPointUtils;

    @Autowired
    private ISyncPointService syncPointService;

    @Test

    public void getPointsTest() {


        List<SyncPoint> testPointAll = getPulverizerPointUtils.getTestPointAll();


        List<String> names = syncPointService.list(new LambdaQueryWrapper<SyncPoint>().select(SyncPoint::getName))
                .stream().map(syncPoint -> syncPoint.getName()).collect(Collectors.toList());


        List<SyncPoint> updates = testPointAll.stream().filter(syncPoint -> names.contains(syncPoint.getName())).collect(Collectors.toList());
        List<SyncPoint> saves = testPointAll.stream().filter(syncPoint -> !names.contains(syncPoint.getName())).collect(Collectors.toList());

        syncPointService.updateBatchById(updates);

        syncPointService.saveBatch(saves);

    }


    @Autowired
    private IPulverizerPointService pulverizerPointService;


    @Autowired
    private RedisTemplateUtil redisTemplateUtil;

    @Test
    public void listSaveRedisTest() {
        pulverizerPointService.listSaveRedis();

        Map<String, Object> map = redisTemplateUtil.hmget(CommonConstans.PULVERIZER_POINT_REDIS);

        map.values().stream().forEach(
                obj -> {
                    PulverizerPointRedisVO pulverizerPointRedisVO = JSON.toJavaObject(JSON.parseObject(obj.toString()), PulverizerPointRedisVO.class);
                    System.out.println(pulverizerPointRedisVO);
                }
        );
    }


    @Test
    public void getRTDataByBatchTest() {
  /*      List<String> strings = Arrays.asList("a", "b", "c", "d");
        List<Map<String, Object>> maps = getPulverizerPointUtils.getRealTimeDatas(strings);

        for (Map<String, Object> map : maps) {
            if (map != null) {
                for (String key : map.keySet()) {
                    System.out.print(key + ":" + map.get(key) + "\t");
                }
            } else {
                System.out.println("null");
            }

            System.out.println();
        }*/


        QueryHistoryVO queryHistoryVO = new QueryHistoryVO();
        queryHistoryVO.setTagName("hello");
        queryHistoryVO.setInterval(1);
        Date date = new Date();
        queryHistoryVO.setEdTime(date.getTime() / 1000);

        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.MONTH, -1);

        queryHistoryVO.setStTime(c.getTime().getTime() / 1000);

        List<Map<String, Object>> historyDatas = getPulverizerPointUtils.getHistoryDatas(queryHistoryVO);

        for (Map<String, Object> map : historyDatas) {
            if (map != null) {
                for (String key : map.keySet()) {
                    System.out.print(key + ":" + map.get(key) + "\t");
                }
                System.out.println();
            } else {
                System.out.println("null");
            }

        }
    }


    @Test
    public void test1() throws BusinessException {
        List<BaseDict> baseDictList = new ArrayList<>();
        baseDictList.add(new BaseDict("root", "pulverizer"));
        baseDictList.add(new BaseDict("root", "sensor_type"));
        baseDictList.add(new BaseDict("root", "test1"));
        baseDictService.existsIn(baseDictList);
    }

    /**
     * 报警信息创建
     */

    @Autowired
    private AlarmInformationMapper alarmInformationMapper;

    @Autowired
    private PulverizerPointMapper pulverizerPointMapper;
    @Autowired
    private BaseDictMapper baseDictMapper;


    @Autowired
    private AbnormalDetailMapper abnormalDetailMapper;


    @Autowired
    private OperationLogMapper operationLogMapper;

    @Autowired
    private IAlarmService alarmService;

    @Test
    public void execute() {
        for (int i = 0; i < 100; i++) {
            alarmCreate();
        }
    }


    @Test
    public void alarmCreate() {
        Random random = new Random();
        List<PulverizerPoint> ids = pulverizerPointMapper.selectList(new LambdaQueryWrapper<>()).stream()
//                .map(pulverizerPoint -> pulverizerPoint.getId())
                .collect(Collectors.toList());

        PulverizerPoint pulverizerPoint = ids.get(random.nextInt(ids.size()));
//        PulverizerPoint pulverizerPoint = ids.get(1);
        Date alarmTime = new Date();
        Integer pulverizerPointId = pulverizerPoint.getId();
        String sensorTypeCode = pulverizerPoint.getSensorTypeCode();
        String abnormalCode = random.nextInt(2) + 1 + "";
        String alarmDescription = "我报警了";


        BaseDict baseDict = baseDictMapper.selectOne(
                new LambdaQueryWrapper<BaseDict>()
                        .eq(BaseDict::getType, "sensor_type")
                        .eq(BaseDict::getCode, sensorTypeCode)
                        .isNull(BaseDict::getDeleteTime)
        );

        String detectionValue = random.nextInt(100) + baseDict.getDescription();
        alarmCreate(alarmTime, pulverizerPointId, abnormalCode, alarmDescription, detectionValue);


    }

    private void alarmCreate(Date alarmTime, Integer pulverizerPointId, String abnormalCode, String alarmDescription, String detectionValue) {
        //判断是否存在 当前磨煤机未处理的相同报警
        AlarmInformation alarmInformation = alarmService.exists(pulverizerPointId, abnormalCode);
        if (CommonUtil.isEmpty(alarmInformation)) {
            //写入报警表
            alarmInformation = insertAlarmInformation(alarmTime, pulverizerPointId, abnormalCode, alarmDescription, detectionValue);
            //写入异常明细表
            AbnormalDetail abnormalDetail = insertAbnormalDetail(alarmTime, pulverizerPointId, abnormalCode, alarmDescription, detectionValue, alarmInformation);
            //写入操作记录表
            insertOperationLog(alarmTime, alarmInformation);

            alarmService.pushAlarmInfo(alarmInformation, alarmTime);
        } else {
            log.info("alarmInformation:{}", alarmInformation.getId());
            alarmInformationMapper.augmentCount(alarmInformation.getId());
            AbnormalDetail abnormalDetail = insertAbnormalDetail(alarmTime, pulverizerPointId, abnormalCode, alarmDescription, detectionValue, alarmInformation);
            insertOperationLog(alarmTime, alarmInformation);
        }
    }

    private void insertOperationLog(Date alarmTime, AlarmInformation alarmInformation) {
        //插入操作记录表
        OperationLog operationLog = new OperationLog();
        operationLog.setAlarmInformationId(alarmInformation.getId());
        operationLog.setDateTime(alarmTime);
        operationLog.setDescription("发生" + baseDictService.getBaseDict("abnormal", alarmInformation.getAbnormalCode()).getValue());
        operationLog.setUserName("admin");
        operationLog.setNickName("未知");
        operationLogMapper.insert(operationLog);
    }

    private AbnormalDetail insertAbnormalDetail(Date alarmTime, Integer pulverizerPointId, String abnormalCode, String alarmDescription, String detectionValue, AlarmInformation alarmInformation) {
        //插入异常明细表
        AbnormalDetail abnormalDetail = new AbnormalDetail();
        abnormalDetail.setAbnormalCode(abnormalCode);
        abnormalDetail.setAlarmDescription(alarmDescription);
        abnormalDetail.setAlarmInformationId(alarmInformation.getId());
        abnormalDetail.setAlarmTime(alarmTime);
        abnormalDetail.setPulverizerPointId(pulverizerPointId);
        abnormalDetail.setDetectionValue(detectionValue);
        abnormalDetailMapper.insert(abnormalDetail);
        return abnormalDetail;
    }

    private AlarmInformation insertAlarmInformation(Date alarmTime, Integer pulverizerPointId, String abnormalCode, String alarmDescription, String detectionValue) {
        AlarmInformation alarmInformation;//插入报警信息表
        alarmInformation = new AlarmInformation();
        //报警时间
        alarmInformation.setAlarmTime(alarmTime);
        //报警类型代码
        alarmInformation.setAbnormalCode(abnormalCode);
        //磨煤机点位id
        alarmInformation.setPulverizerPointId(pulverizerPointId);
        //检测值
        alarmInformation.setDetectionValue(detectionValue);
        //报警次数
        alarmInformation.setCount(1);
        //报警描述
        alarmInformation.setAlarmDescription(alarmDescription);
        alarmInformationMapper.insert(alarmInformation);
        return alarmInformation;
    }


    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    public void redisTest() {

        while (true) {
            Map<String, Object> map = redisTemplateUtil.hmget(CommonConstans.PULVERIZER_POINT_REDIS);
            //获得启动的点位信息
            long count = map.values().stream().map(
                    obj -> {
                        PulverizerPointRedisVO pulverizerPointRedisVO = JSON.toJavaObject(JSON.parseObject(obj.toString()), PulverizerPointRedisVO.class);
                        return pulverizerPointRedisVO;
                    }
            ).filter(pulverizerPointRedisVO -> pulverizerPointRedisVO.getEnable() == 1).count();
            System.out.println("当前启动的点位数量为:" + count);
        }

    }

    @Test
    public void pointTest() {
        PulverizerPoint pulverizerPoint = new PulverizerPoint();

        pulverizerPoint.setId(39);
        pulverizerPoint.setNo(9);
        pulverizerPoint.setPulverizerCode("3");


        pulverizerPoint.setDcsDataIdentifier("CALC.LOGIC.RTALARM.9");
        pulverizerPoint.setEnable(1);
        pulverizerPointService.updatev1(pulverizerPoint);
        System.out.println();
    }

    @Autowired
    private GetDataTask getDataTask;


    @Test
    public void hisHourTest() throws ParseException {
        Calendar calendar = Calendar.getInstance();
//        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - 1);
        getDataTask.generateHour(calendar);
    }

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    public void mongoTest() throws ParseException {
        String no = "2";
        String pulverizerCode = "1";
        String time = "2021-11-18 15:31:04";
        Map<String, Object> dateMap = getDataTask.dateformat(time);
        double value = 999;
        PointRun pointRun = PointRun.builder()
                .id(pulverizerCode + "-" + dateMap.get("neatDateStr"))
                .date((Date) dateMap.get("date"))
                .time((Date) dateMap.get("time"))
                .pulverizerName("1号磨煤机")
                .hour((Integer) dateMap.get("hour"))
                .pulverizerCode(pulverizerCode)
                .dcsDataIdentifier(pulverizerCode)
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


    @Autowired
    private IPointRunService pointRunService;

    @Test
    public void getHisPointRunByDate() throws ParseException {
        SimpleDateFormat neatdateformat = new SimpleDateFormat("yyyyMMddHHmmss");

        Date startDate = neatdateformat.parse("20211122101837");

        pointRunService.getHisPointRunByDate(startDate, new Date(), "1", 4);

    }

    @Autowired
    private SyncPointMapper syncPointMapper;

    @Test
    //同步测点数据插入
    public void insertBatchSyncPoint() throws IOException {
        String pathname = "D:\\Desktop\\磨煤机数据";
        File rootFile = new File(pathname);
        for (File file : rootFile.listFiles()) {
            if (file.isDirectory()) {
                File[] freeFiles = file.listFiles();
                for (File freeFile : freeFiles) {
                    String name = freeFile.getName();
                    if (name.equals("测点信息.csv")) {
                        //获得到所有测点的全路径信息
                        String freeAbsolutePath = freeFile.getAbsolutePath();
                        Reader in = new FileReader(freeAbsolutePath);
                        Iterator<CSVRecord> iterator = CSVFormat.DEFAULT
//                                .withHeader(headers)
                                .withQuote(null)
                                .withFirstRecordAsHeader()
                                .parse(in)
                                .iterator();
                        List<SyncPoint> syncPoints = new ArrayList<>();
                        while (iterator.hasNext()) {
                            CSVRecord record = iterator.next();
                            SyncPoint syncPoint = new SyncPoint();
                            syncPoint.setName(record.get(0));
                            syncPoint.setDescp(record.get(1));
                            syncPoint.setUnit(record.get(2));
                            System.out.printf("3:%s", record.get(3));
                            System.out.printf("4:%s", record.get(4));
                            syncPoint.setMax(StringUtils.isNotEmpty(record.get(3)) ? BigDecimal.valueOf(Double.parseDouble(record.get(3))) : BigDecimal.valueOf(0));
                            syncPoint.setMin(StringUtils.isNotEmpty(record.get(4)) ? BigDecimal.valueOf(Double.parseDouble(record.get(4))) : BigDecimal.valueOf(0));

                            syncPoints.add(syncPoint);
                        }
                        List<String> names = syncPointService.list(new LambdaQueryWrapper<SyncPoint>().select(SyncPoint::getName))
                                .stream().map(syncPoint -> syncPoint.getName()).collect(Collectors.toList());


                        List<SyncPoint> updates = syncPoints.stream().filter(syncPoint -> names.contains(syncPoint.getName())).collect(Collectors.toList());
                        List<SyncPoint> saves = syncPoints.stream().filter(syncPoint -> !names.contains(syncPoint.getName())).collect(Collectors.toList());

                        if (!updates.isEmpty()) {
                            syncPointService.updateBatchById(updates);

                        }

                        if (!saves.isEmpty()) {
                            syncPointService.saveBatch(saves);

                        }

                    }
                }
            }
        }

    }


    //保存excel中的测点数据  20220105
    @Test
    public void savePoint() throws IOException {
        String freeAbsolutePath =  "D:\\Develop\\JetBrains\\project\\qsrd-main\\sjzl-data-center\\data-center-server\\src\\test\\java\\com\\vdata\\cloud\\datacenter\\repository\\11.txt";
        Reader in = new FileReader(freeAbsolutePath);
        Iterator<CSVRecord> iterator = CSVFormat.DEFAULT
//                                .withHeader(headers)
                .withQuote(null)
//                .withFirstRecordAsHeader()
                .parse(in)
                .iterator();

        Map<String,List<BaseObj>> map = new HashMap<>();

        while (iterator.hasNext()) {
            CSVRecord record = iterator.next();
            String value = record.get(0);
            String key = record.get(1);
            if(key.contains("A")){
                if(!map.containsKey("A")){
                    map.put("A",new ArrayList<BaseObj>());
                }
                BaseObj baseObj = new BaseObj();
                baseObj.key=key;
                baseObj.value=value;
                map.get("A").add(baseObj);
            }
            else if(key.contains("B")){
                if(!map.containsKey("B")){
                    map.put("B",new ArrayList<BaseObj>());
                }
                BaseObj baseObj = new BaseObj();
                baseObj.key=key;
                baseObj.value=value;
                map.get("B").add(baseObj);
            }
            else if(key.contains("C")){
                if(!map.containsKey("C")){
                    map.put("C",new ArrayList<BaseObj>());
                }
                BaseObj baseObj = new BaseObj();
                baseObj.key=key;
                baseObj.value=value;
                map.get("C").add(baseObj);
            }
            else if(key.contains("D")){
                if(!map.containsKey("D")){
                    map.put("D",new ArrayList<BaseObj>());
                }
                BaseObj baseObj = new BaseObj();
                baseObj.key=key;
                baseObj.value=value;
                map.get("D").add(baseObj);
            }
            else if(key.contains("E")){
                if(!map.containsKey("E")){
                    map.put("E",new ArrayList<BaseObj>());
                }
                BaseObj baseObj = new BaseObj();
                baseObj.key=key;
                baseObj.value=value;
                map.get("E").add(baseObj);
            }
            else {
                if(!map.containsKey("else")){
                    map.put("else",new ArrayList<BaseObj>());
                }
                BaseObj baseObj = new BaseObj();
                baseObj.key=key;
                baseObj.value=value;
                map.get("else").add(baseObj);
            }

        }

        List<PulverizerPoint>  pulverizerPoints = new ArrayList<>();
        for (int i = 0; i <5 ; i++) {
            String key = String.valueOf((char)('A' + i));
            List<BaseObj> baseObjs = map.get(key).stream().sorted((a,b)->a.key.compareTo(b.key)).collect(Collectors.toList());
            for (int i1 = 0; i1 < baseObjs.size(); i1++) {
                int no=i1+1;
                PulverizerPoint pulverizerPoint = new PulverizerPoint();
                pulverizerPoint.setPulverizerCode((i+1)+"");
                BaseObj baseObj = baseObjs.get(i1);

                pulverizerPoint.setPointName(baseObj.key);
                pulverizerPoint.setDcsDataIdentifier(baseObj.value);

                pulverizerPoint.setNo(no);
                pulverizerPoints.add(pulverizerPoint);
            }
        }

        List<BaseObj> elseObjs = map.get("else").stream().sorted((a, b) -> a.key.compareTo(b.key)).collect(Collectors.toList());
        for (int i = 0; i < elseObjs.size(); i++) {
            int no = i+100;
            for (int j = 0; j < 5; j++) {
                PulverizerPoint pulverizerPoint = new PulverizerPoint();
                pulverizerPoint.setPulverizerCode((j+1)+"");
                BaseObj baseObj = elseObjs.get(i);

                pulverizerPoint.setPointName(baseObj.key);
                pulverizerPoint.setDcsDataIdentifier(baseObj.value);

                pulverizerPoint.setNo(no);
                pulverizerPoints.add(pulverizerPoint);
            }

        }

    /*    for (PulverizerPoint pulverizerPoint : pulverizerPoints) {
            System.out.println(pulverizerPoint);
        }*/


//        System.out.println(pulverizerPoints.size());

        pulverizerPointService.saveBatch(pulverizerPoints);


//        pulverizerPointMapper.insert();
    }


    /*
     * 填充点位信息  20220107
     * */
    @Test
    public void savePoint1() throws IOException {
        String rootPath = "C:\\Users\\Administrator\\Desktop\\新建文件夹";

        File rootfile = new File(rootPath);
        File[] files = rootfile.listFiles();
        Map<String, PointObj> map = new HashMap<>();

        for (File file : files) {
            if (!file.isDirectory()) {
                String absolutePath = file.getAbsolutePath();
                Reader in = new FileReader(absolutePath);
                Iterator<CSVRecord> iterator = CSVFormat.DEFAULT
//                                .withHeader(headers)
                        .withQuote(null)
                        .withFirstRecordAsHeader()
                        .parse(in)
                        .iterator();
                while (iterator.hasNext()) {
                    CSVRecord next = iterator.next();
                    PointObj pointObj = new PointObj();
                    pointObj.dcs = next.get(0);
                    pointObj.unit = next.get(2);
                    pointObj.up = strTfBigDecimal(next.get(3));
                    pointObj.low = strTfBigDecimal(next.get(4));
                    map.put(pointObj.dcs, pointObj);
                }
            }
        }


        System.out.println(map);
        System.out.println(map);

        List<PulverizerPoint> pulverizerPoints = pulverizerPointMapper.selectList(null);

        List<PulverizerPoint> collect = pulverizerPoints.stream()
                .filter(pulverizerPoint -> map.containsKey(pulverizerPoint.getDcsDataIdentifier()))
                .map(pulverizerPoint -> {
                    PointObj pointObj = map.get(pulverizerPoint.getDcsDataIdentifier());

                    pulverizerPoint.setUnit(pointObj.unit);
                    pulverizerPoint.setUpperLimit(pointObj.up);
                    pulverizerPoint.setLowerLimit(pointObj.low);
                    return pulverizerPoint;
                }).collect(Collectors.toList());

        boolean b = pulverizerPointService.updateBatchById(collect);
        

    }


    BigDecimal strTfBigDecimal(String str) {
        if (StringUtils.isEmpty(str)) {
            return BigDecimal.valueOf(0);
        }

        return BigDecimal.valueOf(Double.valueOf(str));
    }

    class PointObj {
        String unit;
        String dcs;
        BigDecimal up;
        BigDecimal low;
    }

    @Test
    public void test18() {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            list.add(i);
        }
        List<List<Integer>> partition = ListUtils.partition(list, 10);

        List<Integer> addall = new ArrayList<>();
        for (int i = 0; i < partition.size(); i++) {
            addall.addAll(partition.get(i));
        }
        System.out.println(addall);

    }

    class BaseObj {
        String key;
        String value;
    }


    @Test
    //同步测点数据插入
    public void insertBatchSyncPoint1() throws IOException {
        String pathname = "D:\\Desktop\\磨煤机数据";
        File rootFile = new File(pathname);

        Set<String> descSet = new HashSet();
        Set<String> pointNameSet = new HashSet<>();
        for (File file : rootFile.listFiles()) {
            if (file.isDirectory()) {
                File[] freeFiles = file.listFiles();
                for (File freeFile : freeFiles) {
                    String name = freeFile.getName();
                    if (name.equals("测点信息.csv")) {
                        //获得到所有测点的全路径信息
                        String freeAbsolutePath = freeFile.getAbsolutePath();
                        Reader in = new FileReader(freeAbsolutePath);
                        Iterator<CSVRecord> iterator = CSVFormat.DEFAULT
//                                .withHeader(headers)
                                .withQuote(null)
                                .withFirstRecordAsHeader()
                                .parse(in)
                                .iterator();
                        List<SyncPoint> syncPoints = new ArrayList<>();
                        while (iterator.hasNext()) {
                            CSVRecord record = iterator.next();
                            String pointName = record.get(0);

                            String desc = record.get(1);
                            if (desc.contains("磨煤机")) {
                                descSet.add(desc);
                                pointNameSet.add(pointName);
                            }

                        }

                    }
                }
            }
        }

        System.out.printf("descSet:%d", descSet.size());
        System.out.printf("pointNameSet:%d", pointNameSet.size());

    }


    @Test
    //上下限保存
    public void insertBatchSyncPoint2() throws IOException {
        String pathname = "D:\\software\\work\\idea\\ideaProject\\PJ202107210905_QSRD\\doc\\8_技术文档\\数据采集\\磨煤机数据\\磨煤机数据";
        File rootFile = new File(pathname);

        Map<String, BigDecimal[]> map = new HashMap<>();
        for (File file : rootFile.listFiles()) {
            if (file.isDirectory()) {
                File[] freeFiles = file.listFiles();
                for (File freeFile : freeFiles) {
                    String name = freeFile.getName();
                    if (name.equals("测点信息.csv")) {
                        //获得到所有测点的全路径信息
                        String freeAbsolutePath = freeFile.getAbsolutePath();
                        Reader in = new FileReader(freeAbsolutePath);
                        Iterator<CSVRecord> iterator = CSVFormat.DEFAULT
//                                .withHeader(headers)
                                .withQuote(null)
                                .withFirstRecordAsHeader()
                                .parse(in)
                                .iterator();
                        List<SyncPoint> syncPoints = new ArrayList<>();
                        while (iterator.hasNext()) {
                            CSVRecord record = iterator.next();
                            String pointName = record.get(0);

                            String desc = record.get(1);
                            if (desc.contains("磨煤机")) {
                                BigDecimal up = record.get(3) != null ? BigDecimal.valueOf(Double.valueOf(record.get(3))) : BigDecimal.valueOf(0);
                                BigDecimal low = record.get(4) != null ? BigDecimal.valueOf(Double.valueOf(record.get(4))) : BigDecimal.valueOf(0);
                                BigDecimal[] bigDecimals = new BigDecimal[]{
                                        up, low
                                };

                                map.put(pointName, bigDecimals);
                            }
                        }
                    }
                }
            }
        }
        for (String key : map.keySet()) {
            BigDecimal[] bigDecimals = map.get(key);
            System.out.println("key:" + key + ":" + bigDecimals[0].doubleValue() + ":" + bigDecimals[1].doubleValue());

            PulverizerPoint pulverizerPoint = new PulverizerPoint();
            pulverizerPoint.setUpperLimit(bigDecimals[0]);
            pulverizerPoint.setLowerLimit(bigDecimals[1]);
            LambdaQueryWrapper<PulverizerPoint> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(PulverizerPoint::getDcsDataIdentifier, key);
            pulverizerPointMapper.update(pulverizerPoint, wrapper);

        }

    }


    @Test
    //保存磨煤机点位信息
    public void savePulverizerPointList() {

        LambdaQueryWrapper<SyncPoint> syncPointLambdaQueryWrapper = new LambdaQueryWrapper<>();

        syncPointLambdaQueryWrapper.like(SyncPoint::getDescp, "磨煤机");


        List<SyncPoint> list = syncPointService.list(syncPointLambdaQueryWrapper);


        List<PulverizerPoint> pulverizerPointList = new ArrayList();
        Map<String, Integer> noMap = new HashMap<>();
        for (SyncPoint syncPoint : list) {
            String descp = syncPoint.getDescp();
            PulverizerPoint pulverizerPoint = new PulverizerPoint();
            if (descp.contains("A")) {
                pulverizerPoint.setPulverizerCode("1");
                pulverizerPoint.setSensorTypeCode("temperature");
                pulverizerPoint.setPositionCode("1");

            } else if (descp.contains("B")) {
                pulverizerPoint.setPulverizerCode("2");
                pulverizerPoint.setSensorTypeCode("temperature");
                pulverizerPoint.setPositionCode("1");

            } else if (descp.contains("C")) {
                pulverizerPoint.setPulverizerCode("3");
                pulverizerPoint.setSensorTypeCode("temperature");
                pulverizerPoint.setPositionCode("2");

            } else if (descp.contains("D")) {
                pulverizerPoint.setPulverizerCode("4");
                pulverizerPoint.setSensorTypeCode("shake");
                pulverizerPoint.setPositionCode("3");

            } else if (descp.contains("E")) {
                pulverizerPoint.setPulverizerCode("5");
                pulverizerPoint.setSensorTypeCode("shake");
                pulverizerPoint.setPositionCode("3");
            } else {
                continue;
            }
            pulverizerPoint.setPointName(descp);
            pulverizerPoint.setTestItemCode("KN");
            pulverizerPoint.setUnit(syncPoint.getUnit());
            pulverizerPoint.setEnable(1);
            pulverizerPoint.setDcsDataIdentifier(syncPoint.getName());
            pulverizerPoint.setCreateUser("admin");
            pulverizerPoint.setUpdateUser("admin");

            if (noMap.get(pulverizerPoint.getPulverizerCode()) == null) {
                noMap.put(pulverizerPoint.getPulverizerCode(), 1);
            }

            pulverizerPoint.setNo(noMap.get(pulverizerPoint.getPulverizerCode()));

            noMap.put(pulverizerPoint.getPulverizerCode(), noMap.get(pulverizerPoint.getPulverizerCode()) + 1);

            pulverizerPointList.add(pulverizerPoint);


        }

        pulverizerPointService.saveBatch(pulverizerPointList);


    }


    @Test
    //保存导入的数据
    public void savePointRunMongoDB() throws IOException, ParseException {

        Map<String, Object> baseDictMap = redisTemplateUtil.hmget(CommonConstans.BASE_DICT_REDIS);
        Map<String, Object> pulverizerPointMap = redisTemplateUtil.hmget(CommonConstans.PULVERIZER_POINT_REDIS);

        Map<String, Integer> pointDcsMap = pulverizerPointMap.values().stream().map(
                obj -> {
                    PulverizerPointRedisVO pulverizerPointRedisVO = JSON.toJavaObject(JSON.parseObject(obj.toString()), PulverizerPointRedisVO.class);
                    return pulverizerPointRedisVO;
                }
        ).filter(pulverizerPointRedisVO -> pulverizerPointRedisVO.getEnable() == 1).collect(Collectors.toMap(key -> key.getPulverizerCode() + "|" + key.getDcsDataIdentifier(), value -> value.getNo()));

        Map<String, Integer> map = new HashMap<>();
        String pathname = "D:\\Desktop\\磨煤机数据";
        File rootFile = new File(pathname);
        for (File file : rootFile.listFiles()) {
            if (file.isDirectory()) {
                String rootName = file.getName();
                String pulverizerCode = "";
                if (rootName.contains("磨煤机A")) {
                    pulverizerCode = "1";
                } else if (rootName.contains("磨煤机B")) {
                    pulverizerCode = "2";
                } else if (rootName.contains("磨煤机C")) {
                    pulverizerCode = "3";
                } else if (rootName.contains("磨煤机D")) {
                    pulverizerCode = "4";
                } else if (rootName.contains("磨煤机E")) {
                    pulverizerCode = "5";
                }

                File[] freeFiles = file.listFiles();
                List<PointRun> pointRunList = new ArrayList<>();
                for (File freeFile : freeFiles) {

                    String name = freeFile.getName();
                    if (!name.equals("测点信息.csv")) {
                        //获得到所有测点的全路径信息
                        String freeAbsolutePath = freeFile.getAbsolutePath();
                        Reader in = new FileReader(freeAbsolutePath);
                        List<CSVRecord> records = CSVFormat.DEFAULT
//                                .withHeader(headers)
                                .withQuote(null)
//                                .withFirstRecordAsHeader()
                                .parse(in)
                                .getRecords();


                        Iterator<String> iterator = records.get(1).iterator();
                        int rowN = 1;
                        Map<Integer, String> pointNameMap = new HashMap<>();
                        while (iterator.hasNext()) {
                            String pointName = iterator.next();
                            if (!pointName.equals("点名")) {
                                pointNameMap.put(rowN++, pointName);
                            }
                        }

                        for (int i = 3; i < records.size(); i++) {
                            CSVRecord record = records.get(i);

                            String dateStr = record.get(0);
                            Map<String, Object> dateMap = getDataTask.dateformat(dateStr);
                            BaseDict baseDict = JSON.toJavaObject(JSON.parseObject(baseDictMap.get("pulverizer|" + pulverizerCode).toString()), BaseDict.class);

                            PointRun pointRun = PointRun.builder()
                                    .id(pulverizerCode + "-" + dateMap.get("neatDateStr"))
                                    .date((Date) dateMap.get("date"))
                                    .time((Date) dateMap.get("time"))
                                    .pulverizerName(baseDict.getValue())
                                    .hour((Integer) dateMap.get("hour"))
                                    .pulverizerCode(pulverizerCode)
                                    .build();
                            LinkedHashMap<String, Double> linkedHashMap = new LinkedHashMap<>();
                            for (int l = 1; l < record.size(); l++) {
                                String no = "no" + pointDcsMap.get(pulverizerCode + "|" + pointNameMap.get(l));
                                linkedHashMap.put(no, Double.valueOf(StringUtils.isEmpty(record.get(l)) ? "0" : record.get(l)));
                            }
                            pointRun.setPoint(linkedHashMap);
                            mongoTemplate.save(pointRun);
                        }
                    }
                }

            }
        }


    }


    @Test
    //保存导入的数据
    public void savePointRunMongoDBCount() throws IOException, ParseException {


        Map<String, Object> baseDictMap = redisTemplateUtil.hmget(CommonConstans.BASE_DICT_REDIS);


        Map<String, Integer> map = new HashMap<>();
        String pathname = "D:\\Desktop\\磨煤机数据";
        File rootFile = new File(pathname);
        for (File file : rootFile.listFiles()) {
            if (file.isDirectory()) {
                String rootName = file.getName();
                String pulverizerCode = "";
                if (rootName.contains("磨煤机A")) {
                    pulverizerCode = "1";
                } else if (rootName.contains("磨煤机B")) {
                    pulverizerCode = "2";
                } else if (rootName.contains("磨煤机C")) {
                    pulverizerCode = "3";
                } else if (rootName.contains("磨煤机D")) {
                    pulverizerCode = "4";
                } else if (rootName.contains("磨煤机E")) {
                    pulverizerCode = "5";
                }

                File[] freeFiles = file.listFiles();
                List<PointRun> pointRunList = new ArrayList<>();
                for (File freeFile : freeFiles) {

                    String name = freeFile.getName();
                    if (!name.equals("测点信息.csv")) {
                        //获得到所有测点的全路径信息
                        String freeAbsolutePath = freeFile.getAbsolutePath();
                        Reader in = new FileReader(freeAbsolutePath);
                        List<CSVRecord> records = CSVFormat.DEFAULT
//                                .withHeader(headers)
                                .withQuote(null)
//                                .withFirstRecordAsHeader()
                                .parse(in)
                                .getRecords();


                        Iterator<String> iterator = records.get(1).iterator();
                        int rowN = 1;
                        Map<Integer, String> pointNameMap = new HashMap<>();
                        while (iterator.hasNext()) {
                            String pointName = iterator.next();
                            if (!pointName.equals("点名")) {
                                pointNameMap.put(rowN++, pointName);
                            }
                        }

                        for (int i = 3; i < records.size(); i++) {
                            CSVRecord record = records.get(i);

                            String dateStr = record.get(0);
                            Map<String, Object> dateMap = getDataTask.dateformat(dateStr);
                            BaseDict baseDict = JSON.toJavaObject(JSON.parseObject(baseDictMap.get("pulverizer|" + pulverizerCode).toString()), BaseDict.class);

                            PointRun pointRun = PointRun.builder()
                                    .id(pulverizerCode + "-" + dateMap.get("neatDateStr"))
                                    .date((Date) dateMap.get("date"))
                                    .time((Date) dateMap.get("time"))
                                    .pulverizerName(baseDict.getValue())
                                    .hour((Integer) dateMap.get("hour"))
                                    .pulverizerCode(pulverizerCode)
                                    .build();
                            if (!map.containsKey(pointRun.getId())) {
                                map.put(pointRun.getId(), 1);
                            }
                            map.put(pointRun.getId(), map.get(pointRun.getId()) + 1);
                        }
                    }
                }

            }
        }
        HashMap<String, Integer> finalOut = new LinkedHashMap<>();

        map.entrySet()
                .stream()
                .sorted((p1, p2) -> -p2.getValue().compareTo(p1.getValue()))
                .collect(Collectors.toList()).forEach(ele -> finalOut.put(ele.getKey(), ele.getValue()));

        int count = 0;
        for (String key : finalOut.keySet()) {
            Integer integer = finalOut.get(key);
            if (integer > 1) {
                System.out.println(key + ":" + integer);
                count++;
            }
        }
        System.out.println("总数量为：" + count);


    }

    @Test
    public void batchDatasTset() throws ParseException {
//        getDataTask.getBatchDatas();

//        getDataTask.getBatchDatas();
        pointRunService.datafilling();

    }


    @Test
    public void redisMax() throws ParseException {

        //end 144316
        //start 140000
        List<PointVO> pointVOS = pulverizerPointService.list(null)
                .stream().map(pulverizerPoint -> {
                    PointVO pointVO = new PointVO();
                    pointVO.setNo(pulverizerPoint.getNo());
                    pointVO.setPulverizerCode(pulverizerPoint.getPulverizerCode());
                    return pointVO;
                }).collect(Collectors.toList());

        HisPointDataVO hisPointDataVO = new HisPointDataVO();
        hisPointDataVO.setStartDate("2021-11-30 14:30:00");
        hisPointDataVO.setEndDate("2021-11-30 14:37:17");
        hisPointDataVO.setPointVOs(pointVOS);
        pointRunService.pullHisPointData(hisPointDataVO);

    }


    @Test
    public void redisMax1() throws ParseException {

        //end 144316
        //start 140000
        List<PointVO> pointVOS = pulverizerPointService.list(null)
                .stream().map(pulverizerPoint -> {
                    PointVO pointVO = new PointVO();
                    pointVO.setNo(pulverizerPoint.getNo());
                    pointVO.setPulverizerCode(pulverizerPoint.getPulverizerCode());
                    return pointVO;
                }).collect(Collectors.toList());


        HisPointDataVO hisPointDataVO = new HisPointDataVO();
        hisPointDataVO.setStartDate("2021-11-30 10:00:00");
        hisPointDataVO.setEndDate("2021-12-01 11:00:00");
        hisPointDataVO.setPointVOs(pointVOS);
        pointRunService.pullHisPointData(hisPointDataVO);

    }


    @Test
    public void hourstat() throws ParseException {

      /*  Date endDate = new Date();
        Date startDate = new Date(endDate.getTime() - (1000 * 24 * 60 * 60));

        pointRunService.hourStat(startDate.getTime() / 1000, endDate.getTime() / 1000);*/
    /*    getDataTask.getDatas();


        String baseUrl = getPulverizerPointUtils.getBaseUrl();
        System.out.println(baseUrl);*/

        getDataTask.getBatchDatas();
    }


}