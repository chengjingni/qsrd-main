package com.vdata.cloud.datacenter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.vdata.cloud.datacenter.entity.AbnormalDetail;
import com.vdata.cloud.datacenter.vo.AbnormalDetailVO;
import org.apache.ibatis.annotations.Param;

/**
 * @author hk
 * @date 2021-07-21 14:43:16
 */
public interface AbnormalDetailMapper extends BaseMapper<AbnormalDetail> {

    IPage<AbnormalDetailVO> abnormalDetailVOIPage(@Param("abnormalDetailVOIPage") IPage<AbnormalDetailVO> abnormalDetailVOIPage, @Param("id") Integer id);

}
