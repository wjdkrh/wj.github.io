package com.atguigu.yygh.user.service;

import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.vo.user.LoginVo;
import com.atguigu.yygh.vo.user.UserAuthVo;
import com.atguigu.yygh.vo.user.UserInfoQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;


import java.util.Map;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author atguigu
 * @since 2022-07-19
 */
public interface UserInfoService extends IService<UserInfo> {

    Map<String, Object> login(LoginVo loginVo);

    UserInfo queryUserInfoByOpenId(String openid);

    UserInfo getUserInfoById(Long userId);

    void userAuth(Long userId, UserAuthVo userAuthVo);

    IPage<UserInfo> queryPage(Long page, Long limit, UserInfoQueryVo userInfoQueryVo);

    Map<String,Object> queryUserInfo(Long userId);

    boolean approval(Long userId, Integer authStatus);
}
