package com.atguigu.yygh.user.service.impl;

import com.atguigu.yygh.cmn.client.DictFeignClient;
import com.atguigu.yygh.model.user.Patient;
import com.atguigu.yygh.user.mapper.PatientMapper;
import com.atguigu.yygh.user.service.PatientService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientServiceImpl extends ServiceImpl<PatientMapper,Patient> implements PatientService{
    @Autowired
    private DictFeignClient dictFeignClient;

    @Override
    public List<Patient> findAll(Long userId) {
        QueryWrapper<Patient> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);
        List<Patient> list = baseMapper.selectList(queryWrapper);
        for (Patient patient : list) {
            String certificatesType = patient.getCertificatesType();
            String name = dictFeignClient.getNameByValue(Long.parseLong(certificatesType));
            patient.getParam().put("certificatesTypeString",name);
        }
        return list;
    }

    @Override
    public Patient findById(Long id) {
        Patient patient = baseMapper.selectById(id);
        String certificatesType = patient.getCertificatesType();
        String name = dictFeignClient.getNameByValue(Long.parseLong(certificatesType));
        patient.getParam().put("certificatesTypeString",name);
        String provinceString = dictFeignClient.getNameByValue(Long.parseLong(patient.getProvinceCode()));
        String cityString = dictFeignClient.getNameByValue(Long.parseLong(patient.getCityCode()));
        String districtString = dictFeignClient.getNameByValue(Long.parseLong(patient.getDistrictCode()));
        patient.getParam().put("provinceString",provinceString);
        patient.getParam().put("cityString",cityString);
        patient.getParam().put("districtString",districtString);
        return patient;
    }

}
