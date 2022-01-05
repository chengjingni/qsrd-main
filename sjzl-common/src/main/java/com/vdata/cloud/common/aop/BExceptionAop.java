package com.vdata.cloud.common.aop;

import com.vdata.cloud.common.annotion.BException;
import com.vdata.cloud.common.exception.BusinessException;
import com.vdata.cloud.common.exception.CommonException;
import com.vdata.cloud.common.util.CommonUtil;
import com.vdata.cloud.common.util.ReflectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 *  * @ProjectName:    sjzl-master
 *  * @Package:        com.vdata.cloud.common.aop
 *  * @ClassName:      BExceptionAop
 *  * @Author:         Torry
 *  * @Description:    Biz异常处理
 *  * @Date:            2020/11/23 17:35
 *  * @Version:    1.0
 *  
 */
@Aspect
@Component
@Slf4j
public class BExceptionAop {

    @Pointcut(value = "@annotation(com.vdata.cloud.common.annotion.BException)")
    public void cutService() {
        log.info("BException");
    }

    @Around("cutService()")
    public Object handle(ProceedingJoinPoint point) throws Throwable {
        try {
            //先执行业务
            Object result = point.proceed();
            return result;
        } catch (BusinessException e) {
            log.error(e.getMessage());
            throw new BusinessException(e.getMessage());
        } catch (Exception e) {
            Method currentMethod = ReflectionUtils.getPointMethod(point);
            String methodName = currentMethod.getName();
            BException exception = currentMethod.getAnnotation(BException.class);
            String msg = CommonUtil.nvl(exception.value(), methodName) + "出错！";
            log.error(msg, e);
            throw new CommonException(msg);
        }

    }
}
