package com.vdata.cloud.common.annotion;

import java.lang.annotation.*;

/**
 *  * @ProjectName:    sjzl-master
 *  * @Package:        com.vdata.cloud.common.annotion
 *  * @ClassName:      DataDic
 *  * @Author:         Torry
 *  * @Description:    ${Description}
 *  * @Date:            2020/11/24 15:00
 *  * @Version:    1.0
 *  
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface DataDic {
    String value() default "";
}
