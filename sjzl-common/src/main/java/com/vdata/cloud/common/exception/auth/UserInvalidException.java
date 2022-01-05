package com.vdata.cloud.common.exception.auth;


import com.vdata.cloud.common.constant.Constants;
import com.vdata.cloud.common.exception.BaseException;

/**
 * Created by ace on 2017/9/8.
 */
public class UserInvalidException extends BaseException {
    private static final long serialVersionUID = 3378073431215781568L;

    public UserInvalidException(String message) {
        super(message, Constants.EX_USER_PASS_INVALID_CODE);
    }
}
