package com.vdata.cloud.datacenter.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 业务系统密钥对
 *
 * @author XuBo
 * @date 2020-10-21 10:23:16
 */
@ApiModel
@Data
@TableName("RSA_KEY")
public class RsaKey implements Serializable {
    private static final long serialVersionUID = 1L;

    //主键
    @TableId(type = IdType.AUTO)
    private Integer id;

    //应用编号
    @ApiModelProperty(value = "应用编号")
    @TableField("APP_ID")
    private String appId;

    //私钥
    @ApiModelProperty(value = "私钥")
    @TableField("PRIVATE_KEY")
    private String privateKey;

    //公钥
    @ApiModelProperty(value = "公钥")
    @TableField("PUBLIC_KEY")
    private String publicKey;

    //创建人
    @ApiModelProperty(value = "创建人")
    @TableField("CRT_STFF_ID")
    private Integer crtStffId;

    //创建时间
    @ApiModelProperty(value = "创建时间")
    @TableField("CRT_TM")
    private Date crtTm;

    //删除标志
    @ApiModelProperty(value = "删除标志")
    @TableField("DEL_LBL")
    private String delLbl;

    public RsaKey() {
        super();
    }

    public RsaKey(String publicKey, String privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

}
