package com.atguigu.yygh.hosp.service;

import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.vo.hosp.DepartmentVo;

import java.util.List;

public interface DeptService {
    List<DepartmentVo> findDeptList(String hoscode);

    //根据科室编号，和医院编号，查询科室名称
    String getDepName(String hoscode, String depcode);

    Department getDeptByHoscodeAndDepcode(String hoscode, String depcode);
}
