package com.atguigu.yygh.sms.controller;

import com.atguigu.yygh.common.result.R;
import com.atguigu.yygh.sms.service.SMSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/sms")
public class SMSController {
    @Autowired
    private SMSService smsService;

    @GetMapping("/{phone}")
    public R sendCode(@PathVariable String phone){
        Boolean flag = smsService.sendCode(phone);
        if (flag){
            return R.ok();
        }else {
            return R.error();
        }
    }

}
