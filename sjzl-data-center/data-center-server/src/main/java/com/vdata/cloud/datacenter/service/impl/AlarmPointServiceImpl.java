package com.vdata.cloud.datacenter.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vdata.cloud.common.exception.BusinessException;
import com.vdata.cloud.datacenter.constants.CommonConstans;
import com.vdata.cloud.datacenter.entity.AlarmPoint;
import com.vdata.cloud.datacenter.mapper.AlarmPointMapper;
import com.vdata.cloud.datacenter.service.IAlarmPointService;
import com.vdata.cloud.datacenter.util.RedisTemplateUtil;
import com.vdata.cloud.datacenter.vo.AlarmPointVO;
import com.vdata.cloud.datacenter.vo.PulverizerPointRedisVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ProjectName: qsrd-main
 * @Package: com.vdata.cloud.datacenter.service.impl
 * @ClassName: AlarmPointServiceImpl
 * @Author: HK
 * @Description:
 * @Date: 2021/12/22 11:44
 * @Version: 1.0
 */
@Service
public class AlarmPointServiceImpl extends ServiceImpl<AlarmPointMapper, AlarmPoint> implements IAlarmPointService {
    @Autowired
    private RedisTemplateUtil redisTemplateUtil;

    @Override
    @Transactional()
    public void joinPoint(List<AlarmPoint> alarmPointList) {
        List<String> alarmCodes = alarmPointList.stream()
                .filter(alarmPoint -> StringUtils.isNotBlank(alarmPoint.getAlarmCode()))
                .map(alarmPoint -> alarmPoint.getAlarmCode()).distinct().collect(Collectors.toList());
        if (alarmCodes.size() > 1) {
            throw new BusinessException("只可以有一个报警类型");
        }

        LambdaQueryWrapper<AlarmPoint> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(AlarmPoint::getAlarmCode, alarmCodes.get(0));
        this.remove(queryWrapper);
        this.saveBatch(alarmPointList);
    }

    @Override
    public List<AlarmPointVO> joinPointList() {
        List<AlarmPointVO> alarmPointVOS = baseMapper.joinPointList();

        Map<Integer, PulverizerPointRedisVO> pulverizerPointRedisVOMap = redisTemplateUtil.hmget(CommonConstans.PULVERIZER_POINT_REDIS).values().stream().map(o -> {
            return JSON.toJavaObject(JSON.parseObject(o.toString()), PulverizerPointRedisVO.class);
        }).collect(Collectors.toMap(PulverizerPointRedisVO::getId, pulverizerPointRedisVO -> pulverizerPointRedisVO));

        for (AlarmPointVO alarmPointVO : alarmPointVOS) {
            String pulverizerPointIds = alarmPointVO.getPulverizerPointIds();
            if (StringUtils.isNotBlank(pulverizerPointIds)) {
                List<PulverizerPointRedisVO> list = Arrays.stream(pulverizerPointIds.split(","))
                        .map(pulverizerPointId -> {
                            return pulverizerPointRedisVOMap.get(Integer.valueOf(pulverizerPointId));
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                alarmPointVO.setPulverizerPointRedisVOS(list);
            } else {
                alarmPointVO.setPulverizerPointRedisVOS(new ArrayList<>());
            }
        }
        return alarmPointVOS;


    }
}
