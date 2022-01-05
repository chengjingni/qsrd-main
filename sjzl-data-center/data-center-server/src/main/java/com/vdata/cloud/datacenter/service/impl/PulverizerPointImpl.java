package com.vdata.cloud.datacenter.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vdata.cloud.common.exception.BusinessException;
import com.vdata.cloud.datacenter.constants.CommonConstans;
import com.vdata.cloud.datacenter.entity.BaseDict;
import com.vdata.cloud.datacenter.entity.PointDcsLog;
import com.vdata.cloud.datacenter.entity.PulverizerPoint;
import com.vdata.cloud.datacenter.mapper.PointDcsLogMapper;
import com.vdata.cloud.datacenter.mapper.PulverizerPointMapper;
import com.vdata.cloud.datacenter.service.IBaseDictService;
import com.vdata.cloud.datacenter.service.IPulverizerPointService;
import com.vdata.cloud.datacenter.util.RedisTemplateUtil;
import com.vdata.cloud.datacenter.vo.PulverizerPointPageVO;
import com.vdata.cloud.datacenter.vo.PulverizerPointRedisVO;
import com.vdata.cloud.datacenter.vo.PulverizerPointVO;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @ProjectName: qsrd
 * @Package: com.vdata.cloud.datacenter.service.impl
 * @ClassName: PulverizerPointImpl
 * @Author: HK
 * @Description:
 * @Date: 2021/7/20 13:49
 * @Version: 1.0
 */
@Service
@Log4j2
public class PulverizerPointImpl extends ServiceImpl<PulverizerPointMapper, PulverizerPoint> implements IPulverizerPointService {
    @Autowired
    private PulverizerPointMapper pulverizerPointMapper;
    @Autowired
    private IBaseDictService baseDictService;


    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private PointDcsLogMapper pointDcsLogMapper;


    @Autowired
    private RedisTemplateUtil redisTemplateUtil;


    @Override
    public void insert(PulverizerPoint pulverizerPoint) throws BusinessException {
        //判断传入的字典代码是否存在
        boolean exists = existsDict(pulverizerPoint);
        if (!exists) {
            throw new BusinessException("字典内容不存在");
        }
        int count = pulverizerPointMapper.existsNoCountByPulverizerCode(pulverizerPoint);
        if (count > 0) {
            throw new BusinessException("当前编号已经存在");
        }
        pulverizerPoint.setDeleteDate(null);
        pulverizerPointMapper.insert(pulverizerPoint);
        this.listSaveRedis();
    }

    private boolean existsDict(PulverizerPoint pulverizerPoint) throws BusinessException {


        List<BaseDict> baseDictList = new ArrayList<>();
        baseDictList.add(new BaseDict("pulverizer", pulverizerPoint.getPulverizerCode()));
        baseDictList.add(new BaseDict("sensor_type", pulverizerPoint.getSensorTypeCode()));
//        baseDictList.add(new BaseDict("test_item", pulverizerPoint.getTestItemCode()));
        return baseDictService.existsIn(baseDictList);
    }

    @Override
    public void update(PulverizerPoint pulverizerPoint) throws BusinessException {
        boolean exists = existsDict(pulverizerPoint);
        if (!exists) {
            throw new BusinessException("字典内容不存在");
        }

      /*  pulverizerPoint.setPulverizerCode(null);
        pulverizerPoint.setSensorTypeCode(null);
        pulverizerPoint.setTestItemCode(null);*/

        pulverizerPoint.setDeleteDate(null);
        //磨煤机类型不可修改
        pulverizerPoint.setPulverizerCode(null);
        pulverizerPointMapper.updateById(pulverizerPoint);
    }

    @Override
    public IPage<PulverizerPointVO> page(PulverizerPointPageVO pulverizerPointPageVO) throws BusinessException {
        long page = pulverizerPointPageVO.getPage();
        long size = pulverizerPointPageVO.getSize();

        IPage<PulverizerPoint> pulverizerPointIPage = new Page<PulverizerPoint>(page == 0 ? 1 : page, size == 0 ? 10 : size);

    /*    LambdaQueryWrapper<PulverizerPoint> pulverizerPointLambdaQueryWrapper = new LambdaQueryWrapper<PulverizerPoint>();
        if (CommonUtil.isNotEmpty(code)) {
            pulverizerPointLambdaQueryWrapper.eq(PulverizerPoint::getPulverizerCode, code);
        }

        pulverizerPointLambdaQueryWrapper.orderByAsc(PulverizerPoint::getNo);*/


        return pulverizerPointMapper.page(pulverizerPointIPage, pulverizerPointPageVO);
    }

