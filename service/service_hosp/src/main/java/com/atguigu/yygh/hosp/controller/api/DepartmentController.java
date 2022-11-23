package com.atguigu.yygh.hosp.controller.api;

import com.atguigu.yygh.common.exception.YYGHException;
import com.atguigu.yygh.common.utils.MD5;
import com.atguigu.yygh.hosp.result.Result;
import com.atguigu.yygh.hosp.service.DepartmentService;
import com.atguigu.yygh.hosp.service.HospitalSetService;
import com.atguigu.yygh.model.hosp.Department;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("/api/hosp")
public class DepartmentController {

    @Autowired
    private HospitalSetService hospitalSetService;

    @Autowired
    private DepartmentService departmentService;

    @PostMapping("/saveDepartment")
    public Result<Department> saveDepartment(@RequestParam HashMap<String,String> paramMap){
        //验证参数
        //验证签名
        String departmentSign = paramMap.get("sign");
        String hoscode = paramMap.get("hoscode");
        String signKey = hospitalSetService.getSignByhospitalHoscode(hoscode).getSignKey();
        signKey = MD5.encrypt(signKey);
        if (StringUtils.isEmpty(signKey) || StringUtils.isEmpty(departmentSign) || !signKey.equals(departmentSign)){
            throw new YYGHException(20001,"signkey有误");
        }
        //科室信息保存到mongodb中
        departmentService.saveDepartment(paramMap);
        return Result.ok();
    }

    @PostMapping("/department/list")
    public Result<Page> findPage(@RequestParam HashMap<String,String> paramMap){
        String pageNum = paramMap.get("page");
        String pageSize = paramMap.get("limit");
        String hoscode = paramMap.get("hoscode");
        Page<Department> page = departmentService.findPage(pageNum,pageSize,hoscode);
        return Result.ok(page);
    }

    @PostMapping("/department/remove")
    public Result<Department> remove(@RequestParam HashMap<String,String> paramMap){
        String hoscode = paramMap.get("hoscode");
        String depcode = paramMap.get("depcode");
        departmentService.remove(hoscode,depcode);
        return Result.ok();
    }
}
