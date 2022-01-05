package com.vdata.cloud.datacenter.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import java.io.Serializable;

/**
 * 实体类
 *
 * @author hk
 * @date 2021-07-19 14:34:08
 */
@ApiModel
@Data
@TableName("pulverizer_running")
@Validated
@NoArgsConstructor
public class PulverizerRunning implements Serializable {
    private static final long serialVersionUID = -4768845596352121265L;
    @TableField("pulverizer_code")
    private String pulverizerCode;
    @TableField("`day`")
    private int day;


}
