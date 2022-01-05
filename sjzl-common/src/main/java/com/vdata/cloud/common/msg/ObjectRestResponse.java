package com.vdata.cloud.common.msg;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ObjectRestResponse<T> extends BaseResponse {

    @ApiModelProperty(value = "返回数据")
    T data;
    @ApiModelProperty(value = "返回代码（00-成功，01-失败）")
    String code;

    public ObjectRestResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public ObjectRestResponse data(T data) {
        this.setData(data);
        return this;
    }

    public ObjectRestResponse setMessages(String message) {
        this.message = message;
        return this;
    }

}
