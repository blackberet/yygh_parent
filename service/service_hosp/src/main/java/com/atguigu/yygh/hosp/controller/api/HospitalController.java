package com.atguigu.yygh.hosp.controller.api;


import com.atguigu.yygh.common.exception.YYGHException;
import com.atguigu.yygh.common.utils.MD5;
import com.atguigu.yygh.hosp.result.HttpRequestHelper;
import com.atguigu.yygh.hosp.result.Result;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.hosp.service.HospitalSetService;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.model.hosp.HospitalSet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/api/hosp")
public class HospitalController {

    @Autowired
    private HospitalSetService hospitalSetService;

    @Autowired
    private HospitalService hospitalService;



    @PostMapping("/saveHospital")
    public Result<Hospital> saveHospital(HttpServletRequest request){
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, String> map = HttpRequestHelper.switchMap(parameterMap);
        //验证signkey
        String hospitalSign = map.get("sign");
        log.info("map的"+ hospitalSign);
        String hospitalHoscode = map.get("hoscode");
        HospitalSet hospitalSet = hospitalSetService.getSignByhospitalHoscode(hospitalHoscode);
        String signKey = hospitalSet.getSignKey();
        signKey = MD5.encrypt(signKey);
        log.info( "查出来的" + signKey);
        if (StringUtils.isEmpty(signKey) || StringUtils.isEmpty(hospitalSign) || !signKey.equals(hospitalSign)){
            throw new YYGHException(20001,"signkey有误");
        }
        //医院信息保存到mongodb中
        String logoData = map.get("logoData");
        map.put("logoData",logoData.replaceAll(" ", "+"));
        hospitalService.saveHospital(map);
        return Result.ok();
    }

    @PostMapping("/hospital/show")
    public Result<Hospital> showHospital(@RequestParam HashMap<String,String> parameterMap){
        //验证signkey
        String hospitalSign = parameterMap.get("sign");
        String hoscode = parameterMap.get("hoscode");
        if (StringUtils.isEmpty(hospitalSign)) {
            throw new YYGHException(20001,"sign为空");
        }
        //验证signkey通过去mongodb里面查询
        Hospital hospital = hospitalService.getHospitalByHoscode(hoscode);
        return Result.ok(hospital);
    }






}
