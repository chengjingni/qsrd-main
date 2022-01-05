package com.vdata.cloud.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * <p>
 * 业务异常
 * </p>
 *
 * @author xubo
 * @since 2019-12-11
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BusinessException extends RuntimeException {
    private static final long serialVersionUID = -447631698989522191L;
    private String code;
    private String message;


    public BusinessException(String message) {
        super(message);
        this.message = message;

    }

    public BusinessException(String message, Exception e) {
        super(message, e);
    }

}
