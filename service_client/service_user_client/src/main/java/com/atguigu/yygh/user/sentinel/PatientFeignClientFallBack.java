package com.atguigu.yygh.user.sentinel;

import com.atguigu.yygh.model.user.Patient;
import com.atguigu.yygh.user.client.PatientFeignClient;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class PatientFeignClientFallBack implements FallbackFactory<PatientFeignClient> {
    @Override
    public PatientFeignClient create(Throwable throwable) {
        return new PatientFeignClient() {
            @Override
            public Patient getById(Long id) {
                return new Patient();
            }
        };
    }
}
