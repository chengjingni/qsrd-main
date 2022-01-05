package com.vdata.cloud.common.annotion;

import java.lang.annotation.*;

/**
 *  * @ProjectName:    sjzl-master
 *  * @Package:        com.vdata.cloud.common.annotion
 *  * @ClassName:      BException
 *  * @Author:         Torry
 *  * @Description:    Biz异常处理的注解
 *  * @Date:            2020/11/24 9:27
 *  * @Version:    1.0
 *  
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface BException {
    String value() default "";
}
