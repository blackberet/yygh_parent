package com.atguigu.yygh.cmn.sentinel;

import com.atguigu.yygh.cmn.client.DictFeignClient;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

//一定要放到容器中
@Component
public class DictFeignClientFallBack implements FallbackFactory<DictFeignClient> {
    @Override
    public DictFeignClient create(Throwable throwable) {
        return new DictFeignClient() {
            @Override
            public String getNameByValue(Long value) {
                return "服务优化升级中,请稍后访问!";
            }

            @Override
            public String getNameByValueAndCode(Long value, String dictCode) {
                return "服务优化升级中,请稍后访问!";
            }
        };
    }
}
