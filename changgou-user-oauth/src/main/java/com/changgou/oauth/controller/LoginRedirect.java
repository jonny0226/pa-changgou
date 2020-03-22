package com.changgou.oauth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/oauth")
public class LoginRedirect {

    /***
     * 跳转到登录页面
     * @return
     */
    @GetMapping(value = "/login")
    public String login(String from, Model model){
        model.addAttribute("from",from);//from就是ajax请求过来 访问当前路径的值
        return "login";//返回一个逻辑视图
    }
}
