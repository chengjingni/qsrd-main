package com.vdata.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 *  * @ProjectName:    wru-master
 *  * @Package:        com.vdata.cloud.datacenter
 *  * @ClassName:      DataopenfeignCenterBootzzstrap
 *  * @Author:         Torry
 *  * @Description:    数据交换中心启动类
 *  * @Date:            2020/8/27 13:44
 *  * @Version:    1.0
 *  
 */
@SpringBootApplication(scanBasePackages = "com.vdata.cloud")
@EnableScheduling
@EnableTransactionManagement
@EnableSwagger2
@EnableAsync
public class DataCenterBootstrap {

    public static void main(String[] args) {
        SpringApplication.run(DataCenterBootstrap.class, args);
    }

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
