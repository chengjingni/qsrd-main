package com.vdata.cloud.admin.entity;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.vdata.cloud.admin.constant.AdminCommonConstant;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("base_menu")
public class Menu implements Serializable {
    private static final long serialVersionUID = 7037248778191995793L;
    @TableId
    private String id;

    private String code;

    private String title;

    @TableField("parent_id")
    private String parentId = AdminCommonConstant.ROOT;

    private String href;

    private String icon;

    private String type;

    private String description;

    @TableField("crt_time")
    private Date crtTime;

    @TableField("crt_user")
    private String crtUser;

    @TableField("crt_name")
    private String crtName;

    @TableField("crt_host")
    private String crtHost;

    @TableField("upd_time")
    private Date updTime;

    @TableField("upd_user")
    private String updUser;

    @TableField("upd_name")
    private String updName;

    @TableField("upd_host")
    private String updHost;

    private String attr1;

    private String attr2;

    private String attr3;

    private String attr4;

    private String attr5;

    private String attr6;

    private String attr7;

    private String attr8;

    private String path;

}