package com.atguigu.yygh.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.common.exception.YYGHException;
import com.atguigu.yygh.enums.OrderStatusEnum;
import com.atguigu.yygh.enums.PaymentStatusEnum;
import com.atguigu.yygh.hosp.client.HospFeignClient;
import com.atguigu.yygh.model.order.OrderInfo;
import com.atguigu.yygh.model.order.PaymentInfo;
import com.atguigu.yygh.model.user.Patient;
import com.atguigu.yygh.order.mapper.OrderInfoMapper;
import com.atguigu.yygh.order.service.OrderInfoService;
import com.atguigu.yygh.order.service.PaymentInfoService;
import com.atguigu.yygh.order.service.WeiPayService;
import com.atguigu.yygh.order.utils.HttpRequestHelper;
import com.atguigu.yygh.prop.MqConst;
import com.atguigu.yygh.rabbitmq.RabbitService;
import com.atguigu.yygh.user.client.PatientFeignClient;
import com.atguigu.yygh.vo.hosp.ScheduleOrderVo;
import com.atguigu.yygh.vo.msm.MsmVo;
import com.atguigu.yygh.vo.order.OrderCountQueryVo;
import com.atguigu.yygh.vo.order.OrderCountVo;
import com.atguigu.yygh.vo.order.OrderMqVo;
import com.atguigu.yygh.vo.order.OrderQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 订单表 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2022-11-16
 */
