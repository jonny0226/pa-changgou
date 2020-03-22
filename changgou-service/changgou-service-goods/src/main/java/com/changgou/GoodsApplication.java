package com.changgou;

import com.changgou.entity.IdWorker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * 创建启动类GoodsApplication
 */
@SpringBootApplication
@EnableEurekaClient
@MapperScan(basePackages = "com.changgou.goods.dao")//??????????
public class GoodsApplication {
    public static void main(String[] args) {
        SpringApplication.run(GoodsApplication.class,args);//课件中启动类不需要args吗
    }

    /***
     * IdWorker 主键生成工具
     * @return
     */
    @Bean
    public IdWorker idWorker(){
        return new IdWorker(0,0);
    }
}
