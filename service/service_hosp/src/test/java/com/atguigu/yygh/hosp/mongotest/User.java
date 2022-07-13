package com.atguigu.yygh.hosp.mongotest;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("User") //指定mongodb中的集合名字
public class User {

    @Id
    private String id;
    private String name;
    private Integer age;
    private String email;
    private String createDate;
}