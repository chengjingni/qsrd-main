package com.vdata.cloud.common.handler;

import com.vdata.cloud.common.constant.Constants;
import com.vdata.cloud.common.exception.BaseException;
import com.vdata.cloud.common.exception.auth.ClientTokenException;
import com.vdata.cloud.common.exception.auth.UserInvalidException;
import com.vdata.cloud.common.exception.auth.UserTokenException;
import com.vdata.cloud.common.msg.BaseResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by ace on 2017/9/8.
 */
@ControllerAdvice("com.github.wxiaoqi.security")
@ResponseBody
public class GlobalExceptionHandler {
    private Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ClientTokenException.class)
    public BaseResponse clientTokenExceptionHandler(HttpServletResponse response, ClientTokenException ex) {
        response.setStatus(403);
        logger.error(ex.getMessage(), ex);
        return new BaseResponse(ex.getStatus(), ex.getMessage());
    }

    @ExceptionHandler(UserTokenException.class)
    public BaseResponse userTokenExceptionHandler(HttpServletResponse response, UserTokenException ex) {
        response.setStatus(200);
        logger.error(ex.getMessage(), ex);
        return new BaseResponse(ex.getStatus(), ex.getMessage());
    }

    @ExceptionHandler(UserInvalidException.class)
    public BaseResponse userInvalidExceptionHandler(HttpServletResponse response, UserInvalidException ex) {
        response.setStatus(200);
        logger.error(ex.getMessage(), ex);
        return new BaseResponse(ex.getStatus(), ex.getMessage());
    }

    @ExceptionHandler(BaseException.class)
    public BaseResponse baseExceptionHandler(HttpServletResponse response, BaseException ex) {
        logger.error(ex.getMessage(), ex);
        response.setStatus(500);
        return new BaseResponse(ex.getStatus(), ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public BaseResponse otherExceptionHandler(HttpServletResponse response, Exception ex) {
        response.setStatus(500);
        logger.error(ex.getMessage(), ex);
        //        MyLogUtils.Save();


//        //如果当前用户未登录，不做日志
//        UserEntity user = null;
//        RoomerReg roomerReg = null;
//        Subject subject = SecurityUtils.getSubject();
//        Object object = subject.getPrincipal();
//        if (object.getClass().equals(UserEntity.class)) {
//            user = (UserEntity) object;
//        }
//        if (object.getClass().equals(RoomerReg.class)) {
//            roomerReg = (RoomerReg) object;
//        }
//        String userid = "";
//        String fromType = "";
//        String fkBase = "";
//
//        if (user != null) {
////            userid=user.getUserId();
//            userid = user.getUserName();
//            fromType = "物业端";
//            fkBase = user.getFkBase();
//        }
//        if (roomerReg != null) {
////          userid=roomerReg.getRegRoomerId();
//            userid = roomerReg.getName();
//            fromType = "居民端";
//        }
//
//        SysOperationLogService sysOperationLogService = SpringContextUtil.getBean(SysOperationLogService.class);
//        SysOperationLogEntity sysOperationLogEntity = new SysOperationLogEntity();
//        sysOperationLogEntity.setId(StringUtil.randomStr(32, false));
//        sysOperationLogEntity.setUserid(userid);
////        sysOperationLogEntity.setLogname(bussinessName);
//        sysOperationLogEntity.setLogtype(LogType.EXCEPTION.getMessage());
////        sysOperationLogEntity.setMethod(methodName);
//        sysOperationLogEntity.setMessage(e.toString());
////        sysOperationLogEntity.setClassname(className);
//        sysOperationLogEntity.setCreatetime(new Date());
//        sysOperationLogEntity.setSucceed(LogSucceed.FAIL.getMessage());
//        sysOperationLogEntity.setFromType(fromType);
//        sysOperationLogEntity.setFkBase(fkBase);
//        sysOperationLogService.create(sysOperationLogEntity);
        return new BaseResponse(Constants.EX_OTHER_CODE, ex.getMessage());
    }

}
