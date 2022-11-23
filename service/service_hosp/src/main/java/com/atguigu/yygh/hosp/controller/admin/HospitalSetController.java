package com.atguigu.yygh.hosp.controller.admin;

import com.atguigu.yygh.common.result.R;
import com.atguigu.yygh.hosp.service.HospitalSetService;
import com.atguigu.yygh.common.utils.MD5;
import com.atguigu.yygh.model.hosp.HospitalSet;
import com.atguigu.yygh.vo.hosp.HospitalSetQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Random;


//@CrossOrigin //跨域
@Slf4j
@Api(tags = "医院设置控制层")//医院设置接口
@RestController
@RequestMapping("/admin/hosp/hospitalSet")
public class HospitalSetController {

    @Autowired
    private HospitalSetService hospitalSetService;

    //查询所有医院设置
    @ApiOperation(value = "查询医院设置列表")
    @GetMapping("/findAll")
    public R finaAll(){
        List<HospitalSet> hospitalSetList = hospitalSetService.list();
        return R.ok().data("list",hospitalSetList);
    }


    /**
     * @ApiParam(name = "id",value = "医院设置id",required = true)
     * @ApiImplicitParam(name = "id",value = "医院设置id",required = true,dataType = "Long",paramType = "query")
     * @ApiImplicitParams(value = {
     *             @ApiImplicitParam(name = "id",value = "医院设置id",required = true,dataType = "Long",paramType = "query")
     *     })name,value,,required,dataType,paramType = query|header|path
     * @param id
     * @return
     */
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "id",value = "医院设置id",required = true,dataType = "Long",paramType = "query")
    })
    @ApiOperation(value = "根据医院设置id查询医院设置信息")
    @GetMapping("/info")
    public R getHospitalSetById(@RequestParam Long id){
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        return R.ok().data("item",hospitalSet);
    }


    @ApiOperation(value = "根据id删除医院设置信息")
    @DeleteMapping("{id}")
    public R deleteById(@PathVariable("id") Long id){
        hospitalSetService.removeById(id);
        return R.ok();
    }

    @ApiOperation("批量删除医院设置信息")
    @DeleteMapping("batchRemove")
    public R batchRemove(@RequestBody List<Long> ids){
        hospitalSetService.removeByIds(ids);
        return R.ok();
    }

    @ApiOperation(value = "条件查询医院设置信息分页")
    @PostMapping("/{page}/{limit}")
    public R findPage(@ApiParam(name = "page", value = "当前页码", required = true)
                      @PathVariable Long page,
                      @ApiParam(name = "limit", value = "每页记录数", required = true)
                      @PathVariable Long limit,
                      @ApiParam(name = "HospitalSetQueryVo",value = "封装的查询条件")
                      @RequestBody HospitalSetQueryVo hospitalSetQueryVo){
        Page<HospitalSet> hospitalSetPage = new Page<>(page, limit);
        QueryWrapper<HospitalSet> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(!StringUtils.isEmpty(hospitalSetQueryVo.getHosname()),"hosname",hospitalSetQueryVo.getHosname());
        queryWrapper.eq(!StringUtils.isEmpty(hospitalSetQueryVo.getHoscode()),"hoscode",hospitalSetQueryVo.getHoscode());
        hospitalSetService.page(hospitalSetPage,queryWrapper);
        List<HospitalSet> records = hospitalSetPage.getRecords();
        long total = hospitalSetPage.getTotal();
        return R.ok().data("total",total).data("records",records);
    }

    @ApiOperation("根据id查询医院设置")
    @GetMapping("/getHospSet/{id}")
    public R getById(@ApiParam(name = "id",value = "医院id设置",
            required = true)@PathVariable("id") String id, HttpServletRequest request){
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        //模拟异常
        /*try {
            log.debug(request.getRemoteHost()+ ":" + Thread.currentThread().getName());
            //int a = 1 / 0;
            log.debug("XXX");
        } catch (Exception e) {
            e.printStackTrace();
            throw new YYGHException(20001,"预约挂号异常");
        }*/
        return R.ok().data("item",hospitalSet);
    }

    @ApiOperation("新增医院设置")
    @PostMapping("/saveHospSet")
    public R save(@ApiParam(name = "hospitalSet",value = "医院设置信息",required = true)
                  @RequestBody HospitalSet hospitalSet){
        //设置状态,数据库默认0可用
        //hospitalSet.setStatus(1);
        //设置signkey
        Random random = new Random();
        hospitalSet.setSignKey(MD5.encrypt(System.currentTimeMillis() + random.nextInt(1000) + ""));
        hospitalSetService.save(hospitalSet);
        return R.ok();
    }

    @ApiOperation("根据id修改医院设置")
    @PutMapping("updateHospSet")
    public R update(@ApiParam(name = "hospitalSet",value = "修改医院设置信息",required = true)
                    @RequestBody HospitalSet hospitalSet){
        hospitalSetService.updateById(hospitalSet);
        return R.ok();
    }

    //status:1未锁定,0锁定
    @ApiOperation("锁定和解除锁定")
    @PutMapping("lockHospitalSet/{id}/{status}")
    public R lockHospitalSet(@PathVariable("id") Long id,@PathVariable("status") Integer status){
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        hospitalSet.setStatus(status);
        hospitalSetService.updateById(hospitalSet);
        return R.ok();
    }




}
