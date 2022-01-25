package com.vdata.cloud.datacenter.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.vdata.cloud.common.constant.Constants;
import com.vdata.cloud.common.exception.BusinessException;
import com.vdata.cloud.common.util.CommonUtil;
import com.vdata.cloud.common.vo.DataResult;
import com.vdata.cloud.common.vo.UserVO;
import com.vdata.cloud.datacenter.constants.CommonConstans;
import com.vdata.cloud.datacenter.entity.*;
import com.vdata.cloud.datacenter.enums.StatusTypeEnum;
import com.vdata.cloud.datacenter.mapper.AbnormalDetailMapper;
import com.vdata.cloud.datacenter.mapper.AlarmInformationMapper;
import com.vdata.cloud.datacenter.mapper.OperationLogMapper;
import com.vdata.cloud.datacenter.mapper.PulverizerPointMapper;
import com.vdata.cloud.datacenter.service.IAlarmService;
import com.vdata.cloud.datacenter.service.IBaseDictService;
import com.vdata.cloud.datacenter.util.RedisTemplateUtil;
import com.vdata.cloud.datacenter.vo.*;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ProjectName: sjzl-master
 * @Package: com.vdata.cloud.datacenter.service.impl
 * @ClassName: AlarmServiceImpl
 * @Author: HK
 * @Description:
 * @Date: 2021/7/21 15:04
 * @Version: 1.0
 */
@Log4j2
@Service
public class AlarmServiceImpl implements IAlarmService {

    @Autowired
    private AlarmInformationMapper alarmInformationMapper;

    @Autowired
    private AbnormalDetailMapper abnormalDetailMapper;

    @Autowired
    private OperationLogMapper operationLogMapper;


    @Autowired
    private IBaseDictService baseDictService;

    @Autowired
    private RedisTemplateUtil redisTemplateUtil;


    @Autowired
    private SimpMessagingTemplate wsTemplate;

    @Autowired
    private PulverizerPointMapper pulverizerPointMapper;

    @Override

    public IPage<AlarmInformationVO> page(AlarmPageVO alarmPageVO) throws BusinessException {
        String where = "";
        switch (alarmPageVO.getType()) {
            //查询所有
            case 0:
                break;
            //今日报警
            case 1:
                where = " DATE_FORMAT(CURRENT_TIMESTAMP,'%Y%m%d') = DATE_FORMAT(alarm_time,'%Y%m%d') ";
                break;
            //待处理
            case 2:
//                where = "WHERE result_status<3 or result_status IS NULL";
                where = " (result_status<3 OR  result_status IS NULL)  AND (verify_result=2 OR verify_result IS NULL)";
                break;
            //处理中报警
            case 3:
                where = " result_status=3";
                break;
            case 4:
                //报警总数 (待处理和处理中)
                where = " (result_status<=3 OR  result_status IS NULL)  AND (verify_result=2 OR verify_result IS NULL)";
                break;
            default:
                throw new BusinessException("不存在此类型");
        }
        long page = alarmPageVO.getPage();
        long size = alarmPageVO.getSize();
        IPage<AlarmInformationVO> alarmInformationIPage = new Page<AlarmInformationVO>(page == 0 ? 1 : page, size == 0 ? 10 : size);
        return alarmInformationMapper.page(alarmInformationIPage, where, alarmPageVO);
    }

    @Override
    public AlarmInformation exists(Integer pulverizerPointId, String abnormalCode) {
        return alarmInformationMapper.selectOne(
                new LambdaQueryWrapper<AlarmInformation>()
                        .eq(AlarmInformation::getPulverizerPointId, pulverizerPointId)
                        .eq(AlarmInformation::getAbnormalCode, abnormalCode)
                        //存在没有处理完成则创建
                        .and(e -> e.ne(AlarmInformation::getResultStatus, StatusTypeEnum.PROCESSED.value()).or().isNull(AlarmInformation::getResultStatus))
        );
    }


