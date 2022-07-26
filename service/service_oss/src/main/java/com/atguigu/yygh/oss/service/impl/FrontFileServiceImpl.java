package com.atguigu.yygh.oss.service.impl;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.PutObjectRequest;
import com.atguigu.yygh.common.exception.YyghException;
import com.atguigu.yygh.common.result.ResultCode;
import com.atguigu.yygh.oss.service.FrontFileService;
import com.atguigu.yygh.oss.utils.ConstantProperties;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

/**
 * @ClassName FrontFileServiceImpl * @Description TODO
 * @Author ehdk
 * @Date 11:38 2022/7/25
 * @Version 1.0
 **/
@Service
@Slf4j
public class FrontFileServiceImpl implements FrontFileService {

    @Autowired
    ConstantProperties constantProperties;
    @Override
    public String upload(MultipartFile file) {
        // Endpoint以华东1（杭州）为例，其它Region请按实际情况填写。
        String endpoint = constantProperties.getEndpoint();
        // 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
        String accessKeyId = constantProperties.getKeyId();
        String accessKeySecret = constantProperties.getKeySecret();
        // 填写Bucket名称，例如examplebucket。
        String bucketName = constantProperties.getBucketName();
        // 填写Object完整路径，完整路径中不能包含Bucket名称，例如exampledir/exampleobject.txt。
        //TODO 用户上传的名字有可能文件名相同会导致文件覆盖，我们需要截取后缀拓展名，再使用UUID重命名用户上传的拓展名
        String originalFilename = file.getOriginalFilename();//用户上传的文件名
        String extensionName = originalFilename.substring(originalFilename.lastIndexOf("."));//截取的拓展名列如.jpg
        String newFileName= UUID.randomUUID().toString()+extensionName;//新的文件名，使用UUID保证唯一，文件名不冲突
        String objectName = new DateTime(new Date()).toString("yyyy/MM/dd/")+newFileName;//组装好了object完整路径
        //TODO  objectName是从年/月/日 在阿里云服务器路径中+文件名；如果一直是同一个文件夹下，文件太多导致性能差；说白了是和磁盘上路径一样

        // 填写本地文件的完整路径，例如D:\\localpath\\examplefile.txt。
        // 如果未指定本地路径，则默认从示例程序所属项目对应本地路径中上传文件。
       /* String filePath= "D:\\localpath\\examplefile.txt";*/

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {
            // 创建PutObjectRequest对象。第三个参数是文件输入流
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, file.getInputStream());
            // 如果需要上传时设置存储类型和访问权限，请参考以下示例代码。
            // ObjectMetadata metadata = new ObjectMetadata();
            // metadata.setHeader(OSSHeaders.OSS_STORAGE_CLASS, StorageClass.Standard.toString());
            // metadata.setObjectAcl(CannedAccessControlList.Private);
            // putObjectRequest.setMetadata(metadata);
            // 上传文件。
            ossClient.putObject(putObjectRequest);

            /*https://yygh-0309.oss-cn-hangzhou.aliyuncs.com/test/2.jpg*/
            String url ="https://"+constantProperties.getBucketName()+"."+constantProperties.getEndpoint()+"/"+objectName;
            return url;
        } catch (OSSException oe) {
            log.error("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            log.error("Error Message:" + oe.getErrorMessage());
            log.error("Error Code:" + oe.getErrorCode());
            log.error("Request ID:" + oe.getRequestId());
            log.error("Host ID:" + oe.getHostId());
            log.error(oe.getMessage());
            throw new YyghException("文件上传错误", ResultCode.ERROR,oe);
        } catch (ClientException ce) {
            log.error("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            log.error("Error Message:" + ce.getMessage());
            throw new YyghException("文件上传错误", ResultCode.ERROR,ce);
        } catch (Exception e) {
            throw  new YyghException("文件上传错误", ResultCode.ERROR,e);
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }
}

