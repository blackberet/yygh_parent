package com.atguigu.yygh.hosp.controller.user;

import com.atguigu.yygh.common.result.R;
import com.atguigu.yygh.hosp.service.DepartmentService;
import com.atguigu.yygh.hosp.service.DeptService;
import com.atguigu.yygh.hosp.service.HospService;
import com.atguigu.yygh.hosp.service.ScheService;
import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.model.hosp.Schedule;
import com.atguigu.yygh.vo.hosp.DepartmentVo;
import com.atguigu.yygh.vo.hosp.HospitalQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user/hosp")
public class UserHospitalController {

    @Autowired
    private HospService hospService;

    @Autowired
    private DeptService deptService;

    @Autowired
    private ScheService scheService;

    @PostMapping("/{pageNum}/{pageSize}")
    public R getHospitalList(@PathVariable Integer pageNum,
                             @PathVariable Integer pageSize,
                             @RequestBody HospitalQueryVo hospitalQueryVo
                             ){
        Page<Hospital> page = hospService.findPage(pageNum, pageSize, hospitalQueryVo);
        List<Hospital> list = page.getContent();
        return R.ok().data("list",list);
    }

    @GetMapping("/{hosname}")
    public R getHospitalList(@PathVariable String hosname){
        List<Hospital> hospitalList = hospService.getHospitalListByHosname(hosname);
        return R.ok().data("list",hospitalList);
    }

    @GetMapping("/department/{hoscode}")
    public R findDeptList(@PathVariable String hoscode){
        List<DepartmentVo> deptList = deptService.findDeptList(hoscode);
        return R.ok().data("list",deptList);
    }

    @GetMapping("/registered/{hoscode}")
    public R registered(@PathVariable String hoscode){
        Map<String,Object> map = hospService.registered(hoscode);
        return R.ok().data(map);
    }

    @GetMapping("/schedule")
    public R findSchedule(HttpServletRequest request){
        String hoscode = request.getParameter("hoscode");
        String depcode = request.getParameter("depcode");
        List<Schedule> list = scheService.getSchedule(hoscode,depcode);
        return R.ok().data("list",list);
    }


}
