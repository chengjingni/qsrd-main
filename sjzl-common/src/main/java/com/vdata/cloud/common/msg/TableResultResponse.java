package com.vdata.cloud.common.msg;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


public class TableResultResponse<T> extends BaseResponse {

    @ApiModelProperty(value = "返回列表数据")
    @Getter
    @Setter
    TableData<T> data;
    @ApiModelProperty(value = "返回代码（00-成功，01-失败）")
    String code;


    public String getCode() {
        return code;
    }

    public TableResultResponse setCode(String code) {
        this.code = code;
        return this;
    }


    public TableResultResponse setMessages(String message) {
        this.message = message;
        return this;
    }

    public TableResultResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public TableResultResponse(long total, List<T> records) {
        this.data = new TableData<T>(total, records);
    }

    public TableResultResponse() {
        this.data = new TableData<T>();
    }

    TableResultResponse<T> total(int total) {
        this.data.setTotal(total);
        return this;
    }

    TableResultResponse<T> total(List<T> records) {
        this.data.setRecords(records);
        return this;
    }


    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    class TableData<T> {
        long total;
        List<T> records;
    }
}
