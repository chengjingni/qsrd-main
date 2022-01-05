package com.vdata.cloud.common.vo;

import lombok.Data;

import java.io.Serializable;

/**
 *  * @ProjectName:    sjzl-master
 *  * @Package:        com.vdata.cloud.common.vo
 *  * @ClassName:      UserVO
 *  * @Author:         Torry
 *  * @Description:    ${Description}
 *  * @Date:            2020/12/15 14:12
 *  * @Version:    1.0
 *  
 */
@Data
public class UserVO implements Serializable {

    private String username;
    private String userId;
    private String name;

    public UserVO() {
        super();
    }

    public UserVO(String username, String userId, String name) {
        this.username = username;
        this.userId = userId;
        this.name = name;
    }

}
