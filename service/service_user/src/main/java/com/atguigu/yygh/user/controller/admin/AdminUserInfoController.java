package com.atguigu.yygh.user.controller.admin;

import com.atguigu.yygh.common.result.R;
import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.user.service.UserInfoService;
import com.atguigu.yygh.vo.user.UserInfoQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/userInfo")
public class AdminUserInfoController {

    @Autowired
    private UserInfoService userInfoService;

    @PostMapping("/{pageNum}/{pageSize}")
    public R findPage(@RequestBody UserInfoQueryVo userInfoQueryVo,
                      @PathVariable Integer pageNum,
                      @PathVariable Integer pageSize){
        IPage<UserInfo> page = userInfoService.findPage(userInfoQueryVo,pageNum,pageSize);
        return R.ok().data("page",page);
    }

    @ApiOperation(value = "锁定")
    @GetMapping("lock/{userId}/{status}")
    public R lock(
            @PathVariable("userId") Long userId,
            @PathVariable("status") Integer status){
        userInfoService.lock(userId, status);
        return R.ok();
    }

    //用户详情
    @GetMapping("show/{userId}")
    public R show(@PathVariable Long userId) {
        Map<String,Object> map = userInfoService.show(userId);
        return R.ok().data(map);
    }

    //认证审批
    @GetMapping("approval/{userId}/{authStatus}")
    public R approval(@PathVariable Long userId,@PathVariable Integer authStatus) {
        userInfoService.approval(userId,authStatus);
        return R.ok();
    }
}
