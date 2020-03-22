package com.changgou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * 用户微服务的启动类
 *
 */
@SpringBootApplication
@EnableEurekaClient
@MapperScan(basePackages = "com.changgou.user.dao")//这里需要增删查改 需要dao 所以需要这个注解
public class UserApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class,args);
    }
}
