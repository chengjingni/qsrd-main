package com.vdata.cloud.auth.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vdata.cloud.admin.rpc.service.PermissionService;
import com.vdata.cloud.auth.common.util.jwt.IJWTInfo;
import com.vdata.cloud.client.config.ServiceAuthConfig;
import com.vdata.cloud.client.config.UserAuthConfig;
import com.vdata.cloud.client.jwt.ServiceAuthUtil;
import com.vdata.cloud.client.jwt.UserAuthUtil;
import com.vdata.cloud.common.context.BaseContextHandler;
import com.vdata.cloud.common.vo.PermissionInfo;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @ProjectName: qsrd-main
 * @Package: com.vdata.cloud.auth.interceptor
 * @ClassName: AdminInterceptor
 * @Author: HK
 * @Description: 拦截器实现
 * @Date: 2021/9/15 11:25
 * @Version: 1.0
 */

@Log4j2
public class AdminInterceptor implements HandlerInterceptor {


    @Value("${gate.ignore.startWith}")
    private String startWith;

    @Value("${jwt.expire}")
    private long jwtExpire;

    @Autowired
    private UserAuthUtil userAuthUtil;

    @Autowired
    private ServiceAuthConfig serviceAuthConfig;

    @Autowired
    private UserAuthConfig userAuthConfig;

    @Autowired
    private ServiceAuthUtil serviceAuthUtil;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PermissionService permissionService;


    public List<PermissionInfo> getAllPermission() {
        return permissionService.getAllPermission();
    }


    public List<PermissionInfo> getPermissionByUsername(@PathVariable("username") String username) {
        return permissionService.getPermissionByUsername(username);
    }


  /*  private IJWTInfo getJWTUser(ServerHttpRequest request) throws Exception {
        List<String> strings = request.getHeaders().get(userAuthConfig.getTokenHeader());
        String authToken = null;
        if (strings != null) {
            authToken = strings.get(0);
        }
        if (StringUtils.isBlank(authToken)) {
            strings = request.getQueryParams().get("token");
            if (strings != null) {
                authToken = strings.get(0);
            }
        }
        if (StringUtils.isBlank(authToken)) {
            if (request.getCookies() != null) {
                MultiValueMap<String, HttpCookie> cookies = request.getCookies();
                List<HttpCookie> list = CommonUtil.nvl(cookies.get(Constants.COOKIE_TOKEN_KEY), new ArrayList<>());
                HttpCookie httpCookie = CommonUtil.nvln(list, list.get(0));
                authToken = CommonUtil.nvln(httpCookie, httpCookie.getValue());
            }
        }
        request.header(userAuthConfig.getTokenHeader(), authToken);
        BaseContextHandler.setToken(authToken);
        return userAuthUtil.getInfoFromToken(authToken);
    }*/

    /**
     * 在请求处理之前进行调用（Controller方法调用之前）
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
//        System.out.println("执行了TestInterceptor的preHandle方法");
        try {


            String requestURI = request.getRequestURI();
            final String method = request.getMethod().toString();
            BaseContextHandler.setToken(null);
            IJWTInfo user = null;
            log.info("requestURI:" + requestURI);
            log.info("handler:" + handler);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 请求处理之后进行调用，但是在视图被渲染之前（Controller方法调用之后）
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
//         System.out.println("执行了TestInterceptor的postHandle方法");
    }

    /**
     * 在整个请求结束之后被调用，也就是在DispatcherServlet 渲染了对应的视图之后执行（主要是用于进行资源清理工作）
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
//        System.out.println("执行了TestInterceptor的afterCompletion方法");
    }
}
