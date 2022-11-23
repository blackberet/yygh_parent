package com.atguigu.yygh.cmn.client;

import com.atguigu.yygh.cmn.sentinel.DictFeignClientFallBack;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "service-cmn",path = "/admin/cmn/dict",fallbackFactory = DictFeignClientFallBack.class)
public interface DictFeignClient {

    //根据省市区编号获取名字
    @GetMapping("/name/{value}")
    public String getNameByValue(@PathVariable("value") Long value);

    //根据类型获取类型名字
    @GetMapping("/name/{value}/{dictCode}")
    public String getNameByValueAndCode(@PathVariable("value") Long value,@PathVariable("dictCode") String dictCode);
}
