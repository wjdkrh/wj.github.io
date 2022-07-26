package com.atguigu.yygh.common.utils;

import javax.servlet.http.HttpServletRequest;

public class AuthContextHolder {

    /**
     * 获取当前用户id
     * @param request
     * @return
     */
    public static Long getUserId(HttpServletRequest request) {
        String token = request.getHeader("token");
        Long userId = JwtHelper.getUserId(token);
        return userId;
    }

    /**
     * 获取当前用户名称
     * @param request
     * @return
     */
    public static String getUserName(HttpServletRequest request) {
        String token = request.getHeader("token");
        String userName = JwtHelper.getUserName(token);
        return userName;
    }
    
     /**
     * 校验token是否合法
     * @param request
     */
    public static void checkAuth(HttpServletRequest request){

        String token = request.getHeader("token");
        JwtHelper.parseToken(token);
    }
}