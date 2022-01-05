package com.vdata.cloud.admin.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @Author: wanglian
 * @Description:
 * @Date: 2020/11/24 10:48
 * @Version: 1.0
 */

@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = {UserRoleOrgValidator.class})// 标明由哪个类执行校验逻辑
public @interface UserRoleOrg {

    // 校验出错时默认返回的消息
    String message() default "id或者groupType不能为空";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
