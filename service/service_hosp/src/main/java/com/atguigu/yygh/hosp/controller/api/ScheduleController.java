package com.atguigu.yygh.hosp.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.hosp.result.Result;
import com.atguigu.yygh.hosp.service.ScheduleService;
import com.atguigu.yygh.model.hosp.Schedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("/api/hosp")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @PostMapping("/schedule/list")
    public Result<Page> findSchedulePage(@RequestParam HashMap<String,String> paramMap){
        String pageNum = paramMap.get("page");
        String pageSize = paramMap.get("limit");
        String hoscode = paramMap.get("hoscode");
        Page<Schedule> page = scheduleService.findSchedulePage(pageNum,pageSize,hoscode);
        return Result.ok(page);
    }

    @PostMapping("/saveSchedule")
    public Result<Schedule> saveSchedule(@RequestParam HashMap<String,String> paramMap){
        Schedule schedule = JSONObject.parseObject(JSONObject.toJSONString(paramMap), Schedule.class);
        String hoscode = schedule.getHoscode();
        String depcode = schedule.getDepcode();
        String hosScheduleId = schedule.getHosScheduleId();
        scheduleService.saveSchedule(hoscode,depcode,hosScheduleId,schedule);
        return Result.ok();
    }

    @PostMapping("/schedule/remove")
    public Result<Schedule> removeSchedule(@RequestParam HashMap<String,String> paramMap){
        String hoscode = paramMap.get("hoscode");
        String hosScheduleId = paramMap.get("hosScheduleId");
        scheduleService.removeSchedule(hoscode,hosScheduleId);
        return Result.ok();
    }
}
