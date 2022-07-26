package com.atguigu.yygh.oss.service;

import org.springframework.web.multipart.MultipartFile;

public interface FrontFileService {
    String upload(MultipartFile file);
}
