package com.vdata.cloud.common.exception.auth;


import com.vdata.cloud.common.constant.Constants;
import com.vdata.cloud.common.exception.BaseException;

/**
 * Created by ace on 2017/9/12.
 */
public class ClientForbiddenException extends BaseException {
    private static final long serialVersionUID = 7773366403440727274L;

    public ClientForbiddenException(String message) {
        super(message, Constants.EX_CLIENT_FORBIDDEN_CODE);
    }

}
