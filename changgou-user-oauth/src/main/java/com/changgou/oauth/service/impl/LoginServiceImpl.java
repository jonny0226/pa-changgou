package com.changgou.oauth.service.impl;

import com.changgou.oauth.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.Map;

/***
 * 描述
 * @author ljh
 * @packagename com.changgou.oauth.service.impl
 * @version 1.0
 * @date 2020/1/17
 */
@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private LoadBalancerClient loadBalancerClient;

    //模拟浏览器发送POST请求 传递参数  返回MAP
    @Override
    public Map<String, Object> login(String username, String password, String grantType, String client_id, String client_secret) {
        //定义一个请求的路径：
        ServiceInstance choose = loadBalancerClient.choose("user-auth");
        String url = "http://"+choose.getHost()+":"+choose.getPort()+"/oauth/token";

        //定义一个请求体
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type",grantType);
        body.add("username",username);
        body.add("password",password);

        //定义一个请求头
        MultiValueMap<String, String> headers= new LinkedMultiValueMap<>();
        headers.add("Authorization","Basic "+Base64.getEncoder().encodeToString((client_id+":"+client_secret).getBytes()));

        //参数1  指定的是请求体对象
        //参数2  指定的是请求头对象
        HttpEntity<MultiValueMap<String,String>> requestentity = new HttpEntity<MultiValueMap<String,String>>(body,headers);

        //参数1 指定的是要发送的请求路径
        //参数2 指定的是要发送的请求方法 post
        //参数3 指定的是发送请求携带的请求体和头部信息的封装对象
        //参数4  指定的是请求完成之后返回的数据类型


        ResponseEntity<Map> exchange = restTemplate.exchange(url, HttpMethod.POST, requestentity, Map.class);

        Map<String,Object> entityBody = exchange.getBody();

        return entityBody;
    }

    public static void main(String[] args) {
        byte[] decode = Base64.getDecoder().decode(new String("YXNkZmFmZHNhZjphZmRzYWZhZmFzYWZmZGZhZA==").getBytes());
        System.out.println(new String(decode) );
    }
}
