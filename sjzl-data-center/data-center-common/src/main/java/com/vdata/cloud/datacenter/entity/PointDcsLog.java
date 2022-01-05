package com.vdata.cloud.datacenter.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 实体类
 *
 * @author hk
 * @date 2021-12-02 13:43:20
 */
@ApiModel
@Data
@TableName("point_dcs_log")
public class PointDcsLog implements Serializable {
    private static final long serialVersionUID = 1L;

    //
    @TableId(type = IdType.AUTO)
    private Integer id;

    //磨煤机代码
    @ApiModelProperty(value = "磨煤机代码")
    @TableField("pulverizer_code")
    private String pulverizerCode;

    //更换时间
    @ApiModelProperty(value = "更换时间")
    @TableField("cut_date")
    private Date cutDate;

    //DCS数据标识符
    @ApiModelProperty(value = "DCS数据标识符")
    @TableField("dcs_data_identifier")
    private String dcsDataIdentifier;

    //点位id
    @ApiModelProperty(value = "点位id")
    @TableField("point_id")
    private Integer pointId;

    //点位编码
    @ApiModelProperty(value = "点位编码")
    @TableField("no")
    private Integer no;

    //创建时间
    @ApiModelProperty(value = "创建时间")
    @TableField("create_date")
    private Date createDate;

    //修改时间
    @ApiModelProperty(value = "修改时间")
    @TableField("update_date")
    private Date updateDate;

    //创建人
    @ApiModelProperty(value = "创建人")
    @TableField("create_user")
    private String createUser;

    //修改人
    @ApiModelProperty(value = "修改人")
    @TableField("update_user")
    private String updateUser;

    //删除标志
    @ApiModelProperty(value = "删除标志")
    @TableField("delete_date")
    private Date deleteDate;


}