@Service
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements OrderInfoService {

    @Autowired
    private PatientFeignClient patientFeignClient;

    @Autowired
    private HospFeignClient hospFeignClient;

    @Autowired
    private RabbitService rabbitService;

    @Autowired
    private WeiPayService weiPayService;

    @Autowired
    private PaymentInfoService paymentInfoService;


    @Override
    public Long saveOrder(String scheduleId, Long patientId) {
        //就诊人
        Patient patient = patientFeignClient.getById(patientId);
        //排班信息
        ScheduleOrderVo scheduleOrderVo = hospFeignClient.getScheduleOrderVo(scheduleId);
        Date date = scheduleOrderVo.getStopTime();
        DateTime dateTime = new DateTime(date);
        if (dateTime.isBeforeNow()){
            throw new YYGHException(20001,"超过了当前预约挂号的时间");
        }

        //3 平台里面 ==> 调用医院订单确认接口，
        // 3.1 如果医院返回失败，挂号失败
        //使用map集合封装需要传过医院数据
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("hoscode",scheduleOrderVo.getHoscode());
        paramMap.put("depcode",scheduleOrderVo.getDepcode());
        paramMap.put("hosScheduleId",scheduleOrderVo.getHosScheduleId());
        paramMap.put("reserveDate",new DateTime(scheduleOrderVo.getReserveDate()).toString("yyyy-MM-dd"));
        paramMap.put("reserveTime", scheduleOrderVo.getReserveTime());
        paramMap.put("amount",scheduleOrderVo.getAmount()); //挂号费用
        paramMap.put("name", patient.getName());
        paramMap.put("certificatesType",patient.getCertificatesType());
        paramMap.put("certificatesNo", patient.getCertificatesNo());
        paramMap.put("sex",patient.getSex());
        paramMap.put("birthdate", patient.getBirthdate());
        paramMap.put("phone",patient.getPhone());
        paramMap.put("isMarry", patient.getIsMarry());
        paramMap.put("provinceCode",patient.getProvinceCode());
        paramMap.put("cityCode", patient.getCityCode());
        paramMap.put("districtCode",patient.getDistrictCode());
        paramMap.put("address",patient.getAddress());
        //联系人
        paramMap.put("contactsName",patient.getContactsName());
        paramMap.put("contactsCertificatesType", patient.getContactsCertificatesType());
        paramMap.put("contactsCertificatesNo",patient.getContactsCertificatesNo());
        paramMap.put("contactsPhone",patient.getContactsPhone());
        paramMap.put("timestamp", HttpRequestHelper.getTimestamp());
        //String sign = HttpRequestHelper.getSign(paramMap, signInfoVo.getSignKey());
        paramMap.put("sign", "");

        //使用httpclient发送请求，请求医院接口
        JSONObject result =
                HttpRequestHelper.sendRequest(paramMap, "http://localhost:9998/order/submitOrder");
        //根据医院接口返回状态码判断  200 成功
        if(result.getInteger("code") == 200) { //挂号成功
            // 3.2 如果返回成功，得到返回其他数据
            JSONObject jsonObject = result.getJSONObject("data");
            //预约记录唯一标识（医院预约记录主键）
            String hosRecordId = jsonObject.getString("hosRecordId");
            //预约序号
            Integer number = jsonObject.getInteger("number");;
            //取号时间
            String fetchTime = jsonObject.getString("fetchTime");;
            //取号地址
            String fetchAddress = jsonObject.getString("fetchAddress");;

            //4 如果医院接口返回成功，添加上面三部分数据到数据库
            OrderInfo orderInfo = new OrderInfo();
            //设置添加数据--排班数据
            BeanUtils.copyProperties(scheduleOrderVo, orderInfo);
            //设置添加数据--就诊人数据
            //订单号
            String outTradeNo = System.currentTimeMillis() + ""+ new Random().nextInt(100);
            orderInfo.setOutTradeNo(outTradeNo);
            orderInfo.setScheduleId(scheduleOrderVo.getHosScheduleId());
            orderInfo.setUserId(patient.getUserId());
            orderInfo.setPatientId(patientId);
            orderInfo.setPatientName(patient.getName());
            orderInfo.setPatientPhone(patient.getPhone());
            orderInfo.setOrderStatus(OrderStatusEnum.UNPAID.getStatus());

            //设置添加数据--医院接口返回数据
            orderInfo.setHosRecordId(hosRecordId);
            orderInfo.setNumber(number);
            orderInfo.setFetchTime(fetchTime);
            orderInfo.setFetchAddress(fetchAddress);

            //调用方法添加
            baseMapper.insert(orderInfo);

            //5.根据医院返回数据，更新排班数量
            //排班可预约数
            Integer reservedNumber = jsonObject.getInteger("reservedNumber");
            //排班剩余预约数
            Integer availableNumber = jsonObject.getInteger("availableNumber");

            OrderMqVo orderMqVo = new OrderMqVo();
            orderMqVo.setAvailableNumber(availableNumber);
            orderMqVo.setScheduleId(scheduleId);
            MsmVo msmVo = new MsmVo();
            msmVo.setPhone(patient.getPhone());
            //短信模板code
            //msmVo.setTemplateCode();
            orderMqVo.setMsmVo(msmVo);
            rabbitService.sendMessage(MqConst.EXCHANGE_ORDER,MqConst.ROUTING_KEY,orderMqVo);


            //TODO 6 给就诊人发送短信

            //7 返回订单号
            return orderInfo.getId();
        } else { //挂号失败
            throw new YYGHException(20001,"挂号失败");
        }
    }

    @Override
    public IPage<OrderInfo> selectPage(Page<OrderInfo> pageParam, OrderQueryVo orderQueryVo) {
        //orderQueryVo获取条件值
        String name = orderQueryVo.getKeyword(); //医院名称
        Long patientId = orderQueryVo.getPatientId(); //就诊人名称
        String orderStatus = orderQueryVo.getOrderStatus(); //订单状态
        String reserveDate = orderQueryVo.getReserveDate();//安排时间
        String createTimeBegin = orderQueryVo.getCreateTimeBegin();
        String createTimeEnd = orderQueryVo.getCreateTimeEnd();
        //对条件值进行非空判断
        QueryWrapper<OrderInfo> wrapper = new QueryWrapper<>();
        //查询当前用户下的就诊人
        Long userId = orderQueryVo.getUserId();
        if (!StringUtils.isEmpty(userId)){
            wrapper.eq("user_id",userId);
        }
        if(!StringUtils.isEmpty(name)) {
            wrapper.like("hosname",name);
        }
        if(!StringUtils.isEmpty(patientId)) {
            wrapper.eq("patient_id",patientId);
        }
        if(!StringUtils.isEmpty(orderStatus)) {
            wrapper.eq("order_status",orderStatus);
        }
        if(!StringUtils.isEmpty(reserveDate)) {
            wrapper.ge("reserve_date",reserveDate);
        }
        if(!StringUtils.isEmpty(createTimeBegin)) {
            wrapper.ge("create_time",createTimeBegin);
        }
        if(!StringUtils.isEmpty(createTimeEnd)) {
            wrapper.le("create_time",createTimeEnd);
        }
        Page<OrderInfo> orderInfoPage = baseMapper.selectPage(pageParam, wrapper);
        for (OrderInfo orderInfo : orderInfoPage.getRecords()) {
            //状改成文字显示,而不是显示数字
            orderInfo.getParam().put("orderStatusString",
                    OrderStatusEnum.getStatusNameByStatus(orderInfo.getOrderStatus()));
        }
        return orderInfoPage;
    }

    @Override
    public OrderInfo getOrderInfo(Long orderId,Long userId) {
        QueryWrapper<OrderInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);
        queryWrapper.eq("id",orderId);
        OrderInfo orderInfo = baseMapper.selectOne(queryWrapper);
        orderInfo.getParam().put("orderStatusString",
                OrderStatusEnum.getStatusNameByStatus(orderInfo.getOrderStatus()));
        return orderInfo;
    }

    @Override
    public void cancelOrder(Long orderId) {
        //1.判断当前时间是否在订单规定的退号截止时间之前
        OrderInfo orderInfo = baseMapper.selectById(orderId);
        Date date = orderInfo.getQuitTime();
        DateTime quitTime = new DateTime(date);
        if (quitTime.isBeforeNow()) {
            //1.1是抛出异常
            throw new YYGHException(20001,"超过退号截止时间");
        }
        //1.2是...往下执行
        //2.通知第三方医院,请求第三方医院
        Map<String, Object> map = new HashMap<>();
        map.put("hoscode",orderInfo.getHoscode());
        map.put("hosRecordId",orderInfo.getHosRecordId());
        JSONObject jsonObject = HttpRequestHelper.sendRequest(map, "http://localhost:9998/order/updateCancelStatus");
        if (jsonObject == null || jsonObject.getInteger("code") != 200){
            //2.1第三方医院不允许取消,跑异常
            throw new YYGHException(20001,"取消失败");
        }
        //2.2允许取消,继续执行
        //3.判断订单的状态是否为已支付
        if (orderInfo.getOrderStatus().intValue() == OrderStatusEnum.PAID.getStatus().intValue()){
            //3.1已支付,先微信退款
            boolean flag = weiPayService.refund(orderId);
            if (!flag){
                throw new YYGHException(20001,"退款失败");
            }

        }
        //3.2未支付,继续执行
        //4.更新订单表的订单状态,
        orderInfo.setOrderStatus(OrderStatusEnum.CANCLE.getStatus());
        baseMapper.updateById(orderInfo);

        QueryWrapper<PaymentInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id",orderId);
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setPaymentStatus(PaymentStatusEnum.REFUND.getStatus());
        paymentInfoService.update(paymentInfo,queryWrapper);

        //5.更新平台上的剩余可预约数

        //发送mq信息更新预约数 我们与下单成功更新预约数使用相同的mq信息，不设置可预约数与剩余预约数，接收端可预约数减1即可
        OrderMqVo orderMqVo = new OrderMqVo();
        orderMqVo.setScheduleId(orderInfo.getHoscode() + "-" + orderInfo.getDepcode() + "-" + orderInfo.getScheduleId());
        MsmVo msmVo = new MsmVo();
        msmVo.setPhone(orderInfo.getPatientPhone());
        msmVo.setTemplateCode("短信模板");
        orderMqVo.setMsmVo(msmVo);
        rabbitService.sendMessage(MqConst.EXCHANGE_ORDER,MqConst.ROUTING_KEY,orderMqVo);

        //6.给就诊人发短信提示
    }

    @Override
    public void patientRemind() {
        QueryWrapper<OrderInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("reserve_date",new DateTime().toString("yyyy-MM-dd"));
        //这里应该加个条件，订单状态不为-1的订单，-1表示已退号
        queryWrapper.ne("order_status",OrderStatusEnum.CANCLE.getStatus());
        List<OrderInfo> orderInfoList = baseMapper.selectList(queryWrapper);
        for(OrderInfo orderInfo : orderInfoList) {
            //短信提示
            MsmVo msmVo = new MsmVo();
            msmVo.setPhone(orderInfo.getPatientPhone());
            String reserveDate = new DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd") + (orderInfo.getReserveTime()==0 ? "上午": "下午");
            Map<String,Object> param = new HashMap<String,Object>(){{
                put("title", orderInfo.getHosname()+"|"+orderInfo.getDepname()+"|"+orderInfo.getTitle());
                put("reserveDate", reserveDate);
                put("name", orderInfo.getPatientName());
            }};
            msmVo.setParam(param);
            rabbitService.sendMessage(MqConst.EXCHANGE_SMS, MqConst.ROUTING_SMS, msmVo);
        }
    }

    @Override
    public Map<String, Object> getCountMap(OrderCountQueryVo orderCountQueryVo) {
        Map<String, Object> map = new HashMap<>();
        List<OrderCountVo> orderCountVoList = baseMapper.selectOrderCount(orderCountQueryVo);

        //方法一
        /*List<String> dateList = new ArrayList<>();
        List<Integer> countList = new ArrayList<>();
        for (OrderCountVo orderCountVo : orderCountVoList) {
            String reserveDate = orderCountVo.getReserveDate();
            Integer count = orderCountVo.getCount();
            dateList.add(reserveDate);
            countList.add(count);
        }*/

        //方法二
        //日期列表
        List<String> dateList
                =orderCountVoList.stream().map(OrderCountVo::getReserveDate).collect(Collectors.toList());
        //统计列表
        List<Integer> countList
                =orderCountVoList.stream().map(OrderCountVo::getCount).collect(Collectors.toList());
        map.put("dateList", dateList);
        map.put("countList", countList);
        return map;
    }
}
