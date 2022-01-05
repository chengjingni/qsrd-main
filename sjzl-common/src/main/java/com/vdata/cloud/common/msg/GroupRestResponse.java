package com.vdata.cloud.common.msg;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class GroupRestResponse<T> extends ObjectRestResponse {

    boolean rel;

    public GroupRestResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public GroupRestResponse(boolean rel) {
        this.rel = rel;
    }

    public GroupRestResponse rel(boolean rel){
        this.rel = rel;
        return this;
    }


}
