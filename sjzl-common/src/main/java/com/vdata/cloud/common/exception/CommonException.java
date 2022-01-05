package com.vdata.cloud.common.exception;

import lombok.*;

/**
 *  * @ProjectName:    wru-master
 *  * @Package:        com.vdata.cloud.common.exception
 *  * @ClassName:      CommonException
 *  * @Author:         Torry
 *  * @Description:    通用异常
 *  * @Date:            2020/8/31 15:59
 *  * @Version:    1.0
 *  
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommonException extends Exception {
    private static final long serialVersionUID = -2508851365475651027L;
    private String message;

    public CommonException(Exception e) {
        super(e);
    }
}
