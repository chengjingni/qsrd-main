package com.vdata.cloud.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @author fjzha
 * @version 1.0
 * @date 2020/3/15 0:26
 */
@Data
@TableName("calendar")
public class Calendar implements Serializable {
    private static final long serialVersionUID = -6921619953427248381L;
    @TableId(type = IdType.AUTO)
    private Integer id;
    @TableField("year")
    private String year;
    @TableField("month")
    private String month;
    @TableField("day")
    private String day;
    @TableField("type")
    private String type;
}
