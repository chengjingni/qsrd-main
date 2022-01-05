package com.vdata.cloud.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.vdata.cloud.auth.bean.ClientInfo;
import com.vdata.cloud.auth.entity.Client;
import com.vdata.cloud.auth.mapper.ClientMapper;
import com.vdata.cloud.auth.service.AuthClientService;
import com.vdata.cloud.auth.util.client.ClientTokenUtil;
import com.vdata.cloud.common.exception.auth.ClientInvalidException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ace on 2017/9/10.
 */
@Slf4j
@Service
public class DBAuthClientService implements AuthClientService {
    @Autowired
    private ClientMapper clientMapper;
    @Autowired
    private ClientTokenUtil clientTokenUtil;

    private ApplicationContext context;

    @Autowired
    public DBAuthClientService(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public String apply(String clientId, String secret) throws Exception {
        Client client = getClient(clientId, secret);
        return clientTokenUtil.generateToken(new ClientInfo(client.getCode(), client.getName(), client.getId().toString()));
    }

    private Client getClient(String clientId, String secret) {
        LambdaQueryWrapper<Client> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Client::getCode, clientId);
        Client client = clientMapper.selectOne(wrapper);
        if (client == null || !client.getSecret().equals(secret)) {
            throw new ClientInvalidException("Client not found or Client secret is error!");
        }
        return client;
    }

    @Override
    public void validate(String clientId, String secret) throws Exception {
        LambdaQueryWrapper<Client> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Client::getCode, clientId);
        Client client = clientMapper.selectOne(wrapper);
        if (client == null || !client.getSecret().equals(secret)) {
            throw new ClientInvalidException("Client not found or Client secret is error!");
        }
    }

    @Override
    public HashMap<String, Object> getPlatformAuthorization() {
        return clientMapper.getPlatformAuthorization();
    }

    @Override
    public List<String> getAllowedClient(String clientId, String secret) {
        Client info = this.getClient(clientId, secret);
        List<String> clients = clientMapper.selectAllowedClient(info.getId() + "");
        if (clients == null) {
            return new ArrayList<String>();
        }
        return clients;
    }

    @Override
    public List<String> getAllowedClient(String serviceId) {
        Client info = getClient(serviceId);
        List<String> clients = clientMapper.selectAllowedClient(info.getId() + "");
        if (clients == null) {
            return new ArrayList<String>();
        }
        return clients;
    }

    @Override
    public void registryClient() {

    }

    private Client getClient(String clientId) {
        LambdaQueryWrapper<Client> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Client::getCode, clientId);
        Client client = clientMapper.selectOne(wrapper);
        return client;
    }


}
