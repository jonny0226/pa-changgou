package com.changgou;

import com.xpand.starter.canal.annotation.EnableCanalClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * canal的启动类
 */
@SpringBootApplication (exclude = DataSourceAutoConfiguration.class)//不需要自动配置数据库
@EnableEurekaClient
@EnableCanalClient//监听的注解的生效 启动canal
@EnableFeignClients(basePackages = {"com.changgou.content.feign"})//启动类中开启feign 可以用canal client向广告微服务发请求 获取变化的数据
public class CanalApplication  {
    public static void main(String[] args) {
        SpringApplication.run(CanalApplication.class,args);
    }
}
