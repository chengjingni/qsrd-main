package com.vdata.cloud.common.exception.auth;


import com.vdata.cloud.common.constant.Constants;
import com.vdata.cloud.common.exception.BaseException;

/**
 * Created by ace on 2017/9/10.
 */
public class ClientInvalidException extends BaseException {
    private static final long serialVersionUID = -1553100799104580036L;

    public ClientInvalidException(String message) {
        super(message, Constants.EX_CLIENT_INVALID_CODE);
    }
}
