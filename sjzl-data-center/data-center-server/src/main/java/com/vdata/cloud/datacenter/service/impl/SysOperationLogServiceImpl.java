package com.vdata.cloud.datacenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vdata.cloud.common.util.CommonUtil;
import com.vdata.cloud.datacenter.entity.SysOperationLog;
import com.vdata.cloud.datacenter.mapper.SysOperationLogMapper;
import com.vdata.cloud.datacenter.service.ISysOperationLogService;
import com.vdata.cloud.datacenter.vo.BaseGroupVO;
import com.vdata.cloud.datacenter.vo.ULogGroupVO;
import com.vdata.cloud.datacenter.vo.ULogVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 操作日志
 *
 * @author hk
 * @date 2020-11-03 11:17:50
 */
@Service
@Slf4j
public class SysOperationLogServiceImpl extends ServiceImpl<SysOperationLogMapper, SysOperationLog> implements ISysOperationLogService {

    @Resource
    private SysOperationLogMapper sysOperationLogMapper;

    @Autowired
    private MongoTemplate mongoTemplate;


    @Override
    public IPage<SysOperationLog> queryList(ULogVO uLogVO) {

        long page = uLogVO.getPage() == 0 ? 1 : uLogVO.getPage();
        long limit = uLogVO.getLimit() == 0 ? 10 : uLogVO.getLimit();
        IPage<SysOperationLog> sysOperationLogIPage = new Page<>(page, limit);

        String succeed = uLogVO.getSucceed();

        String search = uLogVO.getSearch();

        String logType = uLogVO.getLogType();


        String loginname = uLogVO.getLoginname();

        String userId = uLogVO.getUserId();

        String sortField = uLogVO.getSortField();


        String sortType = uLogVO.getSortType();

        Date startDate = uLogVO.getStartDate();

        Date endDate = uLogVO.getEndDate();


        QueryWrapper<SysOperationLog> wrapper = new QueryWrapper<>();


        String logname = uLogVO.getLogname();


        if (CommonUtil.isNotEmpty(logname)) {
            wrapper.eq("logname", logname);
        }

        //是否成功
        if (CommonUtil.isNotEmpty(succeed)) {
            wrapper.eq("succeed", succeed);
        }

        //日志类型
        if (CommonUtil.isNotEmpty(logType)) {
            wrapper.eq("LOGTYPE", logType);
        }


        //登录名
        if (CommonUtil.isNotEmpty(loginname)) {
            wrapper.eq("loginname", loginname);
        }


        //用户id
        if (CommonUtil.isNotEmpty(userId)) {
            wrapper.eq("USERID", userId);
        }

        //搜索
        String searchField = uLogVO.getSearchField();
        searchField = CommonUtil.isEmpty(searchField) ? "LOGNAME" : searchField;
        if (CommonUtil.isNotEmpty(search)) {
            Arrays.stream(searchField.split(",")).forEach(
                    str -> {
                        wrapper.like(str, search);
                    }
            );

        }


        //排序

        sortField = CommonUtil.isEmpty(sortField) ? "CREATETIME" : sortField;


        sortType = CommonUtil.isEmpty(sortType) ? "0" : sortType;

        if (sortType.equals("0")) {
            wrapper.orderByDesc(sortField);

        } else {
            wrapper.orderByAsc(sortField);
        }


        //时间选择
        if (CommonUtil.isNotEmpty(endDate)) {
            wrapper.lt("CREATETIME", dateNextOneDay(endDate));


        }

        if (CommonUtil.isNotEmpty(startDate)) {

            wrapper.ge("CREATETIME", startDate);
        }

        IPage<SysOperationLog> sysOperationLogIPage1 = sysOperationLogMapper.selectPage(sysOperationLogIPage, wrapper);


        return sysOperationLogIPage1;
    }


    @Override
    public IPage<ULogGroupVO> queryList1(ULogVO uLogVO) {

        long page = uLogVO.getPage() == 0 ? 1 : uLogVO.getPage();
        long limit = uLogVO.getLimit() == 0 ? 10 : uLogVO.getLimit();
        IPage<ULogGroupVO> sysOperationLogIPage = new Page<>(page, limit);
        Date endDate = uLogVO.getEndDate();
        //时间选择
        if (CommonUtil.isNotEmpty(endDate)) {
            uLogVO.setEndDate(dateNextOneDay1(endDate));
        }

        return sysOperationLogIPage.setRecords(sysOperationLogMapper.queryList(uLogVO, sysOperationLogIPage));
    }