    @Override
    @Transactional
    public void manage(AlarmInformation iAlarmInformation, UserVO user) {
        Integer resultStatus = iAlarmInformation.getResultStatus();


        if (resultStatus == null || resultStatus < 1) {
            throw new BusinessException("传入数据有误");
        }
        Integer verifyResult = iAlarmInformation.getVerifyResult();
        String description = iAlarmInformation.getProcessDescription();

        Integer id = iAlarmInformation.getId();
        AlarmInformation selectAlarmInformation = alarmInformationMapper.selectById(id);
        if (CommonUtil.isEmpty(selectAlarmInformation)) {
            throw new BusinessException("不存在该报警");
        }

        AlarmInformation alarmInformation = new AlarmInformation();
        alarmInformation.setId(iAlarmInformation.getId());
        alarmInformation.setPulverizerPointId(selectAlarmInformation.getPulverizerPointId());

        Date finishDate = new Date();
        //除了核实中其他都有描述
        //核实中
        alarmInformation.setResultStatus(resultStatus);
//        alarmInformation
        alarmInformation.setVerifyResult(verifyResult);


  /*      if (CommonUtil.isNotEmpty(verifyResult) && verifyResult == VerifyResultEnum.TO_VERIFY_THE.value()) {
            alarmInformation.setResultStatus(StatusTypeEnum.PROCESSED.value());
        }*/

        alarmInformation.setAbnormalCode(selectAlarmInformation.getAbnormalCode());

        alarmInformation.setFinishDate(finishDate);

        alarmInformationMapper.updateById(alarmInformation);
        pushAlarmInfo(alarmInformation, finishDate);


        OperationLog operationLog = new OperationLog();
        operationLog.setDescription(description);
        operationLog.setDateTime(finishDate);
        operationLog.setNickName("root");
        operationLog.setUserName("root");
        operationLog.setAlarmInformationId(selectAlarmInformation.getId());
        operationLog.setVerifyResult(alarmInformation.getVerifyResult());
        operationLog.setResultStatus(alarmInformation.getResultStatus());
        operationLogMapper.insert(operationLog);
    }

    @Override
    public IPage<AbnormalDetailVO> countDetailList(AbnormalDetailPageVO abnormalDetailPageVO) {
        Integer id = abnormalDetailPageVO.getId();
        long page = abnormalDetailPageVO.getPage();
        long size = abnormalDetailPageVO.getSize();

        if (CommonUtil.isEmpty(id)) {
            throw new BusinessException("id不能为空");
        }

        IPage<AbnormalDetailVO> abnormalDetailVOIPage = new Page<AbnormalDetailVO>(page == 0 ? 1 : page, size == 0 ? 10 : size);


        return abnormalDetailMapper.abnormalDetailVOIPage(abnormalDetailVOIPage, id);
    }

    @Override
    public IPage<OperationLog> operationLogList(AbnormalDetailPageVO abnormalDetailPageVO) {
        Integer id = abnormalDetailPageVO.getId();
        long page = abnormalDetailPageVO.getPage();
        long size = abnormalDetailPageVO.getSize();

        if (CommonUtil.isEmpty(id)) {
            throw new BusinessException("id不能为空");
        }

        IPage<OperationLog> operationLogIPage = new Page<OperationLog>(page == 0 ? 1 : page, size == 0 ? 10 : size);

        LambdaQueryWrapper<OperationLog> lambdaQueryWrapper = new LambdaQueryWrapper();
        lambdaQueryWrapper.eq(OperationLog::getAlarmInformationId, id);


        return operationLogMapper.selectPage(operationLogIPage, lambdaQueryWrapper);
    }


    @Override
    public Map<String, List<Integer>> trendOfStatistical(AlarmAnalyseQueryVO alarmAnalyseQueryVO) {


        List<Map<String, Object>> resultList = alarmInformationMapper.trendOfStatistical(alarmAnalyseQueryVO);
        Map<String, Integer> newMonthMap = newMonthMap();
        //类型   值
        List<String> verify_results = resultList.stream().map(obj -> obj.get("verify_result").toString()).distinct().collect(Collectors.toList());

        Map<String, List<Integer>> resultMap = new HashMap<>();
        for (String verify_result : verify_results) {
            Map<String, Integer> map = resultList.stream().filter(obj -> verify_result.equals(obj.get("verify_result").toString())).collect(Collectors.toMap(obj -> obj.get("month").toString(), obj -> Integer.valueOf(obj.get("count").toString())));
            List<Integer> list = new ArrayList<>();

            for (String month : newMonthMap.keySet()) {
                list.add(CommonUtil.isNotEmpty(map.get(month)) ? map.get(month) : newMonthMap.get(month));
            }
            resultMap.put(verify_result, list);
        }

        return resultMap;
    }

