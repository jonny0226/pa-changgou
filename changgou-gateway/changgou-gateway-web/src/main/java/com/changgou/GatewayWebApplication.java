package com.changgou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 普通网关的启动类
 */
@SpringBootApplication
@EnableEurekaClient
public class GatewayWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(GatewayWebApplication.class,args);
    }


    //在Applicatioin引导类中添加如下代码，KeyResolver用于计算某一个类型的限流的KEY也就是说，可以通过KeyResolver来指定限流的Key。
    //我们可以根据IP来限流，比如每个IP每秒钟只能请求一次，在GatewayWebApplication定义key的获取，获取客户端IP，将IP作为key，
    /***
     * IP限流
     * @return
     */
    @Bean(name = "ipKeyResolver")
    public KeyResolver userKeyResolver(){
        return new KeyResolver() {//这里因为是返回一个接口的实例 实际上就是一个匿名内部类
            @Override
            public Mono<String> resolve(ServerWebExchange exchange) {
                //获取远程客户端IP
                String hostAddress = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
                System.out.println("hostAddress是"+hostAddress);
                return Mono.just(hostAddress);
            }
        };
    }
}



