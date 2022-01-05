package com.vdata.cloud.client.runner;

import com.vdata.cloud.auth.configuration.KeyConfiguration;
import com.vdata.cloud.auth.service.AuthClientService;
import com.vdata.cloud.client.config.ServiceAuthConfig;
import com.vdata.cloud.client.config.UserAuthConfig;
import com.vdata.cloud.common.msg.ObjectRestResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class AuthClientRunner implements CommandLineRunner {

    @NonNull
    private ServiceAuthConfig serviceAuthConfig;
    @NonNull
    private UserAuthConfig userAuthConfig;
 /*   @NonNull
    private ServiceAuthFeign serviceAuthFeign;*/

    @Autowired
    private AuthClientService authClientService;
    @Autowired
    private KeyConfiguration keyConfiguration;


    public ObjectRestResponse<byte[]> getServicePublicKey(String clientId, String secret) throws Exception {
        authClientService.validate(clientId, secret);
        return new ObjectRestResponse<byte[]>().data(keyConfiguration.getServicePubKey());
    }

    public ObjectRestResponse<byte[]> getUserPublicKey(String clientId, String secret) throws Exception {
        authClientService.validate(clientId, secret);
        return new ObjectRestResponse<byte[]>().data(keyConfiguration.getUserPubKey());
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("初始化加载用户pubKey");
        try {
            refreshUserPubKey();
        } catch (Exception e) {
            log.error("初始化加载用户pubKey失败,1分钟后自动重试!", e);
        }
        log.info("初始化加载客户pubKey");
        try {
            refreshServicePubKey();
        } catch (Exception e) {
            log.error("初始化加载客户pubKey失败,1分钟后自动重试!", e);
        }
    }

    @Scheduled(cron = "0 0/1 * * * ?")
    public void refreshUserPubKey() throws Exception {
        ObjectRestResponse<byte[]> userResponse = getUserPublicKey(serviceAuthConfig.getClientId(), serviceAuthConfig.getClientSecret());
        if (userResponse.getStatus() == HttpStatus.OK.value()) {
            this.userAuthConfig.setPubKeyByte(userResponse.getData());
        }
    }

    @Scheduled(cron = "0 0/1 * * * ?")
    public void refreshServicePubKey() throws Exception {
        ObjectRestResponse<byte[]> serviceResponse = getServicePublicKey(serviceAuthConfig.getClientId(), serviceAuthConfig.getClientSecret());
        if (serviceResponse.getStatus() == HttpStatus.OK.value()) {
            this.serviceAuthConfig.setPubKeyByte(serviceResponse.getData());
        }
    }

}