    @Override
    public IPage<ULogGroupVO> queryListM(ULogVO uLogVO) {

        long page = uLogVO.getPage() == 0 ? 1 : uLogVO.getPage();
        long limit = uLogVO.getLimit() == 0 ? 10 : uLogVO.getLimit();
        IPage<ULogGroupVO> sysOperationLogIPage = new Page<>(page, limit);
        Date endDate = uLogVO.getEndDate();
        //时间选择
        if (CommonUtil.isNotEmpty(endDate)) {
            uLogVO.setEndDate(dateNextOneDay1(endDate));
        }
        Query query = new Query();

        if (!StringUtils.isEmpty(uLogVO.getLogname())) {
            query.addCriteria(Criteria.where("logname").is(uLogVO.getLogname()));
        }

        if (!StringUtils.isEmpty(uLogVO.getLoginname())) {
            Pattern pattern = Pattern.compile("^.*" + uLogVO.getLoginname() + ".*$", Pattern.CASE_INSENSITIVE);

            query.addCriteria(Criteria.where("loginname").regex(pattern));
        }

        if (!StringUtils.isEmpty(uLogVO.getSucceed())) {
            query.addCriteria(Criteria.where("succeed").is(uLogVO.getSucceed()));
        }

        if (!StringUtils.isEmpty(uLogVO.getLogType())) {
            query.addCriteria(Criteria.where("logtype").is(uLogVO.getLogType()));
        }

        if (!StringUtils.isEmpty(uLogVO.getUserId())) {
            query.addCriteria(Criteria.where("userid").is(uLogVO.getUserId()));
        }

        if (!StringUtils.isEmpty(uLogVO.getUserId())) {
            query.addCriteria(Criteria.where("userid").is(uLogVO.getUserId()));
        }

        if (uLogVO.getStartDate() != null && uLogVO.getEndDate() != null) {
            query.addCriteria(Criteria.where("createtime").gte(uLogVO.getStartDate())/*.and("createtime")*/.lt(uLogVO.getEndDate()));
        }

        if (!StringUtils.isEmpty(uLogVO.getGroupId())) {
            query.addCriteria(Criteria.where("fkbasegroup").is(uLogVO.getGroupId()));
        }
        long count = mongoTemplate.count(query, SysOperationLog.class);

        query.with(Sort.by(Sort.Order.desc("createtime")));

        int skip = (int) ((uLogVO.getPage() - 1) * uLogVO.getLimit());
        query.skip(skip);
        query.limit((int) uLogVO.getLimit());
        List<SysOperationLog> sysOperationLogs = mongoTemplate.find(query, SysOperationLog.class);

        List<ULogGroupVO> collect = sysOperationLogs.stream()
                .map(sysOperationLog -> {
                    ULogGroupVO uLogGroupVO = new ULogGroupVO();
                    BeanUtils.copyProperties(sysOperationLog, uLogGroupVO);
                    String userid = sysOperationLog.getUserid();
                    if (!StringUtils.isEmpty(userid)) {
                        BaseGroupVO baseGroupVO = sysOperationLogMapper.getBaseGroupVO(userid);
                        if (baseGroupVO != null) {
                            uLogGroupVO.setGroupName(baseGroupVO.getName());

                        }
                    }

                    return uLogGroupVO;

                }).collect(Collectors.toList());
        sysOperationLogIPage.setTotal(count);
        sysOperationLogIPage.setRecords(collect);
        return sysOperationLogIPage.setRecords(collect);
    }

    @Override
    public List<String> twoComboBox(String logType) {

        List<SysOperationLog> sysOperationLogs = sysOperationLogMapper.selectList(new LambdaQueryWrapper<SysOperationLog>().eq(SysOperationLog::getLogtype, logType));

        return sysOperationLogs.stream().map(m -> m.getLogname()).distinct().collect(Collectors.toList());
    }

    @Override
    public List<String> twoComboBoxM(String logType) {
        Query query = new Query();
        query.addCriteria(Criteria.where("logtype").is(logType));
        List<String> lognames = mongoTemplate.findDistinct(query, "logname", SysOperationLog.class, String.class);
        return lognames;
    }


    @Override
    public Map<String, List<Object>> userLoginLineChart(Map<String, Object> param) {
        if (CommonUtil.isEmpty(param.get("startDate")) && CommonUtil.isEmpty(param.get("endDate"))) {
            param = getDefaultDateMap();
        }

        List<Map<String, Object>> maps = sysOperationLogMapper.userLoginLineChart(param);
        Map<String, Object> newMap = new HashMap<>();
        for (Map<String, Object> map : maps) {
            Object x = map.get("x");
            Object y = map.get("y");
            newMap.put(CommonUtil.objToStr(x), y);
        }


        List<Map<String, Object>> timeSeries = timeSeries(CommonUtil.objToStr(param.get("startDate")), CommonUtil.objToStr(param.get("endDate")));
        maps = timeSeries
                .stream().map(m -> {

                    Object y = newMap.getOrDefault(m.get("x"), 0);

                    m.put("y", y);
                    return m;
                }).collect(Collectors.toList());


        List<Object> x = maps.stream().map(m -> m.get("x")).collect(Collectors.toList());
        List<Object> y = maps.stream().map(m -> m.get("y")).collect(Collectors.toList());

        Map<String, List<Object>> map = new HashMap<>();
        map.put("x", x);
        map.put("y", y);
        return map;
    }

    //生成时间序列
    public List<Map<String, Object>> timeSeries(String startDate, String endDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        List<Map<String, Object>> list = new ArrayList<>();

        boolean flage = true;
        String date = startDate;

        if (!startDate.equals(endDate)) {
            while (flage) {


                Map<String, Object> map = new HashMap<>();
                map.put("x", date);
                map.put("y", 0);
                list.add(map);
                date = dateNextOneDay(LocalDate.parse(date, formatter));

                if (date.equals(endDate)) {
                    flage = false;
                }
            }
        } else {
            Map<String, Object> map = new HashMap<>();
            map.put("x", date);
            map.put("y", 0);
            list.add(map);

        }


        return list;

    }


    private Map<String, Object> getDefaultDateMap() {
        Map<String, Object> map = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String endDate = formatter.format(now);
        String startDate = formatter.format(now.minusDays(30));
        map.put("endDate", endDate);
        map.put("startDate", startDate);
        return map;
    }


    private String dateNextOneDay(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String format = formatter.format(date.plusDays(1));

        return format;
    }

    private String dateNextOneDay(Date date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, 1);//把日期往后增加一天.整数往后推,负数往前移动
        date = calendar.getTime(); //这个时间就是日期往后推一天的结果
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(date);

        return dateString;
    }

    private Date dateNextOneDay1(Date date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, 1);//把日期往后增加一天.整数往后推,负数往前移动
        date = calendar.getTime(); //这个时间就是日期往后推一天的结果


        return date;
    }
}
