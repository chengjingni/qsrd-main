package com.vdata.cloud.auth.service.impl;

import com.vdata.cloud.admin.entity.User;
import com.vdata.cloud.admin.rpc.service.PermissionService;
import com.vdata.cloud.admin.vo.FrontUser;
import com.vdata.cloud.auth.common.util.jwt.JWTInfo;
import com.vdata.cloud.auth.service.AuthService;
import com.vdata.cloud.auth.util.user.JwtAuthenticationRequest;
import com.vdata.cloud.auth.util.user.JwtTokenUtil;
import com.vdata.cloud.client.jwt.UserAuthUtil;
import com.vdata.cloud.common.exception.auth.UserInvalidException;
import com.vdata.cloud.common.exception.auth.UserTokenException;
import com.vdata.cloud.common.util.CommonUtil;
import com.vdata.cloud.common.util.MD5Utils;
import com.vdata.cloud.common.vo.UserInfo;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    @NonNull
    private JwtTokenUtil jwtTokenUtil;
    /*
        @NonNull
        private IUserService userService;


    */
    @Autowired
    private PermissionService permissionService;

    @Autowired
    private UserAuthUtil userAuthUtil;

    public UserInfo validate(JwtAuthenticationRequest authenticationRequest) {
//        return permissionService.validate(body.get("username"), body.get("password"));
        return permissionService.validate(authenticationRequest.getUsername(), authenticationRequest.getPassword());
    }


    public Map<String, Object> getUserByTokenEx(Map<String, Object> param) throws Exception {
        FrontUser u = permissionService.getUserInfo(CommonUtil.objToStr(param.get("token")));
        Map<String, Object> map = new HashMap<>();
        if (CommonUtil.isNotEmpty(u.getUsername())) {
            map.put("userName", u.getUsername());
        } else {
            map.put("userName", "");
        }
        return map;

    }


    public boolean updatePassWordEx(Map<String, String> param) throws Exception {
        FrontUser u = permissionService.getUserInfo(CommonUtil.objToStr(param.get("token")));
        param.put("userName", u.getUsername());
        return permissionService.updatePassWord(param);
    }

    @Override
    public UserInfo login(JwtAuthenticationRequest authenticationRequest) throws Exception {
        UserInfo info = validate(authenticationRequest);
        if (!StringUtils.isEmpty(info.getId())) {
            String token = jwtTokenUtil.generateToken(new JWTInfo(info.getUsername(), info.getId() + "", info.getNickname()));
            info.setToken(token);
            return info;
        }
        throw new UserInvalidException("用户不存在或账户密码错误!");
    }

    @Override
    public void validate(String token) throws Exception {
        jwtTokenUtil.getInfoFromToken(token);
    }

    @Override
    public void invalid(String oldToken) throws Exception {
        try {
            jwtTokenUtil.invalid(oldToken);
        } catch (UserTokenException e) {
            log.error("", e);
        }

    }

    @Override
    public String refresh(String oldToken) throws Exception {
        return jwtTokenUtil.generateToken(jwtTokenUtil.getInfoFromToken(oldToken));
    }

    @Override
    public Map<String, Object> getUserByToken(Map<String, Object> param) throws Exception {
        return getUserByTokenEx(param);

    }

    @Override
    public boolean updatePassWord(Map<String, Object> param) throws Exception {
        Map<String, String> map = new HashMap<>();
        for (String key : param.keySet()) {
            map.put(key, param.get(key).toString());
        }

        return updatePassWordEx(map);

    }

    @Override
    public UserInfo loginV1(JwtAuthenticationRequest authenticationRequest) {

        log.info("开始登录...");

        //获得Subject实例对象
        Subject currentUser = SecurityUtils.getSubject();

        //将用户名和密码封存
        UsernamePasswordToken token = new UsernamePasswordToken(authenticationRequest.getUsername(), MD5Utils.MD5Lower(authenticationRequest.getPassword()));
        UserInfo userInfo = new UserInfo();
        currentUser.login(token);
        User user = (User) currentUser.getPrincipals().getPrimaryPrincipal();
        BeanUtils.copyProperties(user, userInfo);
        userInfo.setId(user.getId().toString());
        log.info("userInfo is{}", userInfo.toString());

        return userInfo;
    }
}
