package com.atguigu.yygh.hosp.service;

import com.atguigu.yygh.model.hosp.Department;
import org.springframework.data.domain.Page;

import java.util.HashMap;


public interface DepartmentService {
    void saveDepartment(HashMap<String, String> paramMap);

    Page<Department> findPage(String pageNum, String pageSize, String hoscode);

    void remove(String hoscode, String depcode);
}
