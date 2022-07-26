package com.atguigu.yygh.user.controller.admin;

import com.atguigu.yygh.common.exception.YyghException;
import com.atguigu.yygh.common.result.R;
import com.atguigu.yygh.common.result.ResultCode;
import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.user.service.UserInfoService;
import com.atguigu.yygh.vo.user.UserInfoQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @ClassName AdminUserInfoController * @Description TODO
 * @Author ehdk
 * @Date 21:03 2022/7/25
 * @Version 1.0
 **/
@Api(tags = "用户管理接口")
@RestController
@RequestMapping("/admin/user/userInfo")
public class AdminUserInfoController {

    @Autowired
    private UserInfoService userInfoService;

    @ApiOperation("分页条件查询")
    @GetMapping("{page}/{limit}")
    public R list(@PathVariable Long page,
                  @PathVariable Long limit,
                  UserInfoQueryVo userInfoQueryVo) {
       IPage pageModel = userInfoService.queryPage(page,limit,userInfoQueryVo);
       return R.ok().data("pageModel",pageModel);

    }

    @ApiOperation("锁定和解锁")
    @PutMapping("lock/{userId}/{status}")
    public R lock(
            @PathVariable("userId") Long userId,
            @PathVariable("status") Integer status){
        if (userId!=null&&(status==0||status==1)) {
            UserInfo userInfo = new UserInfo();
            userInfo.setStatus(status);
            userInfo.setId(userId);
            userInfoService.updateById(userInfo);
            return R.ok().message("设置成功");
        }
        throw new YyghException("设置失败", ResultCode.ERROR);

    }
    @ApiOperation("用户详情")
    @GetMapping("show/{userId}")
    public R show(@PathVariable Long userId) {
        if (userId!=null){
            Map<String, Object> map = userInfoService.queryUserInfo(userId);
            return  R.ok().data(map);
        }
        throw new YyghException("查询用户详情失败", ResultCode.ERROR);
    }

    @ApiOperation("认证审批")
    @PutMapping("approval/{userId}/{authStatus}")
    public R approval(@PathVariable Long userId, @PathVariable Integer authStatus) {
        if(userId!=null&&(authStatus==2||authStatus==-1)){
           boolean flag = userInfoService.approval(userId,authStatus);
           if (flag){
               return R.ok().message("审批设置成功");
           }
           return R.error().message("审批设置失败");
        }
        throw new YyghException("认证审批失败", ResultCode.ERROR);
    }
}