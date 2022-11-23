package com.atguigu.yygh.hosp.service;

import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.vo.hosp.HospitalQueryVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface HospService {
    Page<Hospital> findPage(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo);

    void updateStatus(String id, Integer status);

    Map<String,Object> show(String id);

    String getNameByHoscode(String hoscode);

    List<Hospital> getHospitalListByHosname(String hosname);

    Map<String, Object> registered(String hoscode);

    Hospital findByHoscode(String hoscode);
}
