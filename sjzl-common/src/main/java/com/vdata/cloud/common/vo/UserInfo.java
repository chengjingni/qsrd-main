package com.vdata.cloud.common.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserInfo implements Serializable {
    public String id;
    public String username;
    public String password;
    @JsonProperty("name")
    public String nickname;
    private String token;
    private String description;
    private String status; //使用状态
    private String onlyCode;
    private String roleCode;

}
