package com.vdata.cloud.datacenter.repository;

import com.vdata.cloud.datacenter.entity.SysOperationLog;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @ProjectName: sjzl-master
 * @Package: com.vdata.cloud.datacenter.repository
 * @ClassName: ArticleRepository
 * @Author: HK
 * @Description:
 * @Date: 2021/7/26 16:45
 * @Version: 1.0
 */
public interface SysOperationLogRepository extends MongoRepository<SysOperationLog, String> {
}
