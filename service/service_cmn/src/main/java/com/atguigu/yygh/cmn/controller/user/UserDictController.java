package com.atguigu.yygh.cmn.controller.user;

import com.atguigu.yygh.cmn.service.DictService;
import com.atguigu.yygh.common.result.R;
import com.atguigu.yygh.model.cmn.Dict;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user/dict")
public class UserDictController {

    @Autowired
    private DictService dictService;

    @GetMapping("/{dictCode}")
    public R getDictListByDictCode(@PathVariable String dictCode){
        List<Dict> dictList = dictService.getChildListByParentDictCode(dictCode);
        return R.ok().data("list",dictList);
    }
    
    @ApiOperation("根据id查询子数据列表")
    @GetMapping("findChildData/{id}")
    public R findChildData(@PathVariable Long id){
        List<Dict> dictList =  dictService.findChildData(id);
        return R.ok().data("dictList",dictList);
    }
}
