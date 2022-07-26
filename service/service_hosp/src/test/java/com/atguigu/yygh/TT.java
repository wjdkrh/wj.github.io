package com.atguigu.yygh;

import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;


/**
 * @ClassName TT * @Description TODO
 * @Author ehdk
 * @Date 9:17 2022/7/26
 * @Version 1.0
 **/

public class TT {


    @Test
    public void Test1() {

        DateTime curentTime = new DateTime();
        ArrayList<Date> dateArrayList = new ArrayList<>();
        for (int i = 0; i <5 ; i++) {

            DateTime dateTime = curentTime.plusDays(i);
            String dataStr = dateTime.toString("yyyy-MM-dd");
            dateArrayList.add(new DateTime(dataStr).toDate());
        }
        System.out.println(dateArrayList);


    }


}
