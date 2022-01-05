package com.vdata.cloud.datacenter.util;

import com.vdata.cloud.auth.common.util.jwt.IJWTInfo;
import com.vdata.cloud.client.jwt.UserAuthUtil;
import com.vdata.cloud.common.constant.LogConstants;
import com.vdata.cloud.common.util.CommonUtil;
import com.vdata.cloud.datacenter.entity.SysOperationLog;
import com.vdata.cloud.datacenter.mapper.SysOperationLogMapper;
import com.vdata.cloud.datacenter.service.ISysOperationLogService;
import com.vdata.cloud.datacenter.vo.BaseGroupVO;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

/**
 * @ProjectName: wru-master
 * @Package: com.vdata.cloud.datacenter.util
 * @ClassName: ULogUtils
 * @Author: HK
 * @Description:
 * @Date: 2020/11/3 15:44
 * @Version: 1.0
 */
@Log4j2
@Component
public class ULogUtils {
    @Autowired
    private ISysOperationLogService sysOperationLogService;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private UserAuthUtil userAuthUtil;

    @Autowired
    private SysOperationLogMapper sysOperationLogMapper;

    public void save(String message) {

        sysOperationLogService.save(SysOperationLog.builder()
                .logtype(LogConstants.ERROR_LOG)
                .message(message)
                .succeed(LogConstants.FAIL)
                .message(message).build());

    }

    public void save(String message, Thread thread) {

        sysOperationLogService.save(SysOperationLog.builder()
                .logtype(LogConstants.ERROR_LOG)
                .classname(Thread.currentThread().getStackTrace()[1].getClassName())
                .method(Thread.currentThread().getStackTrace()[1].getMethodName())
                .message(message)
                .succeed(LogConstants.FAIL)
                .message(message).build());

    }

    @Async("asyncTaskExecutor")
    public void save(String message, Thread thread, HttpServletRequest request, Exception e) {
        Cookie[] cookies = request.getCookies();
        String username = null;
        String userId = null;

        if (CommonUtil.isNotEmpty(cookies)) {
            Optional<Cookie> first = Arrays.stream(cookies).filter(cookie -> "Admin-Token".equals(cookie.getName())).findFirst();

            try {
                if (first.isPresent()) {
                    IJWTInfo infoFromToken = userAuthUtil.getInfoFromToken(first.get().getValue());
                    username = infoFromToken.getUniqueName();
                    userId = infoFromToken.getId();
                    log.info("username:" + username);
                    log.info("id:" + userId);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }


        SysOperationLog sysOperationLog = SysOperationLog.builder()
                .logtype(LogConstants.ERROR_LOG)
                .classname(Thread.currentThread().getStackTrace()[1].getClassName())
                .method(Thread.currentThread().getStackTrace()[1].getMethodName())
                .loginname(message)
                .succeed(LogConstants.FAIL)
//                .ip(IPUtils.getIpAddress(request))
                .ip(CommonUtil.getIpAddr(request))
                .loginname(username)
                .userid(userId)
                .id(UUID.randomUUID().toString().replaceAll("-", ""))
                .createtime(new Date())
                .message(errorToString(e)).build();

        if (!StringUtils.isEmpty(userId)) {
            BaseGroupVO baseGroupVO = sysOperationLogMapper.getBaseGroupVO(userId);
            if (baseGroupVO != null) {
                sysOperationLog.setFkBaseGroup(baseGroupVO.getFkBaseGroup());
            }
        }
//        sysOperationLogService.save(sysOperationLog);
        mongoTemplate.save(sysOperationLog);

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
