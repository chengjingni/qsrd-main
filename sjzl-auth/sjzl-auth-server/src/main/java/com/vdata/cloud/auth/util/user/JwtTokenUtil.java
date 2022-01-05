package com.vdata.cloud.auth.util.user;

import com.vdata.cloud.auth.common.constatns.CommonConstants;
import com.vdata.cloud.auth.configuration.KeyConfiguration;
import com.vdata.cloud.auth.common.util.jwt.IJWTInfo;
import com.vdata.cloud.auth.common.util.jwt.JWTHelper;
import com.vdata.cloud.common.constant.Constants;
import com.vdata.cloud.common.exception.auth.UserTokenException;
import com.vdata.cloud.common.util.CommonUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;


@Component
@Slf4j
@RequiredArgsConstructor
public class JwtTokenUtil {

    @Value("${jwt.expire}")
    private int expire;
    @NonNull
    private KeyConfiguration keyConfiguration;

    @NonNull
    private RedisTemplate redisTemplate;

    public String generateToken(IJWTInfo jwtInfo) throws Exception {
        String token = JWTHelper.generateToken(jwtInfo, keyConfiguration.getUserPriKey(), expire);
        redisTemplate.opsForValue().set(CommonConstants.JWT_KEY_USER_ID + "_" + jwtInfo.getId(), token, expire, TimeUnit.SECONDS);
        return token;
    }

    public IJWTInfo getInfoFromToken(String token) throws Exception {
        IJWTInfo jwtInfo = JWTHelper.getInfoFromToken(token, keyConfiguration.getUserPubKey());
        String oritoken = (String) redisTemplate.opsForValue().get(CommonConstants.JWT_KEY_USER_ID + "_" + jwtInfo.getId());
        if (CommonUtil.isEmpty(oritoken)) {
            throw new UserTokenException("用户已注销，请重新登陆!");
        } else if (!token.equals(oritoken)) {
            throw new UserTokenException("用户token已失效!");
        }
        return jwtInfo;
    }


    public void invalid(String oldToken) throws Exception {
        IJWTInfo jwtInfo = getInfoFromToken(oldToken);
        redisTemplate.delete(CommonConstants.JWT_KEY_USER_ID + "_" + jwtInfo.getId());
    }

    public void setCookie(HttpServletResponse response, String token, int maxAge) {
        try {
            // new一个Cookie对象,键值对为参数
            Cookie cookie = new Cookie(Constants.COOKIE_TOKEN_KEY, token);
            // 多应用共享
            cookie.setPath("/");
            cookie.setMaxAge(maxAge);
            // 将Cookie添加到Response中,使之生效
            response.addCookie(cookie); // addCookie后，如果已经存在相同名字的cookie，则最新的覆盖旧的cookie
        } catch (Exception e) {
            log.error("Cookie设置出错！", e);
        }
    }

}
