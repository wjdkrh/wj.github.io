package com.atguigu.yygh.user.service.impl;

import com.atguigu.yygh.common.exception.YyghException;
import com.atguigu.yygh.common.result.ResultCode;
import com.atguigu.yygh.common.utils.JwtHelper;
import com.atguigu.yygh.enums.AuthStatusEnum;
import com.atguigu.yygh.model.user.Patient;
import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.user.mapper.UserInfoMapper;
import com.atguigu.yygh.user.service.PatientService;
import com.atguigu.yygh.user.service.UserInfoService;
import com.atguigu.yygh.vo.user.LoginVo;
import com.atguigu.yygh.vo.user.UserAuthVo;
import com.atguigu.yygh.vo.user.UserInfoQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2022-07-19
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {
    @Autowired
    RedisTemplate<String,String> redisTemplate;
    @Autowired
    PatientService patientService;
    @Override
    public Map<String, Object> login(LoginVo loginVo) {
        String phone = loginVo.getPhone();
        String code = loginVo.getCode();
        String openid = loginVo.getOpenid();
        //对参数做非空校验
        if (StringUtils.isEmpty(phone)||StringUtils.isEmpty(code)){
            throw new YyghException("参数有误:Login", ResultCode.ERROR);
        }
        String codeByRedis =redisTemplate.opsForValue().get("code:"+phone);

        if (!code.equals(codeByRedis)){
            throw new YyghException("验证码错误", ResultCode.ERROR);
        }
            UserInfo userInfo;
         //1.通过手机号注册的用户，没有openid，首先如果是第一次注册，先保存信息即可
        //如果可以查询到，说明已经注册了。验证下status是否为0直接组装token登录即可
            //先通过手机号进行登录，根据手机号从数据库查询:该用户的状态是否是锁定状态，在判断是否是新用户还是老用户再做判断
       if(StringUtils.isEmpty(openid)) {
           userInfo = baseMapper.selectOne(new QueryWrapper<UserInfo>().eq("phone", phone));
           if (userInfo == null) {
               //如果为空就进行注册 添加到user_info表中

               userInfo = new UserInfo();
               userInfo.setName(phone);
               userInfo.setNickName(phone);
               userInfo.setPhone(phone);
               userInfo.setStatus(1);
               userInfo.setCreateTime(new Date());
               userInfo.setUpdateTime(new Date());
               baseMapper.insert(userInfo);
           } else {
               Integer status = userInfo.getStatus();
               if (status == 0) {
                   throw new YyghException("该用户账号已锁定", ResultCode.ERROR);
               }
           }
       }else {
           //2.通过微信，前端已经过滤了 手机号不为空不需要绑定的步骤。来到这里的请求都是需要设置手机号，重新更新
               //查询数据库中是否用手机登录注册过
               UserInfo userInfo1 = baseMapper.selectOne(new QueryWrapper<UserInfo>().eq("phone", phone));
               if (userInfo1!=null) {
                   throw new YyghException("手机号已经注册", ResultCode.ERROR);
               }
               userInfo = this.queryUserInfoByOpenId(openid);
               userInfo.setPhone(phone);
               baseMapper.updateById(userInfo);
       }
        //返回页面显示名称
        Map<String, Object> map = new HashMap<>();
        String name = userInfo.getName();
        Long id = userInfo.getId();
        String token = JwtHelper.createToken(id, name);
        map.put("name", name);
        map.put("token", token); //TODO 访问令牌
        return map;
    }

    @Override
    public UserInfo queryUserInfoByOpenId(String openid) {
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("openid",openid);
        UserInfo userInfo = baseMapper.selectOne(queryWrapper);

        return userInfo;
    }

    @Override
    public UserInfo getUserInfoById(Long userId) {
        UserInfo userInfo = baseMapper.selectById(userId);
        String statusNameByStatus = AuthStatusEnum.getStatusNameByStatus(userInfo.getAuthStatus());
       userInfo.getParam().put("authStatusString",statusNameByStatus);
        return userInfo;
    }

    @Override
    public void userAuth(Long userId, UserAuthVo userAuthVo) {
        UserInfo userInfo = baseMapper.selectById(userId);
        BeanUtils.copyProperties(userAuthVo,userInfo);
        userInfo.setAuthStatus(AuthStatusEnum.AUTH_RUN.getStatus());
        baseMapper.updateById(userInfo);
    }

    @Override
    public  IPage<UserInfo> queryPage(Long page, Long limit, UserInfoQueryVo userInfoQueryVo) {
        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
        String keyword = userInfoQueryVo.getKeyword();
        String timeBegin = userInfoQueryVo.getCreateTimeBegin();
        String timeEnd = userInfoQueryVo.getCreateTimeEnd();
        wrapper.and(!StringUtils.isEmpty(keyword),t-> t.like("name",userInfoQueryVo.getKeyword()).or().like("phone",userInfoQueryVo.getKeyword()))
                .gt(!StringUtils.isEmpty(timeBegin),"create_time",timeBegin).lt(!StringUtils.isEmpty(timeEnd),"create_time",timeEnd);
        IPage<UserInfo> userInfoPage = new Page<>(page, limit);
         baseMapper.selectPage(userInfoPage, wrapper);
         userInfoPage.getRecords().forEach(userInfo -> this.packageUserInfo(userInfo));
        return userInfoPage;
    }
    private UserInfo packageUserInfo(UserInfo userInfo){
        userInfo.getParam().put("authStatusString",AuthStatusEnum.getStatusNameByStatus(userInfo.getAuthStatus()));
        Integer status = userInfo.getStatus();
       String statusValue =  status.intValue()==0? "锁定": "正常";
        userInfo.getParam().put("statusString",statusValue);
        return userInfo;

    }

    @Override
    public Map<String,Object> queryUserInfo(Long userId) {
        Map<String, Object> map = new HashMap<>();
        UserInfo userInfo = baseMapper.selectById(userId);
        this.packageUserInfo(userInfo);
        List<Patient> patientList = patientService.findAllByUserId(userId);
        map.put("userInfo",userInfo);
        map.put("patientList",patientList);
        return map;
    }

    @Override
    public boolean approval(Long userId, Integer authStatus) {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(userId);
        userInfo.setAuthStatus(authStatus);
        return updateById(userInfo);
    }
}
