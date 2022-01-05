package com.vdata.cloud.auth.controller;

import com.vdata.cloud.admin.entity.User;
import com.vdata.cloud.auth.entity.SysOperationLogEx;
import com.vdata.cloud.auth.service.AuthClientService;
import com.vdata.cloud.auth.service.AuthService;
import com.vdata.cloud.auth.util.Response;
import com.vdata.cloud.auth.util.ShiroUtils;
import com.vdata.cloud.auth.util.user.JwtAuthenticationRequest;
import com.vdata.cloud.auth.util.user.JwtTokenUtil;
import com.vdata.cloud.common.constant.Constants;
import com.vdata.cloud.common.constant.LogConstants;
import com.vdata.cloud.common.exception.BusinessException;
import com.vdata.cloud.common.msg.ObjectRestResponse;
import com.vdata.cloud.common.util.Base64Util;
import com.vdata.cloud.common.util.CommonUtil;
import com.vdata.cloud.common.util.PrivacyUtils;
import com.vdata.cloud.common.vo.DataResult;
import com.vdata.cloud.common.vo.SysOperationLogVO;
import com.vdata.cloud.common.vo.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.crazycake.shiro.RedisSessionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

//@RestController
@RequestMapping("")
@Slf4j
@CrossOrigin
public class AuthController {
    @Value("${jwt.token-header}")
    private String tokenHeader;

    @Value("${jwt.expire}")
    private int expire;

    @Autowired
    private Response response;

    @Autowired
    private AuthService authService;
    @Autowired
    private AuthClientService authClientService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;


    /*@Autowired
    private ILogService logService;*/

    @Autowired
    private MongoTemplate mongoTemplate;


    public void saveULog(SysOperationLogVO sysOperationLogVO) {
        SysOperationLogEx sysOperationLogEx = CommonUtil.copyBean(sysOperationLogVO, SysOperationLogEx.class);
        String id = UUID.randomUUID().toString().replaceAll("-", "");
        sysOperationLogEx.setId(id);
        sysOperationLogEx.setCreatetime(new Date());
//        sysOperationLogService.save(sysOperationLog);
        SysOperationLogEx save = mongoTemplate.save(sysOperationLogEx);
        log.info("保存到Mongo成功" + save);

    }

   /* @RequestMapping(value = "login")
    public Map<String, Object> createAuthenticationToken(
            @RequestBody JwtAuthenticationRequest authenticationRequest, HttpServletResponse response, HttpServletRequest request) throws Exception {
        log.info(authenticationRequest.getUsername() + " require logging...");
        Map<String, Object> map = new HashMap<>();
        try {

            UserInfo userInfo = authService.login(authenticationRequest);
            if ("0".equals(userInfo.getStatus())) {
                throw new BusinessException("用户已被禁用,请联系管理员！");
            }
            String token = userInfo.getToken();
            map.put("status", 200);
            map.put("data", token);
            map.put("userId", userInfo.getId());
            map.put("userName", userInfo.getUsername());
            map.put("name", userInfo.getNickname());
            map.put("onlyCode", userInfo.getOnlyCode());
            map.put("roleCode", userInfo.getRoleCode());
            jwtTokenUtil.setCookie(response, token, expire);

            saveULog(
                    SysOperationLogVO.builder()
                            .logtype(LogConstants.LOGIN_LOG)
                            .logname("统一用户登录")
                            .loginname(authenticationRequest.getUsername())
                            .classname(Thread.currentThread().getStackTrace()[1].getClassName())
                            .method(Thread.currentThread().getStackTrace()[1].getMethodName())
                            .succeed(LogConstants.SUCCESSED)
                            .message("登录成功")
                            .ip(CommonUtil.getIpAddr(request))
                            .build()
            );
        } catch (BusinessException e) {
            map.put("status", 401);
            map.put("message", e.getMessage());
            log.error(e.getMessage(), e);
            saveULog(
                    SysOperationLogVO.builder()
                            .logtype(LogConstants.LOGIN_LOG)
                            .logname("统一用户登录")
                            .loginname(authenticationRequest.getUsername())
                            .classname(Thread.currentThread().getStackTrace()[1].getClassName())
                            .method(Thread.currentThread().getStackTrace()[1].getMethodName())
                            .succeed(LogConstants.FAIL)
                            .message(e.getMessage())
                            .ip(CommonUtil.getIpAddr(request))
                            .build()
            );

        } catch (Exception e) {
            map.put("status", 401);
            map.put("message", "用户名密码错误");
            log.error("用户名或密码错误", e);
            saveULog(
                    SysOperationLogVO.builder()
                            .logtype(LogConstants.LOGIN_LOG)
                            .logname("统一用户登录")
                            .loginname(authenticationRequest.getUsername())
                            .classname(Thread.currentThread().getStackTrace()[1].getClassName())
                            .method(Thread.currentThread().getStackTrace()[1].getMethodName())
                            .succeed(LogConstants.FAIL)
                            .message("登录失败,用户名或密码错误")
                            .ip(CommonUtil.getIpAddr(request))
                            .build()
            );

        }
        return map;
    }*/


