package com.atguigu.yygh.hosp.service.impl;


import com.atguigu.yygh.cmn.client.DictFeignClient;
import com.atguigu.yygh.enums.DictEnum;
import com.atguigu.yygh.hosp.repo.HospitalRepository;
import com.atguigu.yygh.hosp.service.HospService;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.vo.hosp.HospitalQueryVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class HospServiceImpl implements HospService {

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private DictFeignClient dictFeignClient;

    @Override
    public Page<Hospital> findPage(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo) {
        //排序条件
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        Pageable pageable = PageRequest.of(page - 1, limit,sort);
        //hospitalQueryVo转化成hospital
        Hospital hospital = new Hospital();
        BeanUtils.copyProperties(hospitalQueryVo,hospital);

        //模糊查询设置
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING) //改变默认字符串匹配方式：模糊查询
                .withIgnoreCase(true); //改变默认大小写忽略方式：忽略大小写
        Example<Hospital> example = Example.of(hospital,matcher);
        Page<Hospital> all = hospitalRepository.findAll(example, pageable);

        all.getContent().stream().forEach(item->{
            this.packageHospital(item);
        });
        return all;
    }

    @Override
    public void updateStatus(String id, Integer status) {
        Hospital hospital = hospitalRepository.findById(id).get();
        hospital.setStatus(status);
        hospitalRepository.save(hospital);
    }

    @Override
    public Map<String,Object> show(String id) {
        HashMap<String, Object> map = new HashMap<>();
        Hospital hospital = hospitalRepository.findById(id).get();

        hospital.setParam(castNameByCode(hospital));

        //医院基本信息（包含医院等级）
        map.put("hospital",hospital);
        //单独处理更直观
        map.put("bookingRule", hospital.getBookingRule());
        //不需要重复返回
        hospital.setBookingRule(null);

        return map;
    }

    @Override
    public String getNameByHoscode(String hoscode) {
        Hospital hospital = new Hospital();
        hospital.setHoscode(hoscode);
        Example<Hospital> example = Example.of(hospital);
        Hospital hospital1 = hospitalRepository.findOne(example).get();
        if (hospital1 != null) {
            String hosname = hospital1.getHosname();
            return hosname;
        }
        return "";
    }

    @Override
    public List<Hospital> getHospitalListByHosname(String hosname) {
        List<Hospital> list = hospitalRepository.findByHosnameLike(hosname);
        return list;
    }

    @Override
    public Map<String, Object> registered(String hoscode) {
        HashMap<String, Object> map = new HashMap<>();
        Hospital hospital = new Hospital();
        hospital.setHoscode(hoscode);
        Example<Hospital> example = Example.of(hospital);
        Hospital hospital1 = hospitalRepository.findOne(example).get();
        packageHospital(hospital1);
        //hospital1里面不放bookingRule信息,单独放在map里面
        map.put("bookingRule",hospital1.getBookingRule());
        hospital1.setBookingRule(null);
        map.put("hospital",hospital1);
        return map;
    }

    @Override
    public Hospital findByHoscode(String hoscode) {
        Hospital hospital = new Hospital();
        hospital.setHoscode(hoscode);
        Example<Hospital> example = Example.of(hospital);
        Hospital hospital1 = hospitalRepository.findOne(example).get();
        return hospital1;
    }

    public void packageHospital(Hospital hospital){
        String hostype = hospital.getHostype();
        String provinceCode = hospital.getProvinceCode();
        String cityCode = hospital.getCityCode();
        String districtCode = hospital.getDistrictCode();

        String provinceName = dictFeignClient.getNameByValue(Long.parseLong(provinceCode));
        String cityName = dictFeignClient.getNameByValue(Long.parseLong(cityCode));
        String districtName = dictFeignClient.getNameByValue(Long.parseLong(districtCode));
        String hostypeStr = dictFeignClient.getNameByValueAndCode(Long.parseLong(hostype), DictEnum.HOSTYPE.getDictCode());
        Map<String, Object> map = hospital.getParam();
        map.put("hostypeStr",hostypeStr);
        map.put("address",provinceName + cityName + districtName + hospital.getAddress());

    }

    public Map<String,Object> castNameByCode(Hospital hospital){
        String hostype = hospital.getHostype();
        String provinceCode = hospital.getProvinceCode();
        String cityCode = hospital.getCityCode();
        String districtCode = hospital.getDistrictCode();

        String provinceName = dictFeignClient.getNameByValue(Long.parseLong(provinceCode));
        String cityName = dictFeignClient.getNameByValue(Long.parseLong(cityCode));
        String districtName = dictFeignClient.getNameByValue(Long.parseLong(districtCode));
        String hostypeStr = dictFeignClient.getNameByValueAndCode(Long.parseLong(hostype), DictEnum.HOSTYPE.getDictCode());
        Map<String, Object> map = hospital.getParam();
        map.put("hostypeStr",hostypeStr);
        map.put("address",provinceName + cityName + districtName + hospital.getAddress());
        return map;
    }


}
