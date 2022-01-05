package com.vdata.cloud.common.annotion;

import java.lang.annotation.*;

/**
 *  * @ProjectName:    sjzl-master
 *  * @Package:        com.vdata.cloud.common.annotion
 *  * @ClassName:      CException
 *  * @Author:         Torry
 *  * @Description:    Controller异常处理的注解
 *  * @Date:            2020/11/23 17:33
 *  * @Version:    1.0
 *  
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface CException {
    String value() default "";
}
