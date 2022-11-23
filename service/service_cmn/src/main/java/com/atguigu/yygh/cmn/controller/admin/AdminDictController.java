package com.atguigu.yygh.cmn.controller.admin;


import com.atguigu.yygh.cmn.service.DictService;
import com.atguigu.yygh.common.result.R;
import com.atguigu.yygh.model.cmn.Dict;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * <p>
 * 组织架构表 前端控制器
 * </p>
 *
 * @author atguigu
 * @since 2022-11-03
 */
@Slf4j
//@CrossOrigin //跨域
@Api(tags = "数据字典")
@RestController
@RequestMapping("/admin/cmn/dict")
public class AdminDictController {

    @Autowired
    private DictService dictService;

    @ApiOperation("根据id查询子数据列表")
    @GetMapping("findChildData/{id}")
    public R findChildData(@PathVariable Long id){
        List<Dict> dictList =  dictService.findChildData(id);
        return R.ok().data("dictList",dictList);
    }

    @ApiOperation("数据字典下载")
    @GetMapping("/download")
    public void download(HttpServletResponse response) throws UnsupportedEncodingException {

        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String fileName = URLEncoder.encode("数据字典", "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename="+ fileName + ".xlsx");

        dictService.download(response);
    }

    @ApiOperation("数据字典上传")
    @PostMapping("/upload")
    public R upload(MultipartFile file) throws IOException {
        dictService.upload(file);
        return R.ok();
    }

    @ApiOperation("根据value查询省市区名字")
    @GetMapping("/name/{value}")
    public String getNameByValue(@PathVariable("value") Long value){
        return dictService.getNameByValue(value);
    }

    @ApiOperation("根据value和dictCode查询医院等级名字")
    @GetMapping("/name/{value}/{dictCode}")
    public String getNameByValueAndCode(@PathVariable("value") Long value,@PathVariable("dictCode") String dictCode){
        return dictService.getNameByValueAndCode(value,dictCode);
    }

    @GetMapping("/childlist/{dictCode}")
    public R getChildListByParentDictCode(@PathVariable("dictCode") String dictCode){
        List<Dict> dictList = dictService.getChildListByParentDictCode(dictCode);
        return R.ok().data("dictList",dictList);
    }

    @GetMapping("/citylist/{parentId}")
    public R getCityListByParentId(@PathVariable("parentId") Long parentId){
        List<Dict> cityList = dictService.getCityListByParentId(parentId);
        return R.ok().data("cityList",cityList);
    }


}

