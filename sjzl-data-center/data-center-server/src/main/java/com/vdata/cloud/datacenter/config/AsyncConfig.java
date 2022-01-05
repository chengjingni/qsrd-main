package com.vdata.cloud.datacenter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 *  * @ProjectName:    wru-master
 *  * @Package:      com.vdata.cloud.datacenter.config
 *  * @ClassName:     AsyncConfig
 *  * @Author:       刘芳
 *  * @Description:    异步调用线程池的配置
 *  * @Date:        2020/11/4 17:01
 *  * @Version:    1.0
 *  
 */
@Configuration
public class AsyncConfig {
    private static final int MAX_POOL_SIZE = 50;

    private static final int CORE_POOL_SIZE = 10;

    private static final int KEEP_ALIVE_SECONDS = 60;

    @Bean
    public AsyncTaskExecutor asyncTaskExecutor() {
        ThreadPoolTaskExecutor asyncTaskExecutor = new ThreadPoolTaskExecutor();
        asyncTaskExecutor.setCorePoolSize(CORE_POOL_SIZE);
        asyncTaskExecutor.setMaxPoolSize(MAX_POOL_SIZE);
        asyncTaskExecutor.setThreadNamePrefix("async-task-thread-");
        asyncTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        asyncTaskExecutor.setKeepAliveSeconds(KEEP_ALIVE_SECONDS);
        asyncTaskExecutor.initialize();
        return asyncTaskExecutor;
    }
}
