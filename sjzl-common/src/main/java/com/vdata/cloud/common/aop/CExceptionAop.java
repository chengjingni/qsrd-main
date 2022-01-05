package com.vdata.cloud.common.aop;

import com.vdata.cloud.common.annotion.CException;
import com.vdata.cloud.common.constant.Constants;
import com.vdata.cloud.common.exception.BusinessException;
import com.vdata.cloud.common.util.CommonUtil;
import com.vdata.cloud.common.util.ReflectionUtils;
import com.vdata.cloud.common.vo.DataResult;
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
 *  * @Description:    Controller异常处理
 *  * @Date:            2020/11/23 17:35
 *  * @Version:    1.0
 *  
 */
@Aspect
@Component
@Slf4j
public class CExceptionAop {

    @Pointcut(value = "@annotation(com.vdata.cloud.common.annotion.CException)")
    public void cutService() {
        log.info("CException");
    }

    @Around("cutService()")
    public Object handle(ProceedingJoinPoint point) throws Throwable {
        try {
            Object result = point.proceed();
            String code = CommonUtil.getFieldValue(result, "code");
            if (CommonUtil.isNotEmpty(code)) {
                return result;
            } else {
                Method currentMethod = ReflectionUtils.getPointMethod(point);
                CException exception = currentMethod.getAnnotation(CException.class);
                code = Constants.RETURN_NORMAL;
                String message = CommonUtil.nvl(exception.value(), "操作") + "成功！";
                CommonUtil.setFieldValue(result, "message", message);
                CommonUtil.setFieldValue(result, "code", code);
                return result;
            }
        } catch (BusinessException e) {
            return new DataResult<>(Constants.RETURN_UNNORMAL, e.getMessage());
        } catch (Exception e) {
            Method currentMethod = ReflectionUtils.getPointMethod(point);
            CException exception = currentMethod.getAnnotation(CException.class);
            String msg = CommonUtil.nvl(exception.value(), "操作") + "出错！";
            log.error(currentMethod.getName() + ":" + e.getMessage());
            return new DataResult<>(Constants.RETURN_UNNORMAL, msg);
        }

    }

}
