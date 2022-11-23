package com.atguigu.yygh.hosp.service.impl;

import com.atguigu.yygh.common.exception.YYGHException;
import com.atguigu.yygh.common.utils.DataTimeConvert;
import com.atguigu.yygh.hosp.repo.HospitalRepository;
import com.atguigu.yygh.hosp.repo.ScheduleRepository;
import com.atguigu.yygh.hosp.service.DeptService;
import com.atguigu.yygh.hosp.service.HospService;
import com.atguigu.yygh.hosp.service.ScheService;
import com.atguigu.yygh.model.hosp.BookingRule;
import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.model.hosp.Schedule;
import com.atguigu.yygh.vo.hosp.BookingScheduleRuleVo;
import com.atguigu.yygh.vo.hosp.ScheduleOrderVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.awt.print.Book;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ScheServiceImpl implements ScheService {
    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private HospService hospService;

    @Autowired
    private DeptService deptService;

    @Autowired
    private HospitalRepository hospitalRepository;

    /**
     * 第一个参数:Aggregation封装聚合条件
     * 第二个参数InputType封装输入类型,根据pojo类找到mongo中的指定聚合
     * 第三个参数:封装输出类型,把聚合后的数据封装到那哪个pojo类对象
     * @param pageNum
     * @param pageSize
     * @param hoscode
     * @param depcode
     * @return
     */
    @Override
    public Map<String, Object> findSchedule(Integer pageNum, Integer pageSize, String hoscode, String depcode) {

        Criteria criteria = Criteria.where("hoscode").is(hoscode).and("depcode").is(depcode);
        Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
                Aggregation.group("workDate").first("workDate").as("workDate")
                        .sum("reservedNumber").as("reservedNumber")
                        .sum("availableNumber").as("availableNumber"),
                Aggregation.sort(Sort.Direction.ASC,"workDate"),
                Aggregation.skip((pageNum - 1) * pageSize),
                Aggregation.limit(pageSize));

        AggregationResults<BookingScheduleRuleVo> aggregate
                = mongoTemplate.aggregate(aggregation, Schedule.class, BookingScheduleRuleVo.class);
        List<BookingScheduleRuleVo> list = aggregate.getMappedResults();

        Aggregation aggregation2 = Aggregation.newAggregation(Aggregation.match(criteria),
                Aggregation.group("workDate").first("workDate").as("workDate"));
        AggregationResults<BookingScheduleRuleVo> aggregate2
                = mongoTemplate.aggregate(aggregation2, Schedule.class, BookingScheduleRuleVo.class);
        int total = aggregate2.getMappedResults().size();

        for (BookingScheduleRuleVo bookingScheduleRuleVo : list) {
            Date workDate = bookingScheduleRuleVo.getWorkDate();
            String dayOfWeek = DataTimeConvert.getDayOfWeek(new DateTime(workDate));
            bookingScheduleRuleVo.setDayOfWeek(dayOfWeek);
        }
        //封装返回map
        HashMap<String, Object> map = new HashMap<>();
        map.put("list",list);
        map.put("total",total);
        //获取医院名称
        String name = hospService.getNameByHoscode(hoscode);
        Map<String, String> baseMap = new HashMap<>();
        baseMap.put("hosname",name);
        map.put("baseMap",baseMap);
        return map;
    }

    @Override
    public List<Schedule> findScheduleList(String hoscode, String depcode, String workDate) {
        //根据参数查询mongodb
        List<Schedule> scheduleList =
                scheduleRepository.findScheduleByHoscodeAndDepcodeAndWorkDate(hoscode,depcode,new DateTime(workDate).toDate());
        //把得到list集合遍历，向设置其他值：医院名称、科室名称、日期对应星期
        scheduleList.stream().forEach(item->{
            this.packageSchedule(item);
        });
        return scheduleList;
    }

    @Override
    public List<Schedule> getSchedule(String hoscode, String depcode) {
        Schedule schedule = new Schedule();
        schedule.setHoscode(hoscode);
        schedule.setDepcode(depcode);
        Example<Schedule> example = Example.of(schedule);
        List<Schedule> list = scheduleRepository.findAll(example);
        return list;
    }

    @Override
    public Map<String, Object> findPage(Integer pageNum, Integer pageSize, String hoscode, String depcode) {
        Hospital hospital = hospService.findByHoscode(hoscode);
        if(hospital == null) {
            throw new YYGHException(20001,"该医院不存在");
        }
        BookingRule bookingRule = hospital.getBookingRule();

        Page<Date> page = getCurrentDatePage(pageNum,pageSize,bookingRule);
        List<Date> dateList = page.getRecords();
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("hoscode").is(hoscode).and("depcode")
                        .is(depcode).and("workDate").in(dateList)),
                Aggregation.group("workDate").first("workDate").as("workDate")
                        .sum("availableNumber").as("availableNumber"),
                Aggregation.sort(Sort.by(Sort.Order.asc("workDate")))
        );
        AggregationResults<BookingScheduleRuleVo> aggregate
                = mongoTemplate.aggregate(aggregation, Schedule.class, BookingScheduleRuleVo.class);
        List<BookingScheduleRuleVo> mappedResults = aggregate.getMappedResults();
        Map<Date, BookingScheduleRuleVo> map = mappedResults.stream().collect(Collectors
                .toMap(BookingScheduleRuleVo::getWorkDate, BookingScheduleRuleVo -> BookingScheduleRuleVo));

        //返回的列表
        List<BookingScheduleRuleVo> list = new ArrayList<>();

        for (int i = 0;i < dateList.size();i++) {
            Date date = dateList.get(i);
            BookingScheduleRuleVo bookingScheduleRuleVo = map.get(date);
            if (bookingScheduleRuleVo == null) {
                bookingScheduleRuleVo = new BookingScheduleRuleVo();
                bookingScheduleRuleVo.setWorkDate(date);
                bookingScheduleRuleVo.setAvailableNumber(-1);//结合前端
            }
            //设置周几
            String dayOfWeek = DataTimeConvert.getDayOfWeek(new DateTime(date));
            bookingScheduleRuleVo.setDayOfWeek(dayOfWeek);
            bookingScheduleRuleVo.setStatus(0);

            //第一页第一条和最后一条进行判断
            //第一页第一条时间超过则显示停止挂号
            if (pageNum == 1 && i == 0){
                DateTime dateTime = concatDateAndString(new Date(), bookingRule.getStopTime());
                if (dateTime.isBeforeNow()) {
                    bookingScheduleRuleVo.setStatus(-1);
                }
            }
            //最后一页最后一条显示即将放号
            if (pageNum == page.getPages() && i == (dateList.size() -1)) {
                bookingScheduleRuleVo.setStatus(1);
            }
            list.add(bookingScheduleRuleVo);
        }

        //返回map
        Map<String, Object> result = new HashMap<>();
        //科室名字,医院名字


        result.put("total",page.getTotal());
        result.put("list",list);

        //其他基础数据
        Map<String, String> baseMap = new HashMap<>();
        //医院名称
        baseMap.put("hosname", hospService.getNameByHoscode(hoscode));
        //科室
        Department department =deptService.getDeptByHoscodeAndDepcode(hoscode, depcode);
        //大科室名称
        baseMap.put("bigname", department.getBigname());
        //科室名称
        baseMap.put("depname", department.getDepname());
        //月
        baseMap.put("workDateString", new DateTime().toString("yyyy年MM月"));
        //放号时间
        baseMap.put("releaseTime", bookingRule.getReleaseTime());
        //停号时间
        baseMap.put("stopTime", bookingRule.getStopTime());
        result.put("baseMap",baseMap);
        return result;
    }

    @Override
    public Schedule getById(String id) {
        Schedule schedule = scheduleRepository.findById(id).get();
        schedule.getParam().put("hosname",hospService.getNameByHoscode(schedule.getHoscode()));
        schedule.getParam().put("depname",deptService.getDeptByHoscodeAndDepcode(schedule.getHoscode(), schedule.getDepcode()).getDepname());
        schedule.getParam().put("dayOfWeek",DataTimeConvert.getDayOfWeek(new DateTime(schedule.getWorkDate())));
        return schedule;
    }

    @Override
    public ScheduleOrderVo getScheduleOrderVo(String scheduleId) {
        ScheduleOrderVo scheduleOrderVo = new ScheduleOrderVo();
        Schedule schedule = scheduleRepository.findById(scheduleId).get();
        if (schedule == null) {
            throw new YYGHException(20001,"没有这条排班");
        }
        Hospital hospital = hospService.findByHoscode(schedule.getHoscode());
        if (hospital == null) {
            throw new YYGHException(20001,"没有此医院");
        }

        BookingRule bookingRule = hospital.getBookingRule();
        if(null == bookingRule) {
            throw new YYGHException(20001,"没有此预约");
        }

        scheduleOrderVo.setHoscode(schedule.getHoscode());
        scheduleOrderVo.setHosname(hospital.getHosname());
        scheduleOrderVo.setDepcode(schedule.getDepcode());
        scheduleOrderVo.setDepname(deptService.getDeptByHoscodeAndDepcode(schedule.getHoscode(),
                schedule.getDepcode()).getDepname());
        scheduleOrderVo.setHosScheduleId(schedule.getHosScheduleId());
        scheduleOrderVo.setAvailableNumber(schedule.getAvailableNumber());
        scheduleOrderVo.setTitle(schedule.getTitle());
        scheduleOrderVo.setReserveDate(schedule.getWorkDate());
        scheduleOrderVo.setReserveTime(schedule.getWorkTime());
        scheduleOrderVo.setAmount(schedule.getAmount());

        //退号截止天数（如：就诊前一天为-1，当天为0）
        int quitDay = bookingRule.getQuitDay();
        DateTime quitTime = concatDateAndString(new DateTime(schedule.getWorkDate()).plusDays(quitDay).toDate(),
                bookingRule.getQuitTime());
        scheduleOrderVo.setQuitTime(quitTime.toDate());
        //预约开始时间
        DateTime startTime = concatDateAndString(new Date(), bookingRule.getReleaseTime());
        scheduleOrderVo.setStartTime(startTime.toDate());
        //预约截止时间
        DateTime endTime = concatDateAndString(new DateTime().plusDays(bookingRule.getCycle()).toDate(),
                bookingRule.getStopTime());
        scheduleOrderVo.setEndTime(endTime.toDate());
        //当天停止挂号时间
        DateTime stopTime = concatDateAndString(schedule.getWorkDate(), bookingRule.getStopTime());
        scheduleOrderVo.setStopTime(stopTime.toDate());
        return scheduleOrderVo;
    }

    @Override
    public void updateAvailableNumber(String scheduleId, Integer availableNumber) {
        Schedule schedule = scheduleRepository.findById(scheduleId).get();
        schedule.setAvailableNumber(availableNumber);
        scheduleRepository.save(schedule);
    }

    @Override
    public void cancelAvailableNumber(String scheduleId) {
        String[] split = scheduleId.split("-");
        Schedule schedule = scheduleRepository.findByHoscodeAndDepcodeAndHosScheduleId(split[0], split[1], split[2]);
        schedule.setAvailableNumber(schedule.getAvailableNumber() + 1);
        scheduleRepository.save(schedule);
    }

    private Page<Date> getCurrentDatePage(Integer pageNum, Integer pageSize, BookingRule bookingRule) {
        Integer cycle = bookingRule.getCycle();
        String releaseTime = bookingRule.getReleaseTime();
        DateTime dateTime = concatDateAndString(new Date(),releaseTime);
        if (dateTime.isBeforeNow()){
            cycle=cycle+1;
        }
        ArrayList<Date> dateList = new ArrayList<>();
        for (Integer i = 0; i < cycle; i++) {
            DateTime dateTime1 = new DateTime().plusDays(i);
            Date date = new DateTime(dateTime1.toString("yyyy-MM-dd")).toDate();
            dateList.add(date);
        }
        Page<Date> page = new Page<>(pageNum,pageSize,dateList.size());

        int start = (pageNum - 1) * pageSize;
        int end = (pageNum - 1) * pageSize + pageSize;

        if (end>dateList.size()){
            end = dateList.size();
        }

        ArrayList<Date> currentDatePageList = new ArrayList<>();
        for (int j = start;j < end; j++){
            Date date = dateList.get(j);
            currentDatePageList.add(date);
        }
        page.setRecords(currentDatePageList);
        return page;
    }

    //时间转换
    private DateTime concatDateAndString(Date date, String releaseTime) {
        String dateTimeString = new DateTime(date).toString("yyyy-MM-dd") + " "+ releaseTime;
        DateTime dateTime = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm").parseDateTime(dateTimeString);
        return dateTime;
    }

    //封装排班详情其他值 医院名称、科室名称、日期对应星期
    private void packageSchedule(Schedule schedule) {
        //设置医院名称
        schedule.getParam().put("hosname",hospService.getNameByHoscode(schedule.getHoscode()));
        //设置科室名称
        schedule.getParam().put("depname",
                deptService.getDepName(schedule.getHoscode(),schedule.getDepcode()));
        //设置日期对应星期
        schedule.getParam().put("dayOfWeek",DataTimeConvert.getDayOfWeek(new DateTime(schedule.getWorkDate())));
    }





}
