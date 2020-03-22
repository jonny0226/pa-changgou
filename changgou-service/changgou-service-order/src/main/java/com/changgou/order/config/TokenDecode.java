package com.changgou.order.config;

import com.alibaba.fastjson.JSON;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.stream.Collectors;

/***
 * 描述
 * @author ljh
 * @packagename com.changgou.order.config
 * @version 1.0
 * @date 2020/2/8
 */
@Component
public class TokenDecode {
    private static final String PUBLIC_KEY = "public.key";
    private String getPubKey() {
        Resource resource = new ClassPathResource(PUBLIC_KEY);
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(resource.getInputStream());
            BufferedReader br = new BufferedReader(inputStreamReader);
            return br.lines().collect(Collectors.joining("\n"));
        } catch (IOException ioe) {
            return null;
        }
    }

    /**
     * 获取当前的登录的用户的信息
     * @return
     */
    public Map<String,String> getUserInfo(){
        OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String tokenValue = details.getTokenValue();//令牌信息
        Jwt jwt = JwtHelper.decodeAndVerify(tokenValue, new RsaVerifier(getPubKey()));

        //获取Jwt原始内容
        String claims = jwt.getClaims();//json格式的字符串

        Map<String,String> map = JSON.parseObject(claims, Map.class);
        return map;
    }
}
