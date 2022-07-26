package com.atguigu.yygh.common.utils;

import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.dysmsapi20170525.models.SendSmsResponseBody;

import com.aliyun.tea.TeaException;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.models.RuntimeOptions;

/**
 * @ClassName SmsUtil * @Description TODO
 * @Author ehdk
 * @Date 15:32 2022/7/20
 * @Version 1.0
 **/
public class ALiSmsUtil {
    private static final String accessKeyId="LTAI5tLMCtb5ij8XtVrGPoyp";
    private static final String accessKeySecret="9DP87e00HjKs0veVLNnDfu0E3bSGjn";

    public static com.aliyun.dysmsapi20170525.Client createClient(String accessKeyId, String accessKeySecret) throws Exception {
        Config config = new Config()
                // 您的 AccessKey ID
                .setAccessKeyId(ALiSmsUtil.accessKeyId)
                // 您的 AccessKey Secret
                .setAccessKeySecret(ALiSmsUtil.accessKeySecret);
        // 访问的域名
        config.endpoint = "dysmsapi.aliyuncs.com";
        return new com.aliyun.dysmsapi20170525.Client(config);
    }

    public static void sendMessage(String phoneNumbers,String code) throws Exception {
       /* java.util.List<String> args = java.util.Arrays.asList(args_);*/
        com.aliyun.dysmsapi20170525.Client client = ALiSmsUtil.createClient(ALiSmsUtil.accessKeyId, ALiSmsUtil.accessKeySecret);
        SendSmsRequest sendSmsRequest = new SendSmsRequest()
                .setPhoneNumbers(phoneNumbers)
                .setSignName("谷粒")
                .setTemplateCode("SMS_96695065")
                .setTemplateParam("{\"code\":\""+code+"\"}");
        RuntimeOptions runtime = new RuntimeOptions();
        try {
            // 复制代码运行请自行打印 API 的返回值
            SendSmsResponse response = client.sendSmsWithOptions(sendSmsRequest, runtime);
            SendSmsResponseBody body = response.getBody();
            String message = body.getMessage();
            System.out.println(message);
        } catch (TeaException error) {
            // 如有需要，请打印 error
            com.aliyun.teautil.Common.assertAsString(error.message);
        } catch (Exception _error) {
            TeaException error = new TeaException(_error.getMessage(), _error);
            // 如有需要，请打印 error
            com.aliyun.teautil.Common.assertAsString(error.message);
        }
    }
}
