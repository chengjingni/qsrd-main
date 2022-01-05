package com.vdata.cloud.datacenter.config;


import com.vdata.cloud.common.constant.Constants;
import com.vdata.cloud.common.util.CommonUtil;
import com.vdata.cloud.common.vo.DataResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * @Author: wanglian
 * @Description: 全局异常处理类
 * @Date: 2020/9/9 9:05
 * @Version: 1.0
 */
@RestControllerAdvice
@Slf4j
public class GlobalControllerAdvice {
    private static final String BAD_REQUEST_MSG = "客户端请求参数错误";

    // 处理 json 请求体调用接口校验失败抛出的异常 ; 处理 form data方式调用接口校验失败抛出的异常
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public DataResult methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        List<String> collect = fieldErrors.stream()
                .map(o -> o.getDefaultMessage())
                .collect(Collectors.toList());
        DataResult dataResult = new DataResult();
        if (CommonUtil.isNotEmpty(collect) && collect.size() == 1) {
            dataResult.setMessage(collect.get(0));
        } else {
            dataResult.setMessage(BAD_REQUEST_MSG);
        }
        dataResult.setCode(Constants.RETURN_UNNORMAL);
        dataResult.setData(collect);
        return dataResult;
    }

    // <3> 处理单个参数校验失败抛出的异常
    @ExceptionHandler(ConstraintViolationException.class)
    public DataResult constraintViolationExceptionHandler(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
        List<String> collect = constraintViolations.stream()
                .map(o -> o.getMessage())
                .collect(Collectors.toList());
        DataResult dataResult = new DataResult();
        if (CommonUtil.isNotEmpty(collect) && collect.size() == 1) {
            dataResult.setMessage(collect.get(0));
        } else {
            dataResult.setMessage(BAD_REQUEST_MSG);
        }
        dataResult.setCode(Constants.RETURN_UNNORMAL);
        dataResult.setData(collect);
        return dataResult;
    }


}