    @Override
    public Map<String, Map<String, Integer>> faultStatistics(AlarmAnalyseQueryVO alarmAnalyseQueryVO) {
        List<Map<String, Object>> faultStatistics = alarmInformationMapper.faultStatistics(alarmAnalyseQueryVO);
        Map<String, Map<String, Integer>> resultMap = new HashMap<>();
        for (Map<String, Object> stringObjectMap : faultStatistics) {
            if (!resultMap.containsKey(stringObjectMap.get("verify_result").toString())) {
                Map<String, Integer> map = new HashMap<String, Integer>();
                resultMap.put(stringObjectMap.get("verify_result").toString(), map);
            }
            resultMap.get(stringObjectMap.get("verify_result").toString()).put(stringObjectMap.get("abnormalValue").toString(), Integer.valueOf(stringObjectMap.get("count").toString()));
        }
        return resultMap;
    }

    @Override
    public List<RealTimeAlarmVO> realTimeAlarm() {

        List<RealTimeAlarmVO> realTimeAlarmVO = alarmInformationMapper.realTimeAlarm();
        return realTimeAlarmVO;
    }

    @Override
    public void pushAlarmInfo(AlarmInformation alarmInformation, Date date) {
   /*     Integer alarmId = alarmInformation.getId();
        Integer pulverizerPointId = alarmInformation.getPulverizerPointId();
        Map<String, Object> map = new HashMap<>();
        PulverizerPoint pulverizerPoint = pulverizerPointMapper.selectById(pulverizerPointId);
        String pulverizerCode = pulverizerPoint.getPulverizerCode();
        Integer no = pulverizerPoint.getNo();
        String key = pulverizerCode + "|" + no;

        Map<String, Object> pulverizerPointRedisVOMap = redisTemplateUtil.hmget(CommonConstans.PULVERIZER_POINT_REDIS);
        PulverizerPointRedisVO pulverizerPointRedisVO = JSON.toJavaObject(JSON.parseObject(pulverizerPointRedisVOMap.get(key).toString()), PulverizerPointRedisVO.class);
        Map<String, Object> baseDictMap = redisTemplateUtil.hmget(CommonConstans.BASE_DICT_REDIS);
        BaseDict baseDict = JSON.toJavaObject(JSON.parseObject(baseDictMap.get("abnormal|" + alarmInformation.getAbnormalCode()).toString()), BaseDict.class);

        log.info("alarmInformation:" + alarmInformation);
        String description = pulverizerPointRedisVO.getPulverizerValue() + pulverizerPointRedisVO.getPositionValue() + "," +
                baseDict.getValue() + "," + (CommonUtil.isNotEmpty(alarmInformation.getResultStatus()) ? StatusTypeEnum.toType(alarmInformation.getResultStatus()).description() : "未处理");
        map.put("alarmId", alarmId);
        map.put("pulverizerPointId", pulverizerPointId);
        map.put("description", description);
        map.put("pulverizerCode", pulverizerPointRedisVO.getPulverizerCode());
        map.put("date", date);*/


        DataResult result = new DataResult();

        //todo  修改返回数据
        AlarmInformationVO alarmInformationVO = alarmInformationMapper.getAlarmInformation(alarmInformation.getId());

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("data", alarmInformationVO);
        resultMap.put("type", CommonConstans.SocketType.ALARMINFO.getValue());
        result.setData(resultMap);
        result.setCode(Constants.RETURN_NORMAL);
        result.setMessage("获取报警处理信息成功");


        wsTemplate.convertAndSend("/topic/server", result);
//        log.info("发生报警:" + map);
    }

