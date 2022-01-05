package com.vdata.cloud.datacenter.entity;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @ProjectName: sjzl-master
 * @Package: com.vdata.cloud.datacenter.entity
 * @ClassName: point_his_hour
 * @Author: HK
 * @Description:
 * @Date: 2021/7/30 15:01
 * @Version: 1.0
 */
@Builder
@Document(collection = "point_his_hour3")
@Data
public class PointHisHour {

    @Tolerate
    public PointHisHour() {
    }

    @Id
    private String id;

    @Field("pulverizer_code")
    private String pulverizerCode;

    @Field("pulverizer_name")
    private String pulverizerName;

    @Field("hour")
    private String hour;

    @Field("date")
    private Date date;

    @Field("time")
    private Date time;

    @Field("point")
//    private Point point;
    private LinkedHashMap<String, Double> point;


    @Transient
    private Map<String, Map<String, Double>> pointMap;
}
