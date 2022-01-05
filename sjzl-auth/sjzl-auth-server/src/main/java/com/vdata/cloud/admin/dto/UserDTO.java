package com.vdata.cloud.admin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vdata.cloud.admin.validator.UserRoleOrg;
import com.vdata.cloud.admin.validator.groups.UserUpdateGroup;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Map;

/**
 * @Author: wanglian
 * @Description:
 * @Date: 2020/11/23 16:42
 * @Version: 1.0
 */

@Getter
@Setter
public class UserDTO implements Serializable {

    private static final long serialVersionUID = -6991462659413137637L;
    @NotNull(message = "用户ID不能为空", groups = UserUpdateGroup.class)
    private Integer Id;
    //用户编号
    @NotEmpty(message = "用户编号不能为空")
    @JsonProperty("name")
    private String nickname;
    //用户名
    @NotEmpty(message = "用户名不可为空")
    private String username;
    /* @NotEmpty(message = "密码不可为空",groups = UserAddGroup.class)
     @Size(min = 6, max = 20, message = "密码长度在6-20之间")*/
    private String password;
    private String address;
    @Pattern(message = "移动电话格式不正确", regexp = "^1[3456789]\\d{9}$")
    private String mobile;
    @Pattern(message = "座机电话格式不正确", regexp = "^0\\d{2,3}-\\d{7,8}$")
    private String telephone;
    /* @Email(message = "邮箱格式不合法", regexp = "^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$")*/
    private String email;
    @Min(value = 0, message = "性别取值在0-2之间")
    @Max(value = 2, message = "性别取值在0-2之间")
    private Integer sex;
    @Min(value = 0, message = "用户状态取值在0-1之间")
    @Max(value = 2, message = "用户状态取值在0-1之间")
    private Integer status;
    @PastOrPresent(message = "生日日期填写不规范")
    private LocalDate birthday;
    private String description;

    @UserRoleOrg(message = "用户角色权限的Id或者groupType不能为空")
    private Map<String, String> org;
    @UserRoleOrg(message = "用户部门的Id或者groupType不能为空")
    private Map<String, String> role;

}
