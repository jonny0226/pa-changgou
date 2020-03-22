package com.changgou.oauth.controller;

import com.changgou.entity.Result;
import com.changgou.entity.StatusCode;
import com.changgou.oauth.service.LoginService;
import com.changgou.oauth.util.CookieUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/***
 * 描述
 * @author ljh
 * @packagename com.changgou.oauth.controller
 * @version 1.0
 * @date 2020/1/17
 */
@RestController
@RequestMapping("/user")
public class LoginController {

    @Autowired
    private LoginService loginService;

    private static  final String grantType="password";
    private static final String client_id="changgou";
    private static final String client_secret="changgou";

    //Cookie存储的域名
    @Value("${auth.cookieDomain}")
    private String cookieDomain;

    //Cookie生命周期
    @Value("${auth.cookieMaxAge}")
    private int cookieMaxAge;

    /**
     * 接收用户在页面中
     * 输入的用户名
     * 输入的密码
     * 模拟postman去申请令牌： 1.使用密码模式申请令牌
     *
     * @return
     */
    @RequestMapping("/login")
    public Result<Map<String,Object>> login(String username, String password) {
         // 写一个方法：模拟浏览器发送请求 申请令牌 （需要传递参数：1用户名 2 密码 3.授权模式 4.client_id 5.client_secret）
        Map<String,Object> map = loginService.login(username,password,grantType,client_id,client_secret);
        //多实现一个将token方法到cookie中
        saveCookie(map.get("access_token").toString());
        return new Result(true, StatusCode.OK, "登录成功", map);

    }

    private void saveCookie(String token){
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        CookieUtil.addCookie(response,cookieDomain,"/","Authorization",token,cookieMaxAge,false);
    }
}
