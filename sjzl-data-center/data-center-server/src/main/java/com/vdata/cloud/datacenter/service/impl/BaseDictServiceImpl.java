package com.vdata.cloud.datacenter.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vdata.cloud.common.exception.BusinessException;
import com.vdata.cloud.common.util.CommonUtil;
import com.vdata.cloud.datacenter.constants.CommonConstans;
import com.vdata.cloud.datacenter.entity.BaseDict;
import com.vdata.cloud.datacenter.mapper.BaseDictMapper;
import com.vdata.cloud.datacenter.service.IBaseDictService;
import com.vdata.cloud.datacenter.vo.BaseDictPageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ProjectName: qsrd
 * @Package: com.vdata.cloud.datacenter.service.impl
 * @ClassName: BaseDictServiceImpl
 * @Author: HK
 * @Description: 数据字典服务实现类
 * @Date: 2021/7/19 15:04
 * @Version: 1.0
 */
@Service
public class BaseDictServiceImpl extends ServiceImpl<BaseDictMapper, BaseDict> implements IBaseDictService {
    @Autowired
    private BaseDictMapper baseDictMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void insert(BaseDict baseDict) throws BusinessException {
        baseDict.setType(isEmptyDefaultType(baseDict.getType()));
        exists(baseDict);
        baseDict.setDeleteTime(null);
        baseDictMapper.insert(baseDict);
    }

    @Override
    public void delete(Integer id) throws BusinessException {
        //如果存在子集无法删除
       /* long subcollectionCount = baseDictMapper.getSubcollectionCountById(id);
        if (subcollectionCount > 0) {
            throw new BusinessException("当前字典存在子集无法删除");
        }*/

        BaseDict baseDict1 = baseDictMapper.selectById(id);

        BaseDict baseDict = new BaseDict();
        baseDict.setDeleteTime(new Date());
        baseDictMapper.update(baseDict, new LambdaQueryWrapper<BaseDict>()
                .eq(BaseDict::getId, id)
                .or()
                .eq(BaseDict::getType, baseDict1.getCode())
        );
    }

    @Override
    public List<BaseDict> list(String type) {
        LambdaQueryWrapper<BaseDict> baseDictLambdaQueryWrapper = queryWhere(type);
        return baseDictMapper.selectList(baseDictLambdaQueryWrapper);
    }

    private LambdaQueryWrapper<BaseDict> queryWhere(String type) {
        LambdaQueryWrapper<BaseDict> baseDictLambdaQueryWrapper = new LambdaQueryWrapper<BaseDict>();
        baseDictLambdaQueryWrapper.eq(BaseDict::getType, isEmptyDefaultType(type))
                .isNull(BaseDict::getDeleteTime)
                .orderByAsc(BaseDict::getSort)
                .orderByDesc(BaseDict::getCreateTime);
        return baseDictLambdaQueryWrapper;
    }

    private String isEmptyDefaultType(String type) {
        return CommonUtil.isEmpty(type) ? "root" : type;
    }


    @Override
    public IPage<BaseDict> page(BaseDictPageVO baseDictPageVO) {
        String type = baseDictPageVO.getType();

        long page = baseDictPageVO.getPage();
        long size = baseDictPageVO.getSize();
        IPage<BaseDict> baseDictIPage = new Page<BaseDict>(page == 0 ? 1 : page, size == 0 ? 10 : size);
        LambdaQueryWrapper<BaseDict> baseDictLambdaQueryWrapper = queryWhere(type);
        return baseDictMapper.selectPage(baseDictIPage, baseDictLambdaQueryWrapper);


    }

    @Override
    public void update(BaseDict baseDict) throws BusinessException {
        if (CommonUtil.isEmpty(baseDict.getId())) {
            throw new BusinessException("没有传入id");
        }
        exists(baseDict);
        baseDict.setType(null);
        baseDict.setDeleteTime(null);
        baseDictMapper.updateById(baseDict);
    }


    @Override
    public void exists(BaseDict baseDict) throws BusinessException {
        if (CommonUtil.isEmpty(baseDict.getCode()) || CommonUtil.isEmpty(baseDict.getValue())) {
            throw new BusinessException("代码或值不能为空");
        }

        //如果code已经存在则无法插入
        LambdaQueryWrapper<BaseDict> baseDictLambdaQueryWrapper = new LambdaQueryWrapper<BaseDict>();
        baseDictLambdaQueryWrapper.eq(BaseDict::getType, isEmptyDefaultType(baseDict.getType()))
                .and(a -> a.eq(BaseDict::getCode, baseDict.getCode()).or().eq(BaseDict::getValue, baseDict.getValue()))
                .isNull(BaseDict::getDeleteTime);

        if (!CommonUtil.isEmpty(baseDict.getId())) {
            baseDictLambdaQueryWrapper.ne(BaseDict::getId, baseDict.getId());
        }
        Integer count = baseDictMapper.selectCount(baseDictLambdaQueryWrapper);
        if (count > 0) {
            throw new BusinessException("当前类型已存在此代码或值");
        }
    }

