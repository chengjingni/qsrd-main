package com.vdata.cloud.auth.service;


import com.vdata.cloud.auth.util.user.JwtAuthenticationRequest;
import com.vdata.cloud.common.vo.UserInfo;

import java.util.Map;

public interface AuthService {
    UserInfo login(JwtAuthenticationRequest authenticationRequest) throws Exception;

    String refresh(String oldToken) throws Exception;

    void validate(String token) throws Exception;

    void invalid(String token) throws Exception;

    Map<String, Object> getUserByToken(Map<String, Object> param) throws Exception;

    boolean updatePassWord(Map<String, Object> param) throws Exception;

    UserInfo loginV1(JwtAuthenticationRequest authenticationRequest);
}