    @Override
    public List<Map<String, Object>> pulverizerRealTimeAlarm(String pulverizerCode) {
        Map<String, Object> pulverizerPointRedisVOMap = redisTemplateUtil.hmget(CommonConstans.PULVERIZER_POINT_REDIS);
        Map<String, Object> baseDictMap = redisTemplateUtil.hmget(CommonConstans.BASE_DICT_REDIS);
        List<AlarmInformation> alarmInformations = alarmInformationMapper.pulverizerRealTimeAlarm(pulverizerCode);
        List<Map<String, Object>> maps = alarmInformations.stream().map(
                alarmInformation -> {
                    Integer alarmId = alarmInformation.getId();
                    Integer pulverizerPointId = alarmInformation.getPulverizerPointId();
                    PulverizerPoint pulverizerPoint = pulverizerPointMapper.selectById(pulverizerPointId);
                    Integer no = pulverizerPoint.getNo();
                    String key = pulverizerCode + "|" + no;

                    Map<String, Object> map = new HashMap<>();
                    PulverizerPointRedisVO pulverizerPointRedisVO = JSON.toJavaObject(JSON.parseObject(pulverizerPointRedisVOMap.get(key).toString()), PulverizerPointRedisVO.class);
                    BaseDict baseDict = JSON.toJavaObject(JSON.parseObject(baseDictMap.get("abnormal|" + alarmInformation.getAbnormalCode()).toString()), BaseDict.class);
                    String description = pulverizerPointRedisVO.getPulverizerValue() + pulverizerPointRedisVO.getPositionValue() + "," +
                            baseDict.getValue() + "," + (CommonUtil.isNotEmpty(alarmInformation.getResultStatus()) ? StatusTypeEnum.toType(alarmInformation.getResultStatus()).description() : "未处理");
                    map.put("alarmId", alarmId);
                    map.put("pulverizerPointId", pulverizerPointId);
                    map.put("description", description);
                    map.put("date", CommonUtil.isEmpty(alarmInformation.getFinishDate()) ? alarmInformation.getAlarmTime() : alarmInformation.getFinishDate());
                    return map;
                }
        ).collect(Collectors.toList());
        return maps;
    }

    @Override
    public List<String> years() {


        return alarmInformationMapper.years();
    }

    @Override
    public Map<String, Long> count() {
        return alarmInformationMapper.count();
    }

    @Override
    public void insertOperationLog(Date alarmTime, AlarmInformation alarmInformation) {
        //插入操作记录表
        OperationLog operationLog = new OperationLog();
        operationLog.setAlarmInformationId(alarmInformation.getId());
        operationLog.setDateTime(alarmTime);
        operationLog.setDescription("发生" + baseDictService.getBaseDict("abnormal", alarmInformation.getAbnormalCode()).getValue());
        operationLog.setUserName("admin");
        operationLog.setNickName("未知");
        operationLogMapper.insert(operationLog);
    }

    @Override
    public void alarmCreate(Date alarmTime, Integer pulverizerPointId, String abnormalCode, String alarmDescription, String detectionValue, String positionCode) {
        //判断是否存在 当前磨煤机未处理的相同报警
        AlarmInformation alarmInformation = this.exists(pulverizerPointId, abnormalCode);
        if (CommonUtil.isEmpty(alarmInformation)) {
            log.info("不存在报警信息,pulverizerPointId:" + pulverizerPointId + ",abnormalCode:" + abnormalCode);
            //写入报警表
            alarmInformation = insertAlarmInformation(alarmTime, pulverizerPointId, abnormalCode, alarmDescription, detectionValue, positionCode);
            //写入异常明细表
            AbnormalDetail abnormalDetail = insertAbnormalDetail(alarmTime, pulverizerPointId, abnormalCode, alarmDescription, detectionValue, alarmInformation);
            //写入操作记录表
//            insertOperationLog(alarmTime, alarmInformation);

            this.pushAlarmInfo(alarmInformation, alarmTime);
        } else {
            log.info("alarmInformation:{}", alarmInformation.getId());
            alarmInformationMapper.augmentCount(alarmInformation.getId());
            AbnormalDetail abnormalDetail = insertAbnormalDetail(alarmTime, pulverizerPointId, abnormalCode, alarmDescription, detectionValue, alarmInformation);
//            insertOperationLog(alarmTime, alarmInformation);
        }
    }

    @Override
    public Map<String, Map<String, Integer>> hisTrueFaultByPulverizer(AlarmAnalyseQueryVO alarmAnalyseQueryVO) {

        List<Map<String, Object>> faultStatistics = alarmInformationMapper.hisTrueFaultByPulverizer(alarmAnalyseQueryVO);
        Map<String, Map<String, Integer>> resultMap = new HashMap<>();
        for (Map<String, Object> stringObjectMap : faultStatistics) {
            if (!resultMap.containsKey(stringObjectMap.get("verify_result").toString())) {
                Map<String, Integer> map = new HashMap<String, Integer>();
                resultMap.put(stringObjectMap.get("verify_result").toString(), map);
            }
            resultMap.get(stringObjectMap.get("verify_result").toString()).put(stringObjectMap.get("pulverizerValue").toString(), Integer.valueOf(stringObjectMap.get("count").toString()));
        }
        return resultMap;

    }