    @Override
    public PulverizerPointVO get(int id) throws BusinessException {
        if (id < 1) {
            throw new BusinessException("id不存在");
        }
        return pulverizerPointMapper.get(id);
    }

    @Override
    public void listSaveRedis() {
        List<PulverizerPointRedisVO> details = pulverizerPointMapper.details();
        //删除存在的key
        redisTemplate.delete(CommonConstans.PULVERIZER_POINT_REDIS);
        Map<String, String> map = new HashMap<>();
        for (PulverizerPointRedisVO pulverizerPointRedisVO : details) {
            String json = JSON.toJSON(pulverizerPointRedisVO).toString();
            map.put(String.valueOf(pulverizerPointRedisVO.getPulverizerCode()) + "|" + String.valueOf(pulverizerPointRedisVO.getNo()), json);
        }

        redisTemplate.opsForHash().putAll(CommonConstans.PULVERIZER_POINT_REDIS, map);
    }

    @Override
    public void listSaveRedisByDcs() {
        List<PulverizerPointRedisVO> details = pulverizerPointMapper.details();
        Map<String, String> map = new HashMap<>();
        for (PulverizerPointRedisVO pulverizerPointRedisVO : details) {
            String json = JSON.toJSON(pulverizerPointRedisVO).toString();
            map.put(pulverizerPointRedisVO.getDcsDataIdentifier(), json);
        }
        redisTemplate.opsForHash().putAll(CommonConstans.PULVERIZER_POINT_DCS_REDIS, map);
    }

    @Override
    public IPage<PointDcsLog> listPointDcsLog(Integer pointId, Long page, Long size) {

        IPage<PointDcsLog> ipage = new Page<>(page, size);
        LambdaQueryWrapper<PointDcsLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PointDcsLog::getPointId, pointId)
                .orderByDesc(PointDcsLog::getCutDate);

        return pointDcsLogMapper.selectPage(ipage, wrapper);
    }

    @Override
    public void delete(Integer id) {
        PulverizerPoint pulverizerPoint = new PulverizerPoint();
        pulverizerPoint.setId(id);
        pulverizerPoint.setDeleteDate(new Date());
        pulverizerPointMapper.updateById(pulverizerPoint);
        this.listSaveRedis();

    }


    @Override
    @Transactional
    public void updatev1(PulverizerPoint pulverizerPoint) {
//        PulverizerPoint pulverizerPoint1 = new PulverizerPoint();
//        pulverizerPoint1.setId(pulverizerPoint.getId());
//        pulverizerPoint1.setPointName(pulverizerPoint.getPointName());
        int count = pulverizerPointMapper.existsNoCountByPulverizerCode(pulverizerPoint);
        if (count > 0) {
            throw new BusinessException("当前编号已经存在");
        }
        pulverizerPointMapper.updateById(pulverizerPoint);


        //保存dcs修改记录
        if (StringUtils.isNotBlank(pulverizerPoint.getDcsDataIdentifier())) {
            saveDcsLog(pulverizerPoint);
        }


        //更新缓存中的信息
        this.listSaveRedis();
    }

    private void saveDcsLog(PulverizerPoint pulverizerPoint) {
        LambdaQueryWrapper<PointDcsLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PointDcsLog::getPointId, pulverizerPoint.getId())
                .orderByDesc(PointDcsLog::getCutDate)
                .last("limit 1");
        PointDcsLog pointDcsLog = pointDcsLogMapper.selectOne(wrapper);
        if (pointDcsLog == null || !pointDcsLog.getDcsDataIdentifier().equals(pulverizerPoint.getDcsDataIdentifier())) {
            pointDcsLog = new PointDcsLog();
            pointDcsLog.setDcsDataIdentifier(pulverizerPoint.getDcsDataIdentifier());
            pointDcsLog.setNo(pulverizerPoint.getNo());
            pointDcsLog.setPointId(pulverizerPoint.getId());
            pointDcsLog.setPulverizerCode(pulverizerPoint.getPulverizerCode());
        }
    }
}