    @Override
    public boolean existsIn(List<BaseDict> baseDicts) throws BusinessException {
        int size = baseDicts.size();

        if (size == 0) {
            throw new BusinessException("没有传入待验证的内容");
        }

        for (BaseDict baseDict : baseDicts) {
            if (CommonUtil.isEmpty(baseDict.getType()) || CommonUtil.isEmpty(baseDict.getCode())) {
                throw new BusinessException("代码或值不能为空");
            }
        }

        int count = baseMapper.existsCountIn(baseDicts);

        if (size != count) {
            return false;
        }

        return true;
    }


    @Override
    public void insert(List<BaseDict> baseDicts) throws BusinessException {
        //判断是否重复
        isReDo(baseDicts);
//        exists(baseDicts);
        this.saveBatch(baseDicts);
    }

    private void exists(List<BaseDict> baseDicts) {
        String type = baseDicts.stream().map(baseDict -> baseDict.getType()).collect(Collectors.toList()).get(0);
        List<String> codes = baseDicts.stream().map(baseDict -> baseDict.getCode()).collect(Collectors.toList());
        List<String> values = baseDicts.stream().map(baseDict -> baseDict.getValue()).collect(Collectors.toList());
        LambdaQueryWrapper<BaseDict> baseDictLambdaQueryWrapper = new LambdaQueryWrapper<>();

        baseDictLambdaQueryWrapper.isNull(BaseDict::getDeleteTime)
                .eq(BaseDict::getType, type)
                .and(wrapper -> wrapper.in(BaseDict::getCode, codes).or().in(BaseDict::getValue, values));
        Integer count = baseMapper.selectCount(baseDictLambdaQueryWrapper);
        if (count > 0) {
            throw new BusinessException("当前代码值已经存在");
        }
    }

    @Override
    @Transactional
    public void update(List<BaseDict> baseDicts) throws BusinessException {
        isReDo(baseDicts);
        Optional<BaseDict> baseDictOptional = baseDicts.stream().filter(baseDict -> "root".equals(baseDict.getType())).findAny();


        if (!baseDictOptional.isPresent()) {
            throw new BusinessException("根节点不存在");
        }
        BaseDict baseDict = baseDictOptional.get();

        LambdaQueryWrapper<BaseDict> baseDictLambdaQueryWrapper = new LambdaQueryWrapper<>();

        baseDictLambdaQueryWrapper.eq(BaseDict::getId, baseDict.getId()).or().eq(BaseDict::getType, baseDict.getCode());
        baseMapper.delete(baseDictLambdaQueryWrapper);
        this.saveBatch(baseDicts);
    }

    @Override
    public BaseDict getBaseDict(String type, String code) {
        return baseDictMapper.selectOne(
                new LambdaQueryWrapper<BaseDict>()
                        .eq(BaseDict::getType, type)
                        .eq(BaseDict::getCode, code)
                        .isNull(BaseDict::getDeleteTime)
        );
    }

    @Override
    public void listRedis() {
        List<BaseDict> baseDicts = baseDictMapper.selectList(new LambdaQueryWrapper<BaseDict>().isNull(BaseDict::getDeleteTime));
        Map<String, String> map = new HashMap<>();
        for (BaseDict baseDict : baseDicts) {
            String json = JSON.toJSON(baseDict).toString();
            map.put(baseDict.getType() + "|" + baseDict.getCode(), json);
        }
        redisTemplate.opsForHash().putAll(CommonConstans.BASE_DICT_REDIS, map);
    }

    private void isReDo(List<BaseDict> baseDicts) throws BusinessException {
        if (baseDicts.size() == 0) {
            throw new BusinessException("没有传入数据");
        }
        long codeCount = baseDicts.stream().map(baseDict -> baseDict.getType() + baseDict.getCode()).distinct().count();
        long valueCount = baseDicts.stream().map(baseDict -> baseDict.getType() + baseDict.getValue()).distinct().count();
        int size = baseDicts.size();
        long typeCount = baseDicts.stream().map(baseDict -> baseDict.getType()).distinct().count();

        long containRoot = baseDicts.stream().map(baseDict -> baseDict.getType()).filter(type -> type.equals("root")).count();

//        String code = baseDicts.stream().filter(baseDict -> "root".equals(baseDict.getType())).map(baseDict -> baseDict.getCode()).findAny().orElse("");
        BaseDict dict = baseDicts.stream().filter(baseDict -> "root".equals(baseDict.getType())).findAny().orElse(new BaseDict());
        if (StringUtils.isEmpty(dict.getCode())) {
            throw new BusinessException("根节点code不能为空");
        }

        long otherCount = baseDicts.stream().filter(baseDict -> dict.getCode().equals(baseDict.getType())).count();


        LambdaQueryWrapper<BaseDict> baseDictLambdaQueryWrapper = new LambdaQueryWrapper<>();

        baseDictLambdaQueryWrapper.eq(BaseDict::getCode, dict.getCode())
                .eq(BaseDict::getType, dict.getType())
                .isNull(BaseDict::getDeleteTime);
        if (CommonUtil.isNotEmpty(dict.getId())) {
            baseDictLambdaQueryWrapper.ne(BaseDict::getId, dict.getId());

        }

        Integer queryCount = baseDictMapper.selectCount(baseDictLambdaQueryWrapper);

        if (codeCount != size || valueCount != size || typeCount != 2 || containRoot != 1 || otherCount != size - 1 || queryCount.intValue() != 0) {
            throw new BusinessException("传入数据有误");
        }
    }

}
