package com.atguigu.yygh.hosp.service.impl;

import com.atguigu.yygh.hosp.repo.DepartmentRepository;
import com.atguigu.yygh.hosp.service.DeptService;
import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.vo.hosp.DepartmentVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DeptServiceImpl implements DeptService {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
    public List<DepartmentVo> findDeptList(String hoscode) {
        Department department = new Department();
        department.setHoscode(hoscode);
        Example<Department> example = Example.of(department);
        //所有科室列表
        List<Department> deptList = departmentRepository.findAll(example);
        //根据大科室code分组
        Map<String, List<Department>> map = deptList.stream().collect(Collectors.groupingBy(Department::getBigcode));
        Set<Map.Entry<String, List<Department>>> entries = map.entrySet();

        ArrayList<DepartmentVo> allDeptList = new ArrayList<>();
        ArrayList<DepartmentVo> bigDeptList = new ArrayList<>();

        for (Map.Entry<String, List<Department>> entry : entries) {
            //大科室编号,一个key代表一个大科室code
            String bigcode = entry.getKey();
            //当前大科室下的子科室
            List<Department> zilist = entry.getValue();
            //封装大科室
            DepartmentVo departmentVo = new DepartmentVo();
            //大科室的code
            departmentVo.setDepcode(bigcode);
            //大科室的名字
            departmentVo.setDepname(zilist.get(0).getDepname());
            bigDeptList.add(departmentVo);
            //封装小科室
            ArrayList<DepartmentVo> smallDeptList = new ArrayList<>();
            for (Department department1 : zilist) {
                DepartmentVo departmentVo1 = new DepartmentVo();
                BeanUtils.copyProperties(department1,departmentVo1);
                smallDeptList.add(departmentVo1);
            }
            //departmentVo的Children属性赋值,就是小科室列表
            departmentVo.setChildren(smallDeptList);
            //装到统一的list里面
            allDeptList.add(departmentVo);
        }
        return allDeptList;
    }

    @Override
    public String getDepName(String hoscode, String depcode) {
        Department department = departmentRepository.findByHoscodeAndDepcode(hoscode, depcode);
        if(department != null) {
            return department.getDepname();
        }
        return null;
    }

    @Override
    public Department getDeptByHoscodeAndDepcode(String hoscode, String depcode) {
        Department department = departmentRepository.findByHoscodeAndDepcode(hoscode, depcode);
        return department;
    }
}