    @Override
    public void export(AlarmPageVO alarmPageVO, HttpServletResponse response) throws IOException {

        ExcelWriter excelWriter = null;
        alarmPageVO.setSize(100000000);
        response.setCharacterEncoding("utf-8");
        String date = "";

        if (StringUtils.isNotBlank(alarmPageVO.getStartDate()) && StringUtils.isNotBlank(alarmPageVO.getEndDate())) {
            date = alarmPageVO.getStartDate() + "_" + alarmPageVO.getEndDate();
        }
        String fileName = URLEncoder.encode("磨煤机故障记录导出" + date, "UTF-8").replaceAll("\\+", "%20");

        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        IPage<AlarmInformationVO> page = this.page(alarmPageVO);
        excelWriter = EasyExcel.write(response.getOutputStream()).build();
 /*       List<List<String>> head = Arrays.asList("#", "报警时间", "故障类型", "所属磨煤机", "所属部位", "点位名称", "可能原因", "处理建议", "报警次数", "处理状态", "处理结果", "处理描述", "处理时间")
                .stream().map(field -> {
                    List<String> fieldL = new ArrayList<String>();
                    fieldL.add(field);
                    return fieldL;
                }).collect(Collectors.toList());*/


        List<List<String>> head = Arrays.asList("#", "报警时间", "故障类型", "所属磨煤机", "所属部位", "点位名称", "可能原因", "处理建议", "处理状态", "处理结果", "处理时间")
                .stream().map(field -> {
                    List<String> fieldL = new ArrayList<String>();
                    fieldL.add(field);
                    return fieldL;
                }).collect(Collectors.toList());
        WriteSheet totalWrite = EasyExcel.writerSheet("总计")
                .head(head).registerWriteHandler(new LongestMatchColumnWidthStyleStrategy()).build();


        List<List<Object>> data = new ArrayList<>();
 /*       for (List<String> strings : head) {
            List<Object> fieldData = new ArrayList<>();
//            fieldData.add(strings);
            data.add(fieldData);
        }


        for (int i = 0; i < records.size(); i++) {
            AlarmInformationVO alarmInformationVO = records.get(0);
            data.get(0).add(i);
            data.get(1).add(alarmInformationVO.getAlarmTime());
            data.get(2).add(alarmInformationVO.getAbnormalValue());
            data.get(3).add(alarmInformationVO.getPulverizerValue());
            data.get(4).add(alarmInformationVO.getPositionValue());
            data.get(5).add(alarmInformationVO.getPointName());
            data.get(6).add(alarmInformationVO.getPossibleCause());
            data.get(7).add(alarmInformationVO.getProposal());
            data.get(8).add(alarmInformationVO.getCount());
            data.get(9).add(alarmInformationVO.getResultStatusValue());
            data.get(10).add(alarmInformationVO.getVerifyResultValue());
            data.get(11).add(alarmInformationVO.getProcessDescription());
            data.get(12).add(alarmInformationVO.getFinishDate());
        }
*/

        List<AlarmInformationVO> records = page.getRecords();
        for (int i = 0; i < records.size(); i++) {
            AlarmInformationVO alarmInformationVO = records.get(i);
            List<Object> tempData = new ArrayList<>();
            tempData.add(i + 1);
            tempData.add(alarmInformationVO.getAlarmTime());
            tempData.add(alarmInformationVO.getAbnormalValue());
            tempData.add(alarmInformationVO.getPulverizerValue());
            tempData.add(alarmInformationVO.getPositionValue());
            tempData.add(alarmInformationVO.getPointName());
            tempData.add(alarmInformationVO.getPossibleCause());
            tempData.add(alarmInformationVO.getProposal());
//            tempData.add(alarmInformationVO.getCount());
            tempData.add(alarmInformationVO.getResultStatusValue());
            tempData.add(alarmInformationVO.getVerifyResultValue());
//            tempData.add(alarmInformationVO.getProcessDescription());
            tempData.add(alarmInformationVO.getFinishDate());

            data.add(tempData);
        }
        excelWriter.write(data, totalWrite);


        excelWriter.finish();

    }

