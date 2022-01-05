package com.vdata.cloud.datacenter.enums;

import java.util.stream.Stream;

/**
 * @ProjectName: sjzl-master
 * @Package: com.vdata.cloud.datacenter.enums
 * @ClassName: StatusEnums
 * @Author: HK
 * @Description:
 * @Date: 2021/7/21 14:54
 * @Version: 1.0
 */
public enum StatusTypeEnum {
    TO_VERIFY_THE(1, "核实中"),
    VERIFIED(2, "已核实"),
    BEING_PROCESSED(3, "处理中"),
    PROCESSED(4, "已处理");

    private int value;
    private String description;

    StatusTypeEnum(int value, String description) {
        this.value = value;
        this.description = description;
    }

    public String description() {
        return this.description;
    }

    public int value() {
        return this.value;
    }

    public static StatusTypeEnum toType(int value) {
        return Stream.of(StatusTypeEnum.values())
                .filter(c -> c.value == value)
                .findAny()
                .orElse(null);
    }
}
