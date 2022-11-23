package com.atguigu.yygh.user.service.impl;


import com.atguigu.yygh.cmn.client.DictFeignClient;
import com.atguigu.yygh.common.exception.YYGHException;
import com.atguigu.yygh.common.utils.JwtHelper;
import com.atguigu.yygh.enums.AuthStatusEnum;
import com.atguigu.yygh.model.cmn.Dict;
import com.atguigu.yygh.model.user.Patient;
import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.user.mapper.UserInfoMapper;
import com.atguigu.yygh.user.service.PatientService;
import com.atguigu.yygh.user.service.UserInfoService;
import com.atguigu.yygh.vo.user.LoginVo;
import com.atguigu.yygh.vo.user.UserAuthVo;
import com.atguigu.yygh.vo.user.UserInfoQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sun.org.apache.bcel.internal.generic.LMUL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2022-11-12
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Autowired
    private PatientService patientService;

    @Autowired
    private DictFeignClient dictFeignClient;

    @Override
    public Map<String, Object> login(LoginVo loginVo) {
        String phone = loginVo.getPhone();
        String code = loginVo.getCode();
        if (StringUtils.isEmpty(code)) {
            throw new YYGHException(20001,"验证码为空");
        }
        //进行验证码判断
        String redisCode = redisTemplate.opsForValue().get(phone);
        if (StringUtils.isEmpty(redisCode) || !code.equals(redisCode)) {
            throw new YYGHException(20001,"验证码不正确");
        }

        UserInfo userInfo = null;

        if (StringUtils.isEmpty(loginVo.getOpenid())) {
            //直接手机号登陆
            QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
            wrapper.eq(!StringUtils.isEmpty(phone),"phone",phone);
            userInfo = baseMapper.selectOne(wrapper);
            if (userInfo == null) {
                userInfo = new UserInfo();
                userInfo.setPhone(phone);
                userInfo.setCreateTime(new Date());
                userInfo.setStatus(1);
                baseMapper.insert(userInfo);
            }
        }else {
            //微信首次扫码,在手机号登陆
            QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
            wrapper.eq("openid",loginVo.getOpenid());
            userInfo = baseMapper.selectOne(wrapper);

            QueryWrapper<UserInfo> wrapper2 = new QueryWrapper<>();
            wrapper2.eq("phone",loginVo.getPhone());
            UserInfo userInfo2 = baseMapper.selectOne(wrapper2);
            if (userInfo2 == null) {
                userInfo.setPhone(phone);
                baseMapper.updateById(userInfo);
            }else {
                //先手机号登陆,再用微信登陆
                //两条合一条,删除微信登陆产生的那一条数据,合并到手机号那条记录上
                userInfo2.setOpenid(userInfo.getOpenid());
                userInfo2.setNickName(userInfo.getNickName());
                baseMapper.updateById(userInfo2);
                //删除多余的一条
                baseMapper.deleteById(userInfo.getId());
                userInfo = userInfo2;
            }

        }

        //校验是否被禁用
        if(userInfo.getStatus() == 0) {
            throw new YYGHException(20001,"用户已经禁用");
        }

        //返回页面显示名称
        Map<String, Object> map = new HashMap<>();
        String name = userInfo.getName();
        if(StringUtils.isEmpty(name)) {
            name = userInfo.getNickName();
        }
        if(StringUtils.isEmpty(name)) {
            name = userInfo.getPhone();
        }
        map.put("name", name);
        map.put("token", JwtHelper.createToken(userInfo.getId(),name));
        return map;
    }

    @Override
    public UserInfo getUserByOpenId(String openid) {
        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
        wrapper.eq(!StringUtils.isEmpty(openid),"openid",openid);
        UserInfo userInfo = baseMapper.selectOne(wrapper);
        return userInfo;
    }

    @Override
    public UserInfo getUserInfo(Long userId) {
        UserInfo userInfo = baseMapper.selectById(userId);
        Integer authStatus = userInfo.getAuthStatus();
        String status = AuthStatusEnum.getStatusNameByStatus(authStatus);
        userInfo.getParam().put("authStatusString",status);
        return userInfo;
    }

    @Override
    public void auth(UserAuthVo userAuthVo, Long userId) {
        UserInfo userInfo = baseMapper.selectById(userId);
        userInfo.setName(userAuthVo.getName());
        userInfo.setCertificatesType(userAuthVo.getCertificatesType());
        userInfo.setCertificatesNo(userAuthVo.getCertificatesNo());
        userInfo.setCertificatesUrl(userAuthVo.getCertificatesUrl());
        userInfo.setAuthStatus(AuthStatusEnum.AUTH_RUN.getStatus());
        baseMapper.updateById(userInfo);
    }

    @Override
    public IPage<UserInfo> findPage(UserInfoQueryVo userInfoQueryVo, Integer pageNum, Integer pageSize) {
        Page<UserInfo> page = new Page<>(pageNum,pageSize);
        //UserInfoQueryVo获取条件值
        String name = userInfoQueryVo.getKeyword(); //用户名称
        Integer status = userInfoQueryVo.getStatus();//用户状态
        Integer authStatus = userInfoQueryVo.getAuthStatus(); //认证状态
        String createTimeBegin = userInfoQueryVo.getCreateTimeBegin(); //开始时间
        String createTimeEnd = userInfoQueryVo.getCreateTimeEnd(); //结束时间
        //对条件值进行非空判断
        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();

        if(!StringUtils.isEmpty(name)) {
            //(UserInfo::getName,name).or().eq(UserInfo::getPhone),name)
            wrapper.and(qr->qr.like(UserInfo::getName,name).or().eq(UserInfo::getPhone,name));
            //wrapper.like(UserInfo::getName,name).or().eq(UserInfo::getPhone,name);
        }
        if(!StringUtils.isEmpty(status)) {
            wrapper.eq(UserInfo::getAuthStatus,status);
        }
        if(!StringUtils.isEmpty(authStatus)) {
            wrapper.eq(UserInfo::getAuthStatus,authStatus);
        }
        if(!StringUtils.isEmpty(createTimeBegin)) {
            wrapper.ge(UserInfo::getCreateTime,createTimeBegin);
        }
        if(!StringUtils.isEmpty(createTimeEnd)) {
            wrapper.le(UserInfo::getCreateTime,createTimeEnd);
        }
        //调用mapper的方法
        IPage<UserInfo> pages = baseMapper.selectPage(page, wrapper);
        //编号变成对应值封装
        pages.getRecords().stream().forEach(item -> {
            this.packageUserInfo(item);
        });
        return pages;
    }

    /**
     * 用户锁定
     * @param userId
     * @param status 0：锁定 1：正常
     */
    @Override
    public void lock(Long userId, Integer status) {
        if(status.intValue() == 0 || status.intValue() == 1) {
            UserInfo userInfo = this.getById(userId);
            userInfo.setStatus(status);
            this.updateById(userInfo);
        }
    }

    @Override
    public Map<String, Object> show(Long userId) {
        Map<String,Object> map = new HashMap<>();
        //根据userid查询用户信息
        UserInfo userInfo = this.packageUserInfo(baseMapper.selectById(userId));
        map.put("userInfo",userInfo);
        //根据userid查询就诊人信息
        List<Patient> patientList = patientService.findAll(userId);
        for (Patient patient : patientList) {
            String provinceString = dictFeignClient.getNameByValue(Long.parseLong(patient.getProvinceCode()));
            String cityString = dictFeignClient.getNameByValue(Long.parseLong(patient.getCityCode()));
            String districtString = dictFeignClient.getNameByValue(Long.parseLong(patient.getDistrictCode()));
            String address = patient.getAddress();
            StringBuilder fullAddress = new StringBuilder().append(provinceString).append(cityString)
                    .append(districtString).append(address);
            patient.getParam().put("fullAddress",fullAddress.toString());
        }
        map.put("patientList",patientList);
        return map;
    }

    /**
     * 认证审批
     * @param userId
     * @param authStatus 2：通过 -1：不通过
     */
    @Override
    public void approval(Long userId, Integer authStatus) {
        if(authStatus.intValue()==2 || authStatus.intValue()==-1) {
            UserInfo userInfo = baseMapper.selectById(userId);
            userInfo.setAuthStatus(authStatus);
            baseMapper.updateById(userInfo);
        }
    }

    //编号变成对应值封装
    private UserInfo packageUserInfo(UserInfo userInfo) {
        //处理认证状态编码
        userInfo.getParam().put("authStatusString",AuthStatusEnum.getStatusNameByStatus(userInfo.getAuthStatus()));
        //处理用户状态 0  1
        String statusString = userInfo.getStatus().intValue()==0 ?"锁定" : "正常";
        userInfo.getParam().put("statusString",statusString);
        return userInfo;
    }

}
