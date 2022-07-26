package com.atguigu.yygh.hosp.controller.admin;

import com.atguigu.yygh.common.result.R;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.*;

/**
 * @author atguigu
 * @since 2021-10-30
 */
@Api(tags = "登录接口")
@RestController
@RequestMapping("/admin/hosp") //跨域
public class UserLoginController {

    //http://localhost:9528/dev-api/vue-admin-template/user/login
    //{"code":20000,"data":{"token":"admin-token"}}
    //登录
    @PostMapping("user/login")
    public R login() {
        return R.ok().data("token","admin-token");
    }

//    http://localhost:9528/dev-api/vue-admin-template/user/info?token=admin-token
//    {"code":20000,"data":{"roles":["admin"],
//        "introduction":"I am a super administrator",
//                "avatar":"https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif",
//                "name":"Super Admin"}}
    //info
    @GetMapping("user/info")
    public R info() {
        return R.ok().data("roles",new String[]{"admin"})
                .data("introduction","I am a super administrator")
                .data("avatar","https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif")
                .data("name","Super Admin");
    }

//    http://localhost:9528/dev-api/vue-admin-template/user/logout
//   {"code":20000,"data":"success"}
    @PostMapping("user/logout")
    public R logout() {
        return R.ok();
    }
}

