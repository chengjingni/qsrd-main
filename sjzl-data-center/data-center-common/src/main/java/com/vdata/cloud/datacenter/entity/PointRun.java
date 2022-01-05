package com.vdata.cloud.datacenter.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @ProjectName: sjzl-master
 * @Package: com.vdata.cloud.datacenter.entity
 * @ClassName: pointRun
 * @Author: HK
 * @Description:
 * @Date: 2021/7/29 11:40
 * @Version: 1.0
 */
@Builder
@Document(collection = "point_run")
@Data
public class PointRun {
    @Tolerate
    public PointRun() {
    }

    @Id
    private String id;

    @Field("pulverizer_code")
    private String pulverizerCode;

    @Field("pulverizer_name")
    private String pulverizerName;


    @Field("dcsDataIdentifier")
    private String dcsDataIdentifier;

    @Field("hour")
    @Indexed
    private int hour;
    @Field("date")
    @Indexed
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date date;
    @Indexed
    @Field("time")
    private Date time;
    @Field("point")
    private LinkedHashMap<String, Double> point;

    //    private Point point;
    @Transient
    private Map<String, Map<String, Double>> pointMap;


}
