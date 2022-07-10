package com.atguigu.yygh.common.exception;

import com.atguigu.yygh.common.result.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @ClassName GlobalExceptionHandler * @Description TODO
 * @Author ehdk
 * @Date 11:23 2022/7/5
 * @Version 1.0
 **/
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public R error(Exception e){
        log.error(ExceptionUtils.getStackTrace(e));
        return R.error();
    }

    @ExceptionHandler(YyghException.class)
    public R error(YyghException e){
        /*e.printStackTrace();*/
        log.error(ExceptionUtils.getStackTrace(e));
        return R.error().message(e.getMsg()).code(e.getCode());
    }

    @ExceptionHandler(RuntimeException.class)
    public R error(RuntimeException e){
        log.error(ExceptionUtils.getStackTrace(e));
        return R.error().message(e.getMessage());
    }
}
