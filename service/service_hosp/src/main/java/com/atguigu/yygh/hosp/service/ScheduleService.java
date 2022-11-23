package com.atguigu.yygh.hosp.service;

import com.atguigu.yygh.model.hosp.Schedule;
import org.springframework.data.domain.Page;

public interface ScheduleService {
    Page<Schedule> findSchedulePage(String pageNum, String pageSize, String hoscode);

    void saveSchedule(String hoscode, String depcode, String hosScheduleId,Schedule schedule);

    void removeSchedule(String hoscode, String hosScheduleId);
}
