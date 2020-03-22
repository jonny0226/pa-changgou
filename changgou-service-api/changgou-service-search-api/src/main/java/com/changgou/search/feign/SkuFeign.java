package com.changgou.search.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * 实现调用搜索的feign
 */
@FeignClient(name = "search")//name = "search"与service-search的微服务服务名一致
@RequestMapping("/search")//请求方法的路径("/search")与微服务service-search中的请求方法一致
public interface SkuFeign {
    /**
     * 搜索
     * @param searchMap
     * @return
     */

    //由于以后做搜索都是基于GET请求，所以我们需要将之前的搜索改成GET请求操作，
    // 修改changgou-service-search微服务的com.changgou.search.controller.SkuController里面的search方法
    @GetMapping
    Map search(@RequestParam(required = false) Map searchMap);//RequestParam?? 传过来的参数可以不写？
}
