package com.vdata.cloud.admin.validator;

import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Map;

/**
 * @Author: wanglian
 * @Description:
 * @Date: 2020/11/24 10:50
 * @Version: 1.0
 */


public class UserRoleOrgValidator implements ConstraintValidator<UserRoleOrg, Map<String, String>> {
    @Override
    public boolean isValid(Map<String, String> map, ConstraintValidatorContext constraintValidatorContext) {
        if (map == null) {
            return true;
        }
        return !StringUtils.isAnyBlank(map.get("id"), map.get("groupType"));

    }
}
