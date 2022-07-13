package com.atguigu.yygh;

import com.alibaba.excel.EasyExcel;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @ClassName EasyExcelTest * @Description TODO
 * @Author ehdk
 * @Date 15:05 2022/7/11
 * @Version 1.0
 **/
public class EasyExcelTest {


    @Test
    public void t1(){
        String road ="D:/excel/"+System.currentTimeMillis()+".xlsx";
        EasyExcel.write(road,Student.class).sheet("模板").doWrite(data());
    }

    public List<Student> data(){
        List<Student> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Student student = new Student();
            student.setName("tom"+i);
            student.setBirthday(new Date());
            list.add(student);
        }
        return list;
    }
}
