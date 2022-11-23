package com.atguigu.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.model.hosp.Hospital;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
public class HospitalServiceImpl implements HospitalService  {

    @Autowired
    private MongoTemplate mongoTemplate;


    @Override
    public void saveHospital(Map<String, String> map) {
        Hospital hospital = JSONObject.parseObject(JSONObject.toJSONString(map), Hospital.class);
        String hoscode = hospital.getHoscode();
        Hospital mongoHospital = mongoTemplate.findById(hoscode, Hospital.class);
        if (mongoHospital == null){
            hospital.setCreateTime(new Date());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            hospital.setStatus(0);
            mongoTemplate.save(hospital);
        }else {
            hospital.setId(mongoHospital.getId());
            hospital.setStatus(mongoHospital.getStatus());
            hospital.setCreateTime(mongoHospital.getCreateTime());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(mongoHospital.getIsDeleted());
            mongoTemplate.save(hospital);
        }


    }

    @Override
    public Hospital getHospitalByHoscode(String hoscode) {
        Query query = new Query(Criteria.where("hoscode").is(hoscode));
        Hospital hospital = mongoTemplate.findOne(query, Hospital.class);
        return hospital;
    }
}