    @Autowired
    RedisSessionDAO redisSessionDAO;

    /*重写登录接口*/
    @RequestMapping(value = "login")
    public Map<String, Object> createAuthenticationToken(
            @RequestBody JwtAuthenticationRequest authenticationRequest, HttpServletRequest request) throws Exception {
        log.info(authenticationRequest.getUsername() + " require logging...");
        Map<String, Object> map = new HashMap<>();
        try {
/*

            UserInfo userInfo = authService.login(authenticationRequest);
            if ("0".equals(userInfo.getStatus())) {
                throw new BusinessException("用户已被禁用,请联系管理员！");
            }
            String token = userInfo.getToken();
            map.put("status", 200);
            map.put("data", token);
            map.put("userId", userInfo.getId());
            map.put("userName", userInfo.getUsername());
            map.put("name", userInfo.getNickname());
            map.put("onlyCode", userInfo.getOnlyCode());
            map.put("roleCode", userInfo.getRoleCode());
            jwtTokenUtil.setCookie(response, token, expire);
*/

            String username = authenticationRequest.getUsername();
            String password = authenticationRequest.getPassword();

            if (StringUtils.isBlank(username)) {
                throw new BusinessException("用户名为空");
            }

            if (StringUtils.isBlank(password)) {
                throw new BusinessException("密码为空");
            }

            UserInfo userInfo = authService.loginV1(authenticationRequest);
            String userId = userInfo.getId().toString();

            String token = userInfo.getToken();
            map.put("status", 200);
            map.put("data", token);
            map.put("userId", userId);
            map.put("userName", userInfo.getUsername());
            map.put("name", userInfo.getNickname());
            map.put("onlyCode", userInfo.getOnlyCode());
            map.put("roleCode", userInfo.getRoleCode());
            kickout(userId);

            saveULog(
                    SysOperationLogVO.builder()
                            .logtype(LogConstants.LOGIN_LOG)
                            .logname("统一用户登录")
                            .loginname(authenticationRequest.getUsername())
                            .classname(Thread.currentThread().getStackTrace()[1].getClassName())
                            .method(Thread.currentThread().getStackTrace()[1].getMethodName())
                            .succeed(LogConstants.SUCCESSED)
                            .message("登录成功")
                            .ip(CommonUtil.getIpAddr(request))
                            .build()
            );
        } catch (BusinessException e) {
            map.put("status", 401);
            map.put("message", e.getMessage());
            log.error(e.getMessage(), e);
            saveULog(
                    SysOperationLogVO.builder()
                            .logtype(LogConstants.LOGIN_LOG)
                            .logname("统一用户登录")
                            .loginname(authenticationRequest.getUsername())
                            .classname(Thread.currentThread().getStackTrace()[1].getClassName())
                            .method(Thread.currentThread().getStackTrace()[1].getMethodName())
                            .succeed(LogConstants.FAIL)
                            .message(e.getMessage())
                            .ip(CommonUtil.getIpAddr(request))
                            .build()
            );

        } catch (Exception e) {
            map.put("status", 401);
            map.put("message", "用户名密码错误");
            log.error("用户名或密码错误", e);
            saveULog(
                    SysOperationLogVO.builder()
                            .logtype(LogConstants.LOGIN_LOG)
                            .logname("统一用户登录")
                            .loginname(authenticationRequest.getUsername())
                            .classname(Thread.currentThread().getStackTrace()[1].getClassName())
                            .method(Thread.currentThread().getStackTrace()[1].getMethodName())
                            .succeed(LogConstants.FAIL)
                            .message("登录失败,用户名或密码错误")
                            .ip(CommonUtil.getIpAddr(request))
                            .build()
            );

        }
        return map;
    }

    private void kickout(String userId) {
        Serializable id = ShiroUtils.getSession().getId();
        for (Session obj : redisSessionDAO.getActiveSessions()) {
            if (!obj.getId().equals(id)) {
                for (Object key : obj.getAttributeKeys()) {
                    if (obj.getAttribute(key) instanceof SimplePrincipalCollection) {
                        SimplePrincipalCollection simplePrincipalCollection = (SimplePrincipalCollection) obj.getAttribute(key);
                        List asList = simplePrincipalCollection.asList();
                        for (int i = 0; i < asList.size(); i++) {
                            if (asList.get(i) instanceof User) {
                                if (((User) asList.get(i)).getId().toString().equals(userId)) {
                                    log.info("踢出当前用户之前登录：" + userId);
                                    obj.setTimeout(0);
                                    redisSessionDAO.update(obj);
                                }
                            }
                        }
                    }
                }
            }

        }


    }

