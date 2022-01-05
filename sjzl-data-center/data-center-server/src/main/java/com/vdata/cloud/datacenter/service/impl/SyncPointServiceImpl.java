package com.vdata.cloud.datacenter.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vdata.cloud.datacenter.entity.SyncPoint;
import com.vdata.cloud.datacenter.mapper.SyncPointMapper;
import com.vdata.cloud.datacenter.service.ISyncPointService;
import org.springframework.stereotype.Service;

/**
 * @ProjectName: qsrd-main
 * @Package: com.vdata.cloud.datacenter.service
 * @ClassName: ISyncPointService
 * @Author: HK
 * @Description:
 * @Date: 2021/11/11 14:46
 * @Version: 1.0
 */
@Service
public class SyncPointServiceImpl extends ServiceImpl<SyncPointMapper, SyncPoint> implements ISyncPointService {
}
