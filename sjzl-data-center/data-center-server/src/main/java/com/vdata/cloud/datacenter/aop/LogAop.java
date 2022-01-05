package com.vdata.cloud.datacenter.aop;


import com.vdata.cloud.admin.entity.User;
import com.vdata.cloud.auth.util.ShiroUtils;
import com.vdata.cloud.client.jwt.UserAuthUtil;
import com.vdata.cloud.common.annotion.BussinessLog;
import com.vdata.cloud.common.constant.LogConstants;
import com.vdata.cloud.common.util.CommonUtil;
import com.vdata.cloud.datacenter.entity.SysOperationLog;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.UUID;

/**
 * 日志记录
 *
 * @author liufang
 * @version 1.0
 * @date 2019/6/25 13:35
 */
@Aspect
@Component
@Log4j2
public class LogAop {

    @Autowired
    UserAuthUtil userAuthUtil;


    @Autowired
    private MongoTemplate mongoTemplate;


    @Pointcut(value = "@annotation(com.vdata.cloud.common.annotion.BussinessLog)")
    public void cutService() {
    }

    @Around("cutService()")
    public Object recordSysLog(ProceedingJoinPoint point) throws Throwable {

        //先执行业务
        Object result = point.proceed();

        try {
            handle(point);
        } catch (Exception e) {
            log.error("日志记录出错!", e);
        }

        return result;
    }

    private void handle(ProceedingJoinPoint point) throws Exception {

        //获取拦截的方法名
        Signature sig = point.getSignature();
        MethodSignature msig = null;
        if (!(sig instanceof MethodSignature)) {
            throw new IllegalArgumentException("该注解只能用于方法");
        }
        msig = (MethodSignature) sig;
        Object target = point.getTarget();
        Method currentMethod = target.getClass().getMethod(msig.getName(), msig.getParameterTypes());
        String methodName = currentMethod.getName();
        log.info(methodName);
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        Cookie[] cookies = request.getCookies();
        String username = null;
        String userId = null;
      /*  if (CommonUtil.isNotEmpty(cookies)) {
            Optional<Cookie> first = Arrays.stream(cookies).filter(cookie -> "Admin-Token".equals(cookie.getName())).findFirst();

            if (first.isPresent()) {
                IJWTInfo infoFromToken = userAuthUtil.getInfoFromToken(first.get().getValue());
                username = infoFromToken.getUniqueName();
                userId = infoFromToken.getId();
                log.info("username:" + username);
                log.info("id:" + userId);
            }
        }
*/

        /*注释获取用户信息内容*/
/*
        User userEntity = ShiroUtils.getUserEntity();
        username = userEntity.getUsername();
        userId = userEntity.getId().toString();
*/

        //获取拦截方法的参数
        String className = point.getTarget().getClass().getName();
        Object[] params = point.getArgs();

        //获取操作名称
        BussinessLog annotation = currentMethod.getAnnotation(BussinessLog.class);
        String bussinessName = annotation.value();

        StringBuilder sb = new StringBuilder();
        for (Object param : params) {
            sb.append(param);
            sb.append(" & ");
        }

        SysOperationLog sysOperationLog = SysOperationLog.builder()
                .logtype(LogConstants.U_LOG)
                .logname(bussinessName)
//                .ip(IPUtils.getIpAddress(request))
                .ip(CommonUtil.getIpAddr(request))
                .classname(className)
                .method(methodName)
                .message(sb.toString())
                .succeed(LogConstants.SUCCESSED)
                .loginname(username)
                .userid(userId)
                .id(UUID.randomUUID().toString().replaceAll("-", ""))
                .createtime(new Date())
                .build();


        mongoTemplate.save(sysOperationLog);

    }
}