    @RequestMapping(value = "un_auth")
    public Response unAuth(
    ) throws Exception {
        return response.failure(HttpStatus.UNAUTHORIZED, "用户未登录！或异地登录", null);
    }


    @RequestMapping(value = "v2/login")
    public Map<String, Object> createAuthenticationToken(
            @RequestBody JwtAuthenticationRequest authenticationRequest, HttpServletResponse response, HttpSession session, HttpServletRequest request) throws Exception {
        log.info(authenticationRequest.getUsername() + " require logging...");
        Map<String, Object> map = new HashMap<>();
        try {
            //验证验证码是否正确
            /*session.setAttribute("code", "1111");*/
       /*     if (!verifyVerificationCode(session, checkcode)) {
                new BusinessException("验证码错误");
            }*/
            UserInfo userInfo = authService.login(authenticationRequest);
            if ("0".equals(userInfo.getStatus())) {
                throw new BusinessException("用户已被禁用,请联系管理员！");
            }
            String token = userInfo.getToken();
            map.put("status", 200);
            map.put("data", token);
            map.put("userId", userInfo.getId());
            map.put("userName", userInfo.getUsername());
            map.put("name", userInfo.getNickname());
            //map.put("indexUrl", userInfo.getIndexUrl());
            //map.put("baseUserSystemList",userInfo.getBaseUserSystemList());
            jwtTokenUtil.setCookie(response, token, expire);

            saveULog(
                    SysOperationLogVO.builder()
                            .logtype(LogConstants.LOGIN_LOG)
                            .logname("数据中心登录")
                            .loginname(authenticationRequest.getUsername())
                            .classname(Thread.currentThread().getStackTrace()[1].getClassName())
                            .method(Thread.currentThread().getStackTrace()[1].getMethodName())
                            .succeed(LogConstants.SUCCESSED)
                            .message("登录成功")
                            .ip(CommonUtil.getIpAddr(request))
                            .build()
            );
        } catch (BusinessException e) {
            map.put("status", 401);
            map.put("message", e.getMessage());
            log.error(e.getMessage(), e);
            saveULog(
                    SysOperationLogVO.builder()
                            .logtype(LogConstants.LOGIN_LOG)
                            .logname("数据中心登录")
                            .loginname(authenticationRequest.getUsername())
                            .classname(Thread.currentThread().getStackTrace()[1].getClassName())
                            .method(Thread.currentThread().getStackTrace()[1].getMethodName())
                            .succeed(LogConstants.FAIL)
                            .message(e.getMessage())
                            .ip(CommonUtil.getIpAddr(request))
                            .build()
            );
        } catch (Exception e) {
            map.put("status", 401);
            map.put("message", "用户名密码错误");
            log.error(e.getMessage(), e);
            saveULog(
                    SysOperationLogVO.builder()
                            .logtype(LogConstants.LOGIN_LOG)
                            .logname("数据中心登录")
                            .loginname(authenticationRequest.getUsername())
                            .classname(Thread.currentThread().getStackTrace()[1].getClassName())
                            .method(Thread.currentThread().getStackTrace()[1].getMethodName())
                            .succeed(LogConstants.FAIL)
                            .message("用户名或密码错误")
                            .ip(CommonUtil.getIpAddr(request))
                            .build()
            );
        }
        return map;
    }


    private boolean verifyVerificationCode(HttpSession session, String checkcode)
            throws ServletException, IOException, BusinessException {
        String code = (String) session.getAttribute("code");
        //System.out.println("code:" + code);
        //System.out.println("checkcode:" + checkcode);
        if (CommonUtil.isEmpty(code) || CommonUtil.isEmpty(checkcode)) {
            throw new BusinessException("验证码为空");
        }
        if (code.toLowerCase().equals(checkcode.toLowerCase())) {
            return true;
        }
        return false;
    }

    @RequestMapping(value = "getUserByToken", method = RequestMethod.POST)
    public DataResult getUserByToken(
            @RequestBody Map<String, Object> param, HttpServletResponse response) throws Exception {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        DataResult result = new DataResult();
        try {
            authService.validate(CommonUtil.objToStr(param.get("token")));
            Map<String, Object> resMap = authService.getUserByToken(param);
            result.setData(resMap);
            result.setCode(Constants.RETURN_NORMAL);
            result.setMessage("查询成功!");
        } catch (Exception e) {
            log.error("token错误", e);
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage("token失效");
        }
        return result;
    }

