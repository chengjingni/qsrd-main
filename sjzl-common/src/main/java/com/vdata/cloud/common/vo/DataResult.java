package com.vdata.cloud.common.vo;


import com.vdata.cloud.common.constant.Constants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zfj
 * @version 1.0
 * @ClassName: DataResult
 * @Description: 返回数据实体
 * @date 2018年5月17日 下午5:54:26
 */
@Data
@ApiModel(value = "返回结果")
public class DataResult<T> {

    @ApiModelProperty(value = "返回代码（00-成功，01-失败）")
    private String code;
    @ApiModelProperty(value = "返回信息(操作成功/失败)")
    private String message;
    @ApiModelProperty(value = "返回数据")
    private T data;

    public DataResult() {
        this.code = Constants.RETURN_NORMAL;
        this.message = Constants.RETURN_NORMAL_MSG;
    }

    public DataResult(T data) {
        this.data = data;
    }

    public DataResult(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public DataResult(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static DataResult success(Object data) {
        DataResult dataResult = new DataResult();
        dataResult.setData(data);
        dataResult.setMessage(Constants.RETURN_NORMAL_MSG);
        dataResult.setCode(Constants.RETURN_NORMAL);
        return dataResult;
    }

    public static DataResult success(String message, Object data) {
        DataResult dataResult = new DataResult();
        dataResult.setData(data);
        dataResult.setMessage(message);
        dataResult.setCode(Constants.RETURN_NORMAL);
        return dataResult;
    }

    public static DataResult error(String message) {
        DataResult dataResult = new DataResult();
        dataResult.setMessage(message);
        dataResult.setCode(Constants.RETURN_UNNORMAL);
        return dataResult;
    }

    public static DataResult error() {
        DataResult dataResult = new DataResult();
        dataResult.setMessage(Constants.RETURN_UNNORMAL_MSG);
        dataResult.setCode(Constants.RETURN_UNNORMAL);
        return dataResult;
    }
}
