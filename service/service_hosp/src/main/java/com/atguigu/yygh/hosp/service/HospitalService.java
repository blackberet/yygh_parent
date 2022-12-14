package com.atguigu.yygh.hosp.service;


import com.atguigu.yygh.model.hosp.Hospital;

import java.util.Map;

public interface HospitalService {
    void saveHospital(Map<String, String> map);

    Hospital getHospitalByHoscode(String hoscode);
}
