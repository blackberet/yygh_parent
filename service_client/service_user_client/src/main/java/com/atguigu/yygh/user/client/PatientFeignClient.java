package com.atguigu.yygh.user.client;

import com.atguigu.yygh.model.user.Patient;
import com.atguigu.yygh.user.sentinel.PatientFeignClientFallBack;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Repository
@FeignClient(value = "service-user",path = "/user/patient",fallbackFactory = PatientFeignClientFallBack.class)
public interface PatientFeignClient {
    @GetMapping("getById/{id}")
    public Patient getById(@PathVariable("id") Long id);
}
