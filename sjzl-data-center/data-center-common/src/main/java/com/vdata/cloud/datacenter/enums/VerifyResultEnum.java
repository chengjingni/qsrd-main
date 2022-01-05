package com.vdata.cloud.datacenter.enums;

import java.util.stream.Stream;

/**
 * @ProjectName: sjzl-master
 * @Package: com.vdata.cloud.datacenter.enums
 * @ClassName: VerifyResultEnum
 * @Author: HK
 * @Description:
 * @Date: 2021/7/21 14:58
 * @Version: 1.0
 */
public enum VerifyResultEnum {
    TO_VERIFY_THE(1, "误报"),
    VERIFIED(2, "真实故障");

    private int value;

    private String description;

    VerifyResultEnum(int value, String description) {

        this.value = value;
        this.description = description;
    }

    public int value() {
        return this.value;
    }

    public String description() {
        return this.description;
    }

    public static VerifyResultEnum toType(int value) {
        return Stream.of(VerifyResultEnum.values())
                .filter(c -> c.value == value)
                .findAny()
                .orElse(null);
    }
}
