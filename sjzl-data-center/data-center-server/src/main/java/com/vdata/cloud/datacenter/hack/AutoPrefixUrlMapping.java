package com.vdata.cloud.datacenter.hack;

import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;

/**
 * @ProjectName: hk-sleeve-application
 * @Package: com.hk.sleeve.core.hack
 * @ClassName: AutoPrefixUrlMapping
 * @Author: HK
 * @Description: 动态拼接api路由前缀
 * @Date: 2021/6/8 14:59
 * @Version: 1.0
 */
public class AutoPrefixUrlMapping extends RequestMappingHandlerMapping {
    private String apiPackagePath = "com.vdata.cloud";

    @Override
    protected RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
        RequestMappingInfo mappingInfo = super.getMappingForMethod(method, handlerType);
        if (mappingInfo != null) {
            String prefix = getPrefix(handlerType);
            return RequestMappingInfo.paths(prefix).build().combine(mappingInfo);
        }
        return mappingInfo;
    }

    private String getPrefix(Class<?> handlerType) {
        String packageName = handlerType.getPackage().getName();
        String dotPath = packageName.replaceAll(this.apiPackagePath, "").replaceAll(".controller", "");
        return dotPath.replace(".", "/");

    }
}
