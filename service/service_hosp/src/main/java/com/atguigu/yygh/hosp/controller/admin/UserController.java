package com.atguigu.yygh.hosp.controller.admin;

import com.atguigu.yygh.common.result.R;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

//@CrossOrigin
@RestController
@RequestMapping("/admin/hosp/hospitalSet/user")
public class UserController {


    @PostMapping("/login")
    public R login(@RequestBody Map<String,Object> map){
        System.out.println("map = " + map);
        return R.ok().data("token","admin-token");
    }

    @GetMapping("/info")
    public R info(@RequestParam String token){
        System.out.println("token = " + token);
        return R.ok().data("roles","[admin]")
                .data("introduction","I am a super administrator")
                .data("avatar","https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif")
                .data("name","Super Admin");
    }

    @PostMapping("/logout")
    public R logout(){
        return R.ok().data("success","success");
    }



}
