package com.vdata.cloud.datacenter.vo;

import lombok.Data;

/**
 * @ProjectName: sjzl-master
 * @Package: com.vdata.cloud.datacenter.vo
 * @ClassName: BaseGroupVO
 * @Author: HK
 * @Description:
 * @Date: 2021/9/6 16:55
 * @Version: 1.0
 */
@Data
public class BaseGroupVO {
    private String name;
    private int fkBaseUser;
    private String fkBaseGroup;
}
