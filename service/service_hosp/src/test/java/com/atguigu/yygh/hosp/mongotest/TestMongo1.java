package com.atguigu.yygh.hosp.mongotest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;


import java.util.List;

/**
 * @ClassName TestMongo1 * @Description TODO
 * @Author ehdk
 * @Date 22:53 2022/7/12
 * @Version 1.0
 **/
@SpringBootTest
public class TestMongo1 {
    @Autowired
    private MongoTemplate mongoTemplate;
    //查询所有
    @Test
    public void findUser() {
        List<User> list = mongoTemplate.findAll(User.class);
        System.out.println(list);
    }

    //根据ID查询
    @Test
    public void findUserById(){
        User user = mongoTemplate.findById("62cd89532a308034bafcc007", User.class);
        System.out.println(user);



    }
    @Test
    public void findUserList() {
        Query query = new Query(Criteria.where("name").is("张三"));
        List<User> userList = mongoTemplate.find(query, User.class);
        System.out.println(userList);
    }

}
