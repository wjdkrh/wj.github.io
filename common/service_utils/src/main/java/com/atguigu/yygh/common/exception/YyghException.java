package com.atguigu.yygh.common.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName YyghException * @Description TODO
 * @Author ehdk
 * @Date 11:16 2022/7/5
 * @Version 1.0
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class YyghException extends RuntimeException {
    private String msg;
    private Integer code;

    public YyghException (String msg,Integer code,Throwable cause){
        super(cause);
        this.msg=msg;
        this.code=code;
    }
}
