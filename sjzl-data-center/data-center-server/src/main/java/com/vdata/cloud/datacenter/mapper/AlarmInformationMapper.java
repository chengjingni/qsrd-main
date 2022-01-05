package com.vdata.cloud.datacenter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.vdata.cloud.datacenter.entity.AlarmInformation;
import com.vdata.cloud.datacenter.vo.AlarmAnalyseQueryVO;
import com.vdata.cloud.datacenter.vo.AlarmInformationVO;
import com.vdata.cloud.datacenter.vo.AlarmPageVO;
import com.vdata.cloud.datacenter.vo.RealTimeAlarmVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author hk
 * @date 2021-07-21 14:43:16
 */
public interface AlarmInformationMapper extends BaseMapper<AlarmInformation> {
    IPage<AlarmInformationVO> page(@Param("alarmInformationIPage") IPage<AlarmInformationVO> alarmInformationIPage,
                                   @Param("whereSql") String where,
                                   @Param("alarmPageVO") AlarmPageVO alarmPageVO);

    void augmentCount(@Param("id") Integer id);

    List<Map<String, Object>> trendOfStatistical(AlarmAnalyseQueryVO alarmAnalyseQueryVO);

    List<Map<String, Object>> faultStatistics(AlarmAnalyseQueryVO alarmAnalyseQueryVO);

    List<RealTimeAlarmVO> realTimeAlarm();

    List<AlarmInformation> pulverizerRealTimeAlarm(@Param("pulverizerCode") String pulverizerCode);

    List<String> years();

    Map<String, Long> count();

    List<Map<String, Object>> hisTrueFaultByPulverizer(AlarmAnalyseQueryVO alarmAnalyseQueryVO);

    AlarmInformationVO getAlarmInformation(@Param("id") Integer id);
}
