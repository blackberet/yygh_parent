package com.atguigu.yygh.user.controller.user;

import com.atguigu.yygh.common.result.R;
import com.atguigu.yygh.common.utils.JwtHelper;
import com.atguigu.yygh.model.user.Patient;
import com.atguigu.yygh.user.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/patient")
public class PatientController {
    @Autowired
    private PatientService patientService;


    //查全部
    @GetMapping("/findAll")
    public R findAll(@RequestHeader String token){
        Long userId = JwtHelper.getUserId(token);
        List<Patient> list = patientService.findAll(userId);
        return R.ok().data("list",list);
    }

    //添加
    @PostMapping("/save")
    public R save(@RequestHeader String token,@RequestBody Patient patient){
        Long userId = JwtHelper.getUserId(token);
        patient.setUserId(userId);
        patientService.save(patient);
        return R.ok();
    }

    //根据id获取就诊人信息，修改就诊人信息时回显
    @GetMapping("/findById/{id}")
    public R getPatient(@PathVariable Long id) {
        Patient patient = patientService.findById(id);
        return R.ok().data("patient",patient);
    }
    //修改就诊人
    @PostMapping("/update")
    public R updatePatient(@RequestBody Patient patient) {
        patientService.updateById(patient);
        return R.ok();
    }
    //删除就诊人
    @DeleteMapping("/remove/{id}")
    public R removePatient(@PathVariable Long id) {
        patientService.removeById(id);
        return R.ok();
    }

    @GetMapping("getById/{id}")
    public Patient getById(@PathVariable("id") Long id){
        Patient patient = patientService.findById(id);
        return patient;
    }


}
