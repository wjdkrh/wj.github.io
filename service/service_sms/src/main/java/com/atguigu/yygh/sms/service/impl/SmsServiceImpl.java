package com.atguigu.yygh.sms.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.dysmsapi20170525.models.SendSmsResponseBody;
import com.aliyun.tea.TeaException;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.models.RuntimeOptions;
import com.atguigu.yygh.common.exception.YyghException;
import com.atguigu.yygh.common.utils.ALiSmsUtil;
import com.atguigu.yygh.sms.service.SmsService;
import com.atguigu.yygh.sms.utils.HttpUtils;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName SmsServiceImpl * @Description TODO
 * @Author ehdk
 * @Date 15:28 2022/7/20
 * @Version 1.0
 **/
@Service
@Slf4j
public class SmsServiceImpl implements SmsService {
    private String accessKeyId="LTAI5tLMCtb5ij8XtVrGPoyp";
    private String accessKeySecret="9DP87e00HjKs0veVLNnDfu0E3bSGjn";

    @Override
    public void send(String phone, String code) {
        String host = "http://dingxin.market.alicloudapi.com";
        String path = "/dx/sendSms";
        String method = "POST";
        String appcode = "cdb6db22056f43cc8013aefac5b09602";
        Map<String, String> headers = new HashMap<String, String>();
        //Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode); //appcode
        Map<String, String> querys = new HashMap<>();
        querys.put("mobile", phone);//手机号
        querys.put("param", "code:" + code); //参数列表
        querys.put("tpl_id", "TP1711063"); //短信验证码模板id
        Map<String, String> bodys = new HashMap<>();
        try {
            HttpResponse response = HttpUtils.doPost(host, path, method, headers,
                    querys, bodys);

            String data = EntityUtils.toString(response.getEntity());
            log.info(data);
            HashMap<String,String> resultMap = JSONObject.parseObject(data, HashMap.class);
          /*  Gson gson = new Gson();
            HashMap<String, String> resultMap = gson.fromJson(data, HashMap.class);*/
            String returnCode = resultMap.get("return_code");

            if(!"00000".equals(returnCode)){
                log.error("短信发送失败：return_code = " + returnCode);
                throw new YyghException("短信发送失败",20011);
            }

        } catch (Exception e) {
            throw new YyghException("短信发送失败",20011,e);
        }
    }

}
   /* @Override
    public boolean send(String phone, String code) {
        String message=null;
        try {Config config = new Config()
                // 您的 AccessKey ID
                .setAccessKeyId(accessKeyId)
                // 您的 AccessKey Secret
                .setAccessKeySecret(accessKeySecret);
        // 访问的域名
        config.endpoint = "dysmsapi.aliyuncs.com";
        com.aliyun.dysmsapi20170525.Client client = ALiSmsUtil.createClient(accessKeyId, accessKeySecret);
        SendSmsRequest sendSmsRequest = new SendSmsRequest()
                .setPhoneNumbers(phone)
                .setSignName("谷粒")
                .setTemplateCode("SMS_96695065")
                .setTemplateParam("{\"code\":\""+code+"\"}");
        RuntimeOptions runtime = new RuntimeOptions();
            // 复制代码运行请自行打印 API 的返回值
            SendSmsResponse response = client.sendSmsWithOptions(sendSmsRequest, runtime);
            SendSmsResponseBody body = response.getBody();
            message = body.getMessage();
        } catch (TeaException error) {
            // 如有需要，请打印 error
            com.aliyun.teautil.Common.assertAsString(error.message);
        } catch (Exception _error) {
            TeaException error = new TeaException(_error.getMessage(), _error);
            // 如有需要，请打印 error
            com.aliyun.teautil.Common.assertAsString(error.message);
        }
        if ("OK".equals(message)){
            return true;
        }else {
            return false;
        }
    }
*/


