package com.vdata.cloud.datacenter.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.vdata.cloud.datacenter.entity.SysOperationLog;
import com.vdata.cloud.datacenter.vo.ULogGroupVO;
import com.vdata.cloud.datacenter.vo.ULogVO;

import java.util.List;
import java.util.Map;

/**
 * 操作日志
 *
 * @author hk
 * @date 2020-11-03 11:17:50
 */
public interface ISysOperationLogService extends IService<SysOperationLog> {


    IPage<SysOperationLog> queryList(ULogVO uLogVO);

    List<String> twoComboBox(String logType);

    List<String> twoComboBoxM(String logType);

    Map<String, List<Object>> userLoginLineChart(Map<String, Object> param);

    IPage<ULogGroupVO> queryList1(ULogVO uLogVO);

    IPage<ULogGroupVO> queryListM(ULogVO uLogVO);
}