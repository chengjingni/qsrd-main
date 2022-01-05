package com.vdata.cloud.datacenter;

import org.junit.Test;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import static java.math.BigDecimal.ROUND_HALF_UP;

/**
 * @ProjectName: sjzl-master
 * @Package: com.vdata.cloud.datacenter
 * @ClassName: TestMain
 * @Author: HK
 * @Description:
 * @Date: 2021/8/26 17:12
 * @Version: 1.0
 */
public class TestMain {
    @Test
    public void test1() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        Date date = new Date();
        System.out.println(date.getTime());
        System.out.println(date.getTime() / 1000);
    }


    @Test
    public void test2() throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        Date parse = simpleDateFormat.parse("2018-11-05 10:05:24");

        System.out.println(parse);

        String format = simpleDateFormat.format(parse);
        System.out.println(format);


    }

    private double randomValue(Random r) {
        float v = r.nextFloat() * 100;
        return BigDecimal.valueOf(v).setScale(3, ROUND_HALF_UP).doubleValue();
    }


    @Test
    public void test4() {
        Date date = new Date();
        long a = date.getTime() / 1000L;
        System.out.println(a);
        System.out.println(a += 1 * 60 * 5);

    }

}
