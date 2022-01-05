package com.vdata.cloud.admin.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("base_group")
public class Group implements Serializable {
    @TableId
    private String id;

    private String code;

    private String name;

    @TableField("parent_id")
    private String parentId;

    private String path;

    private String type;

    @TableField("group_type")
    private Integer groupType;

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

    private String status;

    private String sort;

}