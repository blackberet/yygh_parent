package com.atguigu.yygh.hosp.service;

import com.atguigu.yygh.model.hosp.Schedule;
import com.atguigu.yygh.vo.hosp.ScheduleOrderVo;

import java.util.List;
import java.util.Map;

public interface ScheService {
    Map<String, Object> findSchedule(Integer pageNum, Integer pageSize, String hoscode, String depcode);

    List<Schedule> findScheduleList(String hoscode, String depcode, String workDate);

    List<Schedule> getSchedule(String hoscode, String depcode);

    Map<String, Object> findPage(Integer pageNum, Integer pageSize, String hoscode, String depcode);

    Schedule getById(String id);

    ScheduleOrderVo getScheduleOrderVo(String scheduleId);

    void updateAvailableNumber(String scheduleId, Integer availableNumber);

    void cancelAvailableNumber(String scheduleId);
}
