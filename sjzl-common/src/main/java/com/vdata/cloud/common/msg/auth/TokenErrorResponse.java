package com.vdata.cloud.common.msg.auth;

import com.vdata.cloud.common.constant.RestCodeConstants;
import com.vdata.cloud.common.msg.BaseResponse;

/**
 * Created by ace on 2017/8/23.
 */
public class TokenErrorResponse extends BaseResponse {
    public TokenErrorResponse(String message) {
        super(RestCodeConstants.TOKEN_ERROR_CODE, message);
    }
}
