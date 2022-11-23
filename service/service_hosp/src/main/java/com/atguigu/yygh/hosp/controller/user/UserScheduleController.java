package com.atguigu.yygh.hosp.controller.user;

import com.atguigu.yygh.common.result.R;
import com.atguigu.yygh.hosp.service.ScheService;
import com.atguigu.yygh.model.hosp.Schedule;
import com.atguigu.yygh.vo.hosp.ScheduleOrderVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user/schedule")
public class UserScheduleController {

    @Autowired
    private ScheService scheService;

    @GetMapping("/{pageNum}/{pageSize}/{hoscode}/{depcode}")
    public R findPage(@PathVariable Integer pageNum,
                      @PathVariable Integer pageSize,
                      @PathVariable String hoscode,
                      @PathVariable String depcode){
        Map<String,Object> map = scheService.findPage(pageNum,pageSize,hoscode,depcode);
        return R.ok().data(map);
    }

    @GetMapping("/findScheduleList/{hoscode}/{depcode}/{workDate}")
    public R findScheduleList(@PathVariable String hoscode,
                              @PathVariable String depcode,
                              @PathVariable String workDate){
        List<Schedule> scheduleList = scheService.findScheduleList(hoscode, depcode, workDate);
        return R.ok().data("list",scheduleList);
    }

    @GetMapping("/getSchedule/{id}")
    public R getSchedule (@PathVariable String id){
        Schedule schedule = scheService.getById(id);
        return R.ok().data("schedule",schedule);
    }

    @GetMapping("/getScheduleOrderVo/{scheduleId}")
    public ScheduleOrderVo getScheduleOrderVo(@PathVariable("scheduleId") String scheduleId) {
        ScheduleOrderVo scheduleOrderVo = scheService.getScheduleOrderVo(scheduleId);
        return scheduleOrderVo;
    }

}
