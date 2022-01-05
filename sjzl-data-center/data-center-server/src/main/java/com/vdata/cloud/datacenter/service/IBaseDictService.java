package com.vdata.cloud.datacenter.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.vdata.cloud.common.exception.BusinessException;
import com.vdata.cloud.datacenter.entity.BaseDict;
import com.vdata.cloud.datacenter.vo.BaseDictPageVO;

import java.util.List;

/**
 * @ProjectName: qsrd
 * @Package: com.vdata.cloud.datacenter.service
 * @ClassName: IBaseDictService
 * @Author: HK
 * @Description: 数据字典服务类
 * @Date: 2021/7/19 15:03
 * @Version: 1.0
 */
public interface IBaseDictService extends IService<BaseDict> {
    void insert(BaseDict baseDict) throws BusinessException;

    void delete(Integer id) throws BusinessException;

    List<BaseDict> list(String type);

    IPage<BaseDict> page(BaseDictPageVO baseDictPageVO);

    void update(BaseDict baseDict) throws BusinessException;

    void exists(BaseDict baseDict) throws BusinessException;

    boolean existsIn(List<BaseDict> baseDicts) throws BusinessException;

    void insert(List<BaseDict> baseDicts) throws BusinessException;

    void update(List<BaseDict> baseDicts) throws BusinessException;


    BaseDict getBaseDict(String type, String code);

    //存入字典到redis
    void listRedis();
}
