package com.vdata.cloud.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@TableName("base_user")
public class User implements Serializable {
    private static final long serialVersionUID = -1182758451919118265L;
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String nickname;

    private String username;

    private String password;

    private LocalDate birthday;

    private String address;

    private String mobile;

    private String telephone;

    private String email;

    private Integer sex;

    private Integer status;

    private String description;

    @TableField("create_time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @TableField("create_user_id")
    private String createUserId;

    @TableField("update_time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;

    @TableField("update_user_id")
    private String updateUserId;

    @TableField(exist = false)
    private Map<String, String> role;

    @TableField(exist = false)
    private Map<String, String> org;

    //角色
    @TableField(exist = false)
    private String orgId;

    //部门
    @TableField(exist = false)
    private String roleId;

    public String getAuthCacheKey() {
        return this.getId().toString();
    }
}