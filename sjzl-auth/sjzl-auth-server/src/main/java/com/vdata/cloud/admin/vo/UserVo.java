package com.vdata.cloud.admin.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * @Author: wanglian
 * @Description:
 * @Date: 2020/11/23 16:29
 * @Version: 1.0
 */

@Getter
@Setter
public class UserVo implements Serializable {


    private static final long serialVersionUID = -7608450531819241672L;
    private Integer Id;

    //用户名
    private String username;
    private String password;

    //用户编号
    @JsonProperty("name")
    private String nickname;

    private String address;
    private String mobile;
    private String telephone;
    private String email;

    private Integer sex;
    private LocalDate birthday;
    private String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")

    private LocalDateTime createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;

    @JsonGetter
    private String getSex() {
        return String.valueOf(sex);
    }

    @JsonIgnore
    private Map<String, String> role;

    @JsonIgnore
    private Map<String, String> org;

    //角色
    private String orgId;
    //部门
    private String roleId;

    /*@JsonGetter
    public String getOrgId() {
        if (CommonUtil.isNotEmpty(this.getRole())) {
            return this.getRole().get("id");
        } else {
            return null;
        }
    }

    @JsonGetter
    public String getRoleId() {
        if(CommonUtil.isNotEmpty(this.org)){
            return this.org.get("id");
        } else {
            return null;
        }
    }*/


}
