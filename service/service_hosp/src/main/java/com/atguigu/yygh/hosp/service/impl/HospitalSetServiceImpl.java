package com.atguigu.yygh.hosp.service.impl;


import com.atguigu.yygh.common.exception.YYGHException;
import com.atguigu.yygh.hosp.mapper.HospitalSetMapper;
import com.atguigu.yygh.hosp.service.HospitalSetService;
import com.atguigu.yygh.model.hosp.HospitalSet;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class HospitalSetServiceImpl extends ServiceImpl<HospitalSetMapper,HospitalSet> implements HospitalSetService {

    @Override
    public HospitalSet getSignByhospitalHoscode(String hospitalHoscode) {

        QueryWrapper<HospitalSet> hospitalSetQueryWrapper = new QueryWrapper<>();
        hospitalSetQueryWrapper.eq("hoscode",hospitalHoscode);
        HospitalSet hospitalSet = baseMapper.selectOne(hospitalSetQueryWrapper);
        if (hospitalSet == null) {
            throw  new YYGHException(20001,"没有该医院信息");
        }

        return hospitalSet;
    }
}
