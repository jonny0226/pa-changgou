package com.changgou.test;

import io.jsonwebtoken.*;

import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 测试生成token 令牌的方法
 */
public class JWTTest {

    /****
     * 创建Jwt令牌
     */
    @Test
    public void testCreateJwt(){
        //设置头部
        JwtBuilder builder= Jwts.builder()
        //设置载荷
                .setId("888")             //设置唯一编号
                .setSubject("小白")       //设置主题  可以是JSON数据
//                .setExpiration(new Date())//设置令牌有效期 这个有效期是当前时间有效 过了当前时间就失效了 仅仅为一个测试案例
                .setIssuedAt(new Date())  //设置签发日期
        //设置签名
                .signWith(SignatureAlgorithm.HS256,"itcast");//设置签名 使用HS256算法，并设置SecretKey(字符串) itcast是盐值 通过这个盐值进行加密


        //自定义 载荷
        Map<String,Object> map = new HashMap<>();
        map.put("kye1","value1");
        map.put("kye2","value2");
        builder.addClaims(map);//相当于把json数据（map）加入到载荷中 来构建jwt的载荷部分
        //构建 并返回一个字符串
        String compact = builder.compact();//生成令牌
        System.out.println(compact);
    }


    /**
     * 解析令牌
     */
    @Test
    public void parse(){
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI4ODgiLCJzdWIiOiLlsI_nmb0iLCJpYXQiOjE1NzkxNTY1MzcsImt5ZTEiOiJ2YWx1ZTEiLCJreWUyIjoidmFsdWUyIn0.Kvp7SE8X7MyJSyz7sfiYlVdXrvkQHTOElA8-ZGUM-2I";
        Jws<Claims> itcast = Jwts.parser()
                .setSigningKey("itcast")//把盐加进去进行解析
                .parseClaimsJws(token);//把刚生成的token传进去进行解析
        System.out.println(itcast.getBody());//body就是传进去的数据 就是载荷部分
    }
}
