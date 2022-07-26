package com.atguigu.yygh.cmn.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @ClassName DictFeignClient * @Description TODO
 * @Author ehdk
 * @Date 19:11 2022/7/15
 * @Version 1.0
 **/
@FeignClient("service-cmn")
public interface DictFeignClient {

    @GetMapping(value = "inner/cmn/dict/getName")
     String getName(@RequestParam String parentDictCode, @RequestParam String value);

}
