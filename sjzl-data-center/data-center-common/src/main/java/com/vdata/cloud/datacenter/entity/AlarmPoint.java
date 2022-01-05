package com.vdata.cloud.datacenter.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @ProjectName: qsrd-main
 * @Package: com.vdata.cloud.datacenter.entity
 * @ClassName: AlarmPoint
 * @Author: HK
 * @Description:
 * @Date: 2021/12/22 10:55
 * @Version: 1.0
 */
@TableName("alarm_point")
@Data
public class AlarmPoint implements Serializable {
    private static final long serialVersionUID = 5422704735654830376L;
    @TableId(type = IdType.AUTO)
    private Integer id;
    @TableField("pulverizer_point_id")
    private Integer pulverizerPointId;

    @TableField("alarm_code")
    private String alarmCode;
    @TableField("pulverizer_code")
    private String pulverizerCode;

}
