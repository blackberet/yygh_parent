package com.atguigu.yygh.hosp.controller.admin;

import com.atguigu.yygh.common.result.R;
import com.atguigu.yygh.hosp.service.ScheService;
import com.atguigu.yygh.model.hosp.Schedule;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/hosp/schedule")
public class ScheController {

    @Autowired
    private ScheService scheService;


    @GetMapping("/findSchedule/{pageNum}/{pageSize}/{hoscode}/{depcode}")
    public R findSchedule(@PathVariable("pageNum") Integer pageNum,
                          @PathVariable("pageSize") Integer pageSize,
                          @PathVariable("hoscode") String hoscode,
                          @PathVariable("depcode") String depcode){
        Map<String,Object> map = scheService.findSchedule(pageNum,pageSize,hoscode,depcode);
        return R.ok().data(map);
    }

    //根据医院编号 、科室编号和工作日期，查询排班详细信息
    @ApiOperation(value = "查询排班详细信息")
    @GetMapping("/getScheduleDetail/{hoscode}/{depcode}/{workDate}")
    public R getScheduleDetail( @PathVariable String hoscode,
                                @PathVariable String depcode,
                                @PathVariable String workDate) {
        List<Schedule> list = scheService.findScheduleList(hoscode,depcode,workDate);
        return R.ok().data("list",list);
    }


}
