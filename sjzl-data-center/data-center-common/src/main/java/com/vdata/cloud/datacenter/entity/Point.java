package com.vdata.cloud.datacenter.entity;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * @ProjectName: sjzl-master
 * @Package: com.vdata.cloud.datacenter.entity
 * @ClassName: Point
 * @Author: HK
 * @Description:
 * @Date: 2021/7/29 14:07
 * @Version: 1.0
 */
@Document
@Builder
@Data
public class Point {

    @Tolerate
    public Point() {
    }

    @Field("no1")
    private double no1;
    @Field("no2")
    private double no2;
    @Field("no3")
    private double no3;
    @Field("no4")
    private double no4;
    @Field("no5")
    private double no5;
    @Field("no6")
    private double no6;
    @Field("no7")
    private double no7;
    @Field("no8")
    private double no8;
    @Field("no9")
    private double no9;
    @Field("no10")
    private double no10;
    @Field("no11")
    private double no11;
    @Field("no12")
    private double no12;
    @Field("no13")
    private double no13;
}
