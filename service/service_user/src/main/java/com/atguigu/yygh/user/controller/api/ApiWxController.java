package com.atguigu.yygh.user.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.common.exception.YyghException;
import com.atguigu.yygh.common.result.R;
import com.atguigu.yygh.common.result.ResultCode;
import com.atguigu.yygh.common.utils.JwtHelper;
import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.user.service.UserInfoService;
import com.atguigu.yygh.user.utils.ConstantProperties;
import com.atguigu.yygh.user.utils.HttpClientUtils;
import com.google.gson.JsonObject;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @ClassName ApiWxController * @Description TODO
 * @Author ehdk
 * @Date 16:35 2022/7/22
 * @Version 1.0
 **/
@Api(tags = "微信扫码登录")
@Controller//注意这里没有配置 @RestController
@RequestMapping("/api/user/wx")
@Slf4j
public class ApiWxController {

    @Autowired
    private ConstantProperties constantProperties;
    @Autowired
    private UserInfoService userInfoService;

/*
     * 方式一：在新的页面中展示登录二维码
    @GetMapping("getQRCodeParams")
    public String getQRCodeUrl(HttpSession session){
        try {
            //处理回调url
            String  redirectUri = URLEncoder.encode(constantProperties.getRedirectUri(), "UTF-8");

            //处理state：生成随机数，存入session
            String state = UUID.randomUUID().toString();
            log.info("生成 state = " + state);
            session.setAttribute("wx_open_state", state);

            String qrcodeUrl = "https://open.weixin.qq.com/connect/qrconnect" +
                    "?appid=" + constantProperties.getAppId() +
                    "&redirect_uri=" + redirectUri +
                    "&response_type=code" +
                    "&scope=snsapi_login" +
                    "&state=" + state +
                    "#wechat_redirect";

            return "redirect:" + qrcodeUrl;

        } catch (Exception e) {
            throw new YyghException("生成二维码失败",20014, e);
        }

    }
    */

    /*  @GetMapping("test")
      public void test(){
          String appId = constantProperties.getAppId();
          System.out.println(appId);
      }*/
    @GetMapping("getQRCodeParams")
    @ResponseBody
    public R getQRCodeParams(HttpSession session) {

        try {

            //处理回调url
            String redirectUri = URLEncoder.encode(constantProperties.getRedirectUri(), "UTF-8");

            //处理state：生成随机数，存入session
            String state = UUID.randomUUID().toString().replaceAll("-", "");
            log.info("生成 state = " + state);
            session.setAttribute("wx_open_state", state);
            log.info("sessionid = " + session.getId());

            //组装前端需要的参数
            Map<String, Object> map = new HashMap<>();
            map.put("scope", "snsapi_login");
            map.put("appid", constantProperties.getAppId());
            map.put("redirectUri", redirectUri);
            map.put("state", state);

            return R.ok().data(map);

        } catch (Exception e) {
            throw new YyghException("生成二维码失败", ResultCode.ERROR, e);
        }
    }

    @GetMapping("callback")
    public String callback(String code, String state, HttpSession session) {
        try {
            String wxOpenState = (String) session.getAttribute("wx_open_state");
            log.info("回调state: " + state);
            log.info("回调sessionId: " + session.getId());
            if (StringUtils.isEmpty(code) || StringUtils.isEmpty(state) || !state.equals(wxOpenState)) {
                throw new YyghException("非法的请求参数", ResultCode.ERROR);
            }

            //通过临时code向授权服务器，请求accesstoken
            String url = "https://api.weixin.qq.com/sns/oauth2/access_token?" +
                    "appid=" + constantProperties.getAppId() +
                    "&secret=" + constantProperties.getAppSecret() +
                    "&code=" + code +
                    "&grant_type=authorization_code";
            String accessTokenResp = HttpClientUtils.get(url);
            log.info("响应的accessToken信息：" + accessTokenResp);
            HashMap accessTokenMap = JSONObject.parseObject(accessTokenResp, HashMap.class);
            String accessToken = (String) accessTokenMap.get("access_token");
            String openid = (String) accessTokenMap.get("openid");
            //通过openid在数据库中查询，是否用户已经通过扫码登录过，如果查询到，就不需要在从微信端获取用户信息了
            //没查询的话继续 通过accesstoken+openid 从微信端请求用户信息
            UserInfo userInfo = userInfoService.queryUserInfoByOpenId(openid);
            if (userInfo==null) {
            String userInfoUrl = "https://api.weixin.qq.com/sns/userinfo?" +
                    "access_token=" + accessToken +
                    "&openid=OPENID" + openid;
            String userInfoResp = HttpClientUtils.get(userInfoUrl);
            HashMap userInfoMap = JSONObject.parseObject(userInfoResp, HashMap.class);
            userInfo=new UserInfo();
            userInfo.setNickName((String) userInfoMap.get("nickname"));
            userInfo.setName((String) userInfoMap.get("nickname"));
            userInfo.setOpenid(openid);
            userInfo.setPhone("");//虽然数据看有默认值，但是前端需要，如果不设置，前端接受到的字符串是null，不会做手机绑定
            userInfoService.save(userInfo);
            }else {
                if (userInfo.getStatus()==0){
                    throw new YyghException("用户已被锁定无法登录",ResultCode.ERROR);
                }
            }
            String token = JwtHelper.createToken(userInfo.getId(), userInfo.getName());


            return "redirect:http://localhost:3000/" +
                    "?token=" + token +
                    "&name=" + URLEncoder.encode(userInfo.getName(), "utf-8") +
                    "&openid=" + openid +
                    "&phone=" + userInfo.getPhone() ;
        } catch (Exception e) {
            throw new YyghException("微信登录失败",ResultCode.ERROR,e);
        }

    }
}