package com.atguigu.yygh;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @ClassName Student * @Description TODO
 * @Author ehdk
 * @Date 15:03 2022/7/11
 * @Version 1.0
 **/
@Data
public class Student {
    @ExcelProperty("姓名")
    private String name;
    @ExcelProperty("生日")
    private Date birthday;
}
