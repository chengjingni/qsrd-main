package com.vdata.cloud.common.exception.auth;


import com.vdata.cloud.common.constant.Constants;
import com.vdata.cloud.common.exception.BaseException;

/**
 * Created by ace on 2017/9/8.
 */
public class UserTokenException extends BaseException {
    private static final long serialVersionUID = 6711557315018465464L;

    public UserTokenException(String message) {
        super(message, Constants.EX_USER_INVALID_CODE);
    }
}
