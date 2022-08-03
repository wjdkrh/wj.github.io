package com.atguigu.yygh;

import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;

import java.util.Date;

/**
 * @ClassName T1 * @Description TODO
 * @Author ehdk
 * @Date 20:31 2022/7/29
 * @Version 1.0
 **/
public class T1 {
    @Test
    public void t1(){
        long time = new Date().getTime();
        System.out.println(time);
    }
}
