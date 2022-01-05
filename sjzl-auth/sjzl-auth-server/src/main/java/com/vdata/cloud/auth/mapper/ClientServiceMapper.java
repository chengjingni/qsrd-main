package com.vdata.cloud.auth.mapper;

import com.vdata.cloud.auth.entity.ClientService;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

public interface ClientServiceMapper extends BaseMapper<ClientService> {
    void deleteByServiceId(int id);
}