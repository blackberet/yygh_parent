package com.atguigu.yygh.user.controller.user;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.common.exception.YYGHException;
import com.atguigu.yygh.common.result.R;
import com.atguigu.yygh.common.utils.JwtHelper;
import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.user.prop.WeixinProperties;
import com.atguigu.yygh.user.service.UserInfoService;
import com.atguigu.yygh.user.utils.HttpClientUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/user/wx")
public class WeiXinController {
    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private WeixinProperties weixinProperties;

    @GetMapping("/params")
    @ResponseBody
    public R WXLogin() throws UnsupportedEncodingException {
        Map<String,Object> map = new HashMap<>();
        map.put("appid",weixinProperties.getAppid());
        map.put("scope","snsapi_login");
        String encode = URLEncoder.encode(weixinProperties.getRedirecturl(),"UTF-8");
        map.put("redirecturi",encode);
        map.put("state",System.currentTimeMillis()+"");
        return R.ok().data(map);
    }

    @GetMapping("/callback")
    public String callback(String code,String state) throws Exception {
        StringBuilder stringBuilder = new StringBuilder()
                .append("https://api.weixin.qq.com/sns/oauth2/access_token")
                .append("?appid=%s")
                .append("&secret=%s")
                .append("&code=%s")
                .append("&grant_type=authorization_code");

        String format = String.format(stringBuilder.toString(), weixinProperties.getAppid(),
                weixinProperties.getAppsecret(), code);
        String s = HttpClientUtils.get(format);
        Map map = JSONObject.parseObject(s, Map.class);
        String openid = (String) map.get("openid");
        String access_token = (String) map.get("access_token");
        //首次登陆注册,保存用户信息
        UserInfo userInfo = userInfoService.getUserByOpenId(openid);
        if (userInfo == null) {
            userInfo = new UserInfo();
            userInfo.setOpenid(openid);
            //获取用户昵称
            StringBuilder stringBuilder1 = new StringBuilder()
                    .append("https://api.weixin.qq.com/sns/userinfo")
                    .append("?access_token=%s")
                    .append("&openid=%s");
            String format1 = String.format(stringBuilder1.toString(),access_token,openid);
            String s1 = HttpClientUtils.get(format1);
            JSONObject jsonObject = JSONObject.parseObject(s1);
            String nickname = jsonObject.getString("nickname");
            userInfo.setNickName(nickname);
            userInfo.setStatus(1);

            userInfoService.save(userInfo);
        }
        //已有登陆信息,判断用户状态
        if (userInfo.getStatus() == 0) {
            throw new YYGHException(20001,"该用户被锁定");
        }

        Map<String, String> resultMap = new HashMap<>();
        String name = userInfo.getName();

        if(StringUtils.isEmpty(name)) {
            name = userInfo.getNickName();
        }
        if(StringUtils.isEmpty(name)) {
            name = userInfo.getPhone();
        }
        resultMap.put("name", name);
        resultMap.put("token", JwtHelper.createToken(userInfo.getId(),name));
        //微信扫码登陆强制绑定手机号
        if (StringUtils.isEmpty(userInfo.getPhone())) {
            resultMap.put("openid",openid);
        }else {
            resultMap.put("openid","");
        }

        return "redirect:http://localhost:3000/weixin/callback?token="+resultMap.get("token")+ "&openid="
                +resultMap.get("openid")+"&name="+URLEncoder.encode(resultMap.get("name"),"utf-8");
    }
}
