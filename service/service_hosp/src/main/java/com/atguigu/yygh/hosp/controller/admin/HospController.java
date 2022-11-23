package com.atguigu.yygh.hosp.controller.admin;

import com.atguigu.yygh.common.result.R;
import com.atguigu.yygh.hosp.service.HospService;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.vo.hosp.HospitalQueryVo;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
//@CrossOrigin //跨域
@RequestMapping("/admin/hosp/hospital")
public class HospController {

    @Autowired
    private HospService hospService;

    @PostMapping("/{page}/{limit}")
    public R findPage(@PathVariable("page") Integer page,
                      @PathVariable("limit") Integer limit,
                      @RequestBody HospitalQueryVo hospitalQueryVo){
        Page<Hospital> pages = hospService.findPage(page,limit,hospitalQueryVo);
        return R.ok().data("total",pages.getTotalElements()).data("item",pages.getContent());
    }

    @ApiOperation(value = "更新上线状态")
    @GetMapping("/updateStatus/{id}/{status}")
    public R lock(
            @ApiParam(name = "id", value = "医院id", required = true)
            @PathVariable("id") String id,
            @ApiParam(name = "status", value = "状态（0：未上线 1：已上线）", required = true)
            @PathVariable("status") Integer status){
        hospService.updateStatus(id, status);
        return R.ok();
    }

    @ApiOperation(value = "获取医院详情")
    @GetMapping("show/{id}")
    public R show(
            @ApiParam(name = "id", value = "医院id", required = true)
            @PathVariable String id) {
        Map<String,Object> map = hospService.show(id);
        return R.ok().data("hospital",map);
    }


}
