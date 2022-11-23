package com.atguigu.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.hosp.repo.DepartmentRepository;
import com.atguigu.yygh.hosp.service.DepartmentService;
import com.atguigu.yygh.model.hosp.Department;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;


import java.util.Date;
import java.util.HashMap;
import java.util.Optional;


@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
    public void saveDepartment(HashMap<String, String> paramMap) {
        Department department = JSONObject.parseObject(JSONObject.toJSONString(paramMap), Department.class);
        Query query = new Query(Criteria.where("hoscode").is(department.getHoscode()).and("depcode").is(department.getDepcode()));
        Department mongodbDepartment = mongoTemplate.findOne(query, Department.class);
        if (mongodbDepartment == null) {
            //新增
            department.setCreateTime(new Date());
            department.setUpdateTime(new Date());
            department.setIsDeleted(0);
            mongoTemplate.save(department);
        }else{
            //修改
            department.setCreateTime(mongodbDepartment.getCreateTime());
            department.setUpdateTime(new Date());
            department.setIsDeleted(0);
            department.setId(mongodbDepartment.getId());
            mongoTemplate.save(department);
        }
    }

    @Override
    public Page<Department> findPage(String pageNum, String pageSize, String hoscode) {
        Department department = new Department();
        department.setHoscode(hoscode);
        department.setIsDeleted(0);
        Example<Department> example = Example.of(department);
        Pageable pageable = PageRequest.of(Integer.parseInt(pageNum) - 1, Integer.parseInt(pageSize));
        Page<Department> page = departmentRepository.findAll(example, pageable);
        return page;
    }

    @Override
    public void remove(String hoscode, String depcode) {
        Department department = departmentRepository.findByHoscodeAndDepcode(hoscode,depcode);
        if (department != null) {
            department.setIsDeleted(1);
            departmentRepository.save(department);
        }
    }


}
