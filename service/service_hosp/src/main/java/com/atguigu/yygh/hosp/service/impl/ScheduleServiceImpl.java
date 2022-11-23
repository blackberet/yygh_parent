package com.atguigu.yygh.hosp.service.impl;

import com.atguigu.yygh.hosp.repo.ScheduleRepository;
import com.atguigu.yygh.hosp.service.ScheduleService;
import com.atguigu.yygh.model.hosp.Schedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Override
    public Page<Schedule> findSchedulePage(String pageNum, String pageSize, String hoscode) {
        Schedule schedule = new Schedule();
        schedule.setHoscode(hoscode);
        schedule.setIsDeleted(0);
        Example<Schedule> example = Example.of(schedule);
        Pageable pageable = PageRequest.of(Integer.parseInt(pageNum) - 1,Integer.parseInt(pageSize), Sort.by("createTime").ascending());
        Page<Schedule> page = scheduleRepository.findAll(example, pageable);
        return page;
    }

    @Override
    public void saveSchedule(String hoscode, String depcode, String hosScheduleId,Schedule schedule) {
        Schedule mongoSchedule = scheduleRepository.findByHoscodeAndDepcodeAndHosScheduleId(hoscode,depcode,hosScheduleId);
        if (mongoSchedule == null){
            schedule.setCreateTime(new Date());
            schedule.setUpdateTime(new Date());
            schedule.setIsDeleted(0);
            scheduleRepository.save(schedule);
        }else{
            schedule.setCreateTime(mongoSchedule.getCreateTime());
            schedule.setUpdateTime(new Date());
            schedule.setIsDeleted(mongoSchedule.getIsDeleted());
            schedule.setId(mongoSchedule.getId());
            scheduleRepository.save(schedule);
        }
    }

    @Override
    public void removeSchedule(String hoscode, String hosScheduleId) {
        Schedule schedule = scheduleRepository.findByHoscodeAndHosScheduleId(hoscode,hosScheduleId);
        schedule.setIsDeleted(1);
        scheduleRepository.save(schedule);
    }
}