    @Override
    @Transactional
    public void triggeringAlarm(TriggeringAlarmVO triggeringAlarmVO) {
        //验证校验码是否正确
        String key = triggeringAlarmVO.getKey();
        validateKey(key);

        //验证报警代码是否存在
        String warningCode = triggeringAlarmVO.getWarningCode();
        existsAlarmCode(warningCode);
        //创建报警信息
        alarmCreate(triggeringAlarmVO);


    }

    private void alarmCreate(TriggeringAlarmVO triggeringAlarmVO) {
        AlarmInformation tempAlarmInformation = new AlarmInformation();
        tempAlarmInformation.setAbnormalCode(triggeringAlarmVO.getWarningCode());
        tempAlarmInformation.setPulverizerCode(triggeringAlarmVO.getPulverizerCode());
        tempAlarmInformation.setPossibleCause(triggeringAlarmVO.getPossibleCause());
        tempAlarmInformation.setProposal(triggeringAlarmVO.getProposal());
        tempAlarmInformation.setPositionCode(triggeringAlarmVO.getPositionCode());
        tempAlarmInformation.setCount(1);

        //判断当前报警信息是否存在
        LambdaQueryWrapper<AlarmInformation> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(AlarmInformation::getAbnormalCode, triggeringAlarmVO.getWarningCode())
                .eq(AlarmInformation::getPulverizerCode, triggeringAlarmVO.getPulverizerCode())
                .and(e -> e.ne(AlarmInformation::getResultStatus, StatusTypeEnum.PROCESSED.value()).or().isNull(AlarmInformation::getResultStatus));
        AlarmInformation alarmInformation = alarmInformationMapper.selectOne(wrapper);
        if (alarmInformation == null) {
            alarmInformationMapper.insert(tempAlarmInformation);
            alarmInformation = tempAlarmInformation;
            this.pushAlarmInfo(alarmInformation, alarmInformation.getFinishDate());

        }

        //记录报警次数
        alarmInformationMapper.augmentCount(alarmInformation.getId());

        //插入异常明细表
        AbnormalDetail abnormalDetail = new AbnormalDetail();
        abnormalDetail.setAbnormalCode(alarmInformation.getAbnormalCode());
        abnormalDetail.setAlarmDescription(alarmInformation.getPossibleCause() + "|" + alarmInformation.getPossibleCause());
        abnormalDetail.setAlarmInformationId(alarmInformation.getId());
        abnormalDetail.setAlarmTime(new Date());
//        abnormalDetail.setPulverizerPointId();
        abnormalDetail.setPulverizerCode(alarmInformation.getPulverizerCode());
        abnormalDetailMapper.insert(abnormalDetail);


    }

    //验证报警代码是否存在
    private void existsAlarmCode(String warningCode) {
        BaseDict baseDict = new BaseDict();
        baseDict.setType("abnormal");
        baseDict.setCode(warningCode);
        LambdaQueryWrapper<BaseDict> wrapper = new LambdaQueryWrapper();
        wrapper.eq(BaseDict::getType, baseDict.getType())
                .eq(BaseDict::getCode, baseDict.getCode())
                .isNull(BaseDict::getDeleteTime);
        int count = baseDictService.count(wrapper);
        if (count == 0) {
            throw new BusinessException("报警代码不存在");
        }
    }

    private void validateKey(String key) {

        // TODO: 2022/1/6 验证逻辑未实现
    }


    private Map<String, Integer> newMonthMap() {
        Map<String, Integer> map = new LinkedHashMap<>();
        for (int i = 1; i <= 12; i++) {
            map.put(String.valueOf(i), 0);
        }
        return map;
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


    private void insertOperationLog(Date finishDate, AlarmInformation alarmInformation, String description) {
        //插入操作记录表
        OperationLog operationLog = new OperationLog();
        operationLog.setAlarmInformationId(alarmInformation.getId());
        operationLog.setDateTime(finishDate);
        operationLog.setDescription(description);
        operationLogMapper.insert(operationLog);

    }


    private AlarmInformation insertAlarmInformation(Date alarmTime, Integer pulverizerPointId, String abnormalCode, String alarmDescription, String detectionValue, String positionCode) {
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
        alarmInformation.setPositionCode(positionCode);
        alarmInformationMapper.insert(alarmInformation);
        return alarmInformation;
    }
}
