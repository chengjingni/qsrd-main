package com.vdata.cloud.datacenter.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.vdata.cloud.common.exception.BusinessException;
import com.vdata.cloud.datacenter.entity.PointDcsLog;
import com.vdata.cloud.datacenter.entity.PulverizerPoint;
import com.vdata.cloud.datacenter.vo.PulverizerPointPageVO;
import com.vdata.cloud.datacenter.vo.PulverizerPointVO;

/**
 * @ProjectName: qsrd
 * @Package: com.vdata.cloud.datacenter.service
 * @ClassName: IPulverizerPointService
 * @Author: HK
 * @Description:
 * @Date: 2021/7/20 13:48
 * @Version: 1.0
 */
public interface IPulverizerPointService extends IService<PulverizerPoint> {

    void insert(PulverizerPoint pulverizerPoint) throws BusinessException;

    void update(PulverizerPoint pulverizerPoint) throws BusinessException;

    IPage<PulverizerPointVO> page(PulverizerPointPageVO pulverizerPointPageVO) throws BusinessException;

    PulverizerPointVO get(int id) throws BusinessException;


    void listSaveRedis();

    void updatev1(PulverizerPoint pulverizerPoint);

    void listSaveRedisByDcs();


    IPage<PointDcsLog> listPointDcsLog(Integer pointId, Long page, Long size);

    void delete(Integer id);
}
