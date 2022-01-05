package com.vdata.cloud.auth.configuration;

import com.vdata.cloud.auth.interceptor.ServiceAuthRestInterceptor;
import com.vdata.cloud.auth.interceptor.UserAuthRestInterceptor;
import com.vdata.cloud.common.handler.GlobalExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration("admimWebConfig")
@Primary
public class WebConfiguration implements WebMvcConfigurer {
    @Bean
    GlobalExceptionHandler getGlobalExceptionHandler() {
        return new GlobalExceptionHandler();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(getServiceAuthRestInterceptor());
        registry.addInterceptor(getUserAuthRestInterceptor()).addPathPatterns("*");
    }

    @Bean
    ServiceAuthRestInterceptor getServiceAuthRestInterceptor() {
        return new ServiceAuthRestInterceptor();
    }

    @Bean
    UserAuthRestInterceptor getUserAuthRestInterceptor() {
        UserAuthRestInterceptor userAuthRestInterceptor = new UserAuthRestInterceptor();
        return userAuthRestInterceptor;

    }

}
