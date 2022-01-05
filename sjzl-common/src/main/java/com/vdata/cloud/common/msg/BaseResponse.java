package com.vdata.cloud.common.msg;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BaseResponse {

    @ApiModelProperty(value = "状态码")
    private int status = 200;
    @ApiModelProperty(value = "返回信息(操作成功/失败)")
    public String message;

}
