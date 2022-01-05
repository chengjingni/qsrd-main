package com.vdata.cloud.auth.biz;

import com.vdata.cloud.auth.entity.Client;
import com.vdata.cloud.auth.entity.ClientService;
import com.vdata.cloud.auth.mapper.ClientMapper;
import com.vdata.cloud.auth.mapper.ClientServiceMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vdata.cloud.common.rest.BaseBiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author Mr.AG
 * @email 463540703@qq.com
 * @date 2017-12-26 19:43:46
 */
@Service
public class ClientBiz extends BaseBiz<ClientMapper, Client> {
    @Autowired
    private ClientServiceMapper clientServiceMapper;
    @Autowired
    private ClientMapper clientMapper;
    @Autowired
    private ClientServiceBiz clientServiceBiz;

    public List<Client> getClientServices(int id) {
        return clientMapper.selectAuthorityServiceInfo(id);
    }

    public void modifyClientServices(int id, String clients) {
        clientServiceMapper.deleteByServiceId(id);
        if (!StringUtils.isEmpty(clients)) {
            String[] mem = clients.split(",");
            for (String m : mem) {
                ClientService clientService = new ClientService();
                clientService.setServiceId(m);
                clientService.setClientId(id + "");
                clientServiceBiz.save(clientService);
            }
        }
    }
}