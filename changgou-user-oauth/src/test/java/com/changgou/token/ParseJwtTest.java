package com.changgou.token;

import org.junit.Test;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;

/*****
 * @Author: www.itheima
 * @Date: 2019/7/7 13:48
 * @Description: com.changgou.token
 *  使用公钥解密令牌数据
 ****/
public class ParseJwtTest {

    /***
     * 校验令牌
     */
    @Test
    public void testParseToken(){
        //令牌
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzY29wZSI6WyJhcHAiXSwibmFtZSI6bnVsbCwiaWQiOm51bGwsImV4cCI6MjAxMzIzNzY3OCwianRpIjoiZjY5MzFhYjEtYTBmZi00ZTI5LTk4MzMtM2IxYjg2ODk0Zjk3IiwiY2xpZW50X2lkIjoic3ppdGhlaW1hIiwidXNlcm5hbWUiOiJzeml0aGVpbWEifQ.h7pwqIbkjHfZvQdsnevuruhtEsMIOTj2DyOfDS0BTJngKR-ilqMc-fTv-iRoS0S_dYUw-TxZ9y0o9LieMNfdoKsMNXC5iZgSnAaVZGa6JJfXyTA63-otRWzMn9V8mf7BPtPtupANQ7KA0loAAlD58g0OP_1gcPJ7wgVWoP79Y4rENNSyJzasPb8mFKOQAe3SOn8ZlNJ7cpP_isoVZEU01XfMLrCnqdT3dyzYuak5CVftbAv0fA_a1lPEyi4yrMjSOBw-GpuagJxyJK7oNvstkdGgdBwFTC_Ke53W2HxwrC_KfiJGxz563IHqcZ0jMeNYa2G2jXHE2VhrbopR9f2eoA";

        //公钥
        String publickey = "-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApER6avbAEZCL+qprQDuTrvQLhklKfzL0WIZY2JE/e72pI6gq/I51rF3OBprkY3D07kHBMz9yyh7l5Kvrf7R0o/rPrEziLWH/ZFOvgH5xWi+rw9tn+LOR5+lLh9ufMXRJFYG5ABMeui4AFcuCtjnr8QYO9D6sQ8KidzzIBZCom3O+gJ7XlcfqbgzdC2y4SvpGz2C1uMdtKuD+wKW1ECut+osiaFMVtp1ivvSCCzz6Fugp7io5PGTb9sVpfh43BQMRf6rLcOEMqOuw6d2bfkLWe/o2FInvvzkH/vYgjff0lgW5NeZzH7X8wALYUUg7w+GU7+uiG5ngysgYRhby9lLT1wIDAQAB-----END PUBLIC KEY-----";

        //校验Jwt
        Jwt jwt = JwtHelper.decodeAndVerify(token, new RsaVerifier(publickey));

        //获取Jwt原始内容
        String claims = jwt.getClaims();
        System.out.println(claims);



        //jwt令牌
        String encoded = jwt.getEncoded();
        System.out.println(encoded);
    }
}
