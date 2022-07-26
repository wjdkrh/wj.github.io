package com.atguigu.yygh.oss.controller.front;

import com.atguigu.yygh.common.result.R;
import com.atguigu.yygh.oss.service.FrontFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @ClassName FrontFileController * @Description TODO
 * @Author ehdk
 * @Date 11:36 2022/7/25
 * @Version 1.0
 **/

@Api(tags = "阿里云文件管理")
@RestController
@RequestMapping("/front/oss/file")
public class FrontFileController {
    @Autowired
    FrontFileService frontFileService;
    /**
     * 文件上传
     */
    @ApiOperation("文件上传")
    @PostMapping("upload")
    public R upload(MultipartFile file) {
           String url = frontFileService.upload(file);
        return R.ok().message("文件上传成功").data("url",url);
    }
}