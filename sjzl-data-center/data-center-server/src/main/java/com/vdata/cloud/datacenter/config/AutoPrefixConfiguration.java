package com.vdata.cloud.datacenter.config;

import com.vdata.cloud.datacenter.hack.AutoPrefixUrlMapping;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * @ProjectName: hk-sleeve-application
 * @Package: com.hk.sleeve.core.configuration
 * @ClassName: AutoPrefixConfiguration
 * @Author: HK
 * @Description:
 * @Date: 2021/6/8 15:05
 * @Version: 1.0
 */
@Component
public class AutoPrefixConfiguration implements WebMvcRegistrations {

    @Override
    public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
        return new AutoPrefixUrlMapping();
    }
}