    @RequestMapping(value = "getPlatformAuthorization", method = RequestMethod.GET)
    public DataResult getPlatformAuthorization() throws SQLException, UnsupportedEncodingException {
        DataResult result = new DataResult();
        HashMap<String, Object> retMap = authClientService.getPlatformAuthorization();
        if (!CommonUtil.isEmpty(retMap)) {
            try {
                String type = Base64Util.decode(new String((byte[]) retMap.get("type")));
                String day = Base64Util.decode(new String((byte[]) retMap.get("day")));
                if (type.equals("87d9c68f2a") && isPastDate(day)) {
                    type = "62c9aea9ac";
                }
                String randomCode = CommonUtil.randomStr(type.length(), false);
                type = PrivacyUtils.encrypt(type, randomCode);
                HashMap<String, Object> rMap = new HashMap<>();
                rMap.put("type", type);
                rMap.put("code", randomCode);
                rMap.put("des", CommonUtil.objToStr(retMap.get("des")));
                result.setData(rMap);
            } catch (Exception e) {
                HashMap<String, Object> rMap = new HashMap<>();
                rMap.put("des", CommonUtil.objToStr(retMap.get("des")));
                result.setData(rMap);
                result.setCode("01");
                result.setMessage("错误的授权信息！");
            }
        } else {
            result.setCode("01");
            result.setMessage("错误的授权信息！");
        }

        return result;
    }

    public static boolean isPastDate(String str) {

        boolean flag = false;
        Date nowDate = new Date();
        Date pastDate = null;
        //格式化日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        //在日期字符串非空时执行
        if (str != null && !"".equals(str)) {
            try {
                //将字符串转为日期格式，如果此处字符串为非合法日期就会抛出异常。
                pastDate = sdf.parse(str + " 23:59:59");
                //调用Date里面的before方法来做判断
                flag = pastDate.before(nowDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("日期参数不可为空");
        }
        return flag;
    }

    @RequestMapping(value = "updatePassWord", method = RequestMethod.POST)
    public DataResult updatePassWord(
            @RequestBody Map<String, Object> param, HttpServletResponse response) throws Exception {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        DataResult result = new DataResult();
        try {
            authService.validate(CommonUtil.objToStr(param.get("token")));
            boolean b = authService.updatePassWord(param);
            result.setData(b);
            result.setCode(Constants.RETURN_NORMAL);
            result.setMessage("修改成功!");
        } catch (Exception e) {
            log.error("token错误", e);
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage("token失效");
        }
        return result;
    }

    @RequestMapping(value = "refresh", method = RequestMethod.GET)
    public ObjectRestResponse<String> refreshAndGetAuthenticationToken(
            HttpServletRequest request) throws Exception {
        String token = request.getHeader(tokenHeader);
        String refreshedToken = authService.refresh(token);
        return new ObjectRestResponse<>().data(refreshedToken);
    }

    @RequestMapping(value = "verify", method = RequestMethod.GET)
    public ObjectRestResponse<?> verify(String token) throws Exception {
        try {
            authService.validate(token);
            return new ObjectRestResponse<>(Constants.RETURN_NORMAL, "token校验成功！");
        } catch (Exception e) {
            log.error("token校验出错！", e);
            return new ObjectRestResponse<>(Constants.RETURN_UNNORMAL, "token校验失败！");
        }

    }

    /*   @RequestMapping(value = "logout", method = RequestMethod.POST)
       public ObjectRestResponse<?> invalid(@RequestParam("token") String token, HttpServletResponse response,
                                            HttpServletRequest request,
                                            @RequestParam(value = "userId", required = false) String userId,
                                            @RequestParam(value = "username", required = false) String username) throws Exception {
           response.setHeader("Access-Control-Allow-Origin", "*");
           response.setHeader("Access-Control-Allow-Credentials", "true");
           try {
               authService.invalid(token);
               jwtTokenUtil.setCookie(response, null, 0);
               return new ObjectRestResponse<>(Constants.RETURN_NORMAL, "退出登录成功！");
           } catch (Exception e) {
               return new ObjectRestResponse<>(Constants.RETURN_NORMAL, "退出登录失败！");
           }

       }
   */

    /*退出登录重写*/
    @RequestMapping(value = "logout", method = RequestMethod.POST)
    public ObjectRestResponse<?> invalid() throws Exception {

        try {
            Subject subject = SecurityUtils.getSubject();
            subject.logout();
            return new ObjectRestResponse<>(Constants.RETURN_NORMAL, "退出登录成功！");
        } catch (Exception e) {
            log.error("logout error ...", e);
            return new ObjectRestResponse<>(Constants.RETURN_NORMAL, "退出登录失败！");
        }

    }

    public String errorToString(Exception e) {
        StringWriter sw = new StringWriter();
        try (PrintWriter pw = new PrintWriter(sw);) {
            e.printStackTrace(pw);
        }
        String errorInfo = sw.toString();
        return errorInfo;
    }
}
