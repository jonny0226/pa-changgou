package com.changgou.goods.feign;


import com.changgou.entity.Result;
import com.changgou.goods.pojo.Sku;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * sku库存商品的接口 是用来和数据库交互 同时也供查询的微服务feign调用的接口 这种调用方法 符合程序员的逻辑
 */

@FeignClient(name = "goods")
@RequestMapping("/sku")
public interface SkuFeign {
    /***
     * 根据审核状态查询Sku
     * @param status
     * @return
     */
    @GetMapping("/status/{status}")
    Result<List<Sku>> findByStatus(@PathVariable(name = "status") String status);


    @GetMapping("/{id}")
    public Result<Sku> findById(@PathVariable(name = "id") Long id);

    //订单服务中如果进行下单 需要减少库存 这里调用了feign
    //本质上就是执行sql update tb_sku set num = num-#{num} where id=#{skuid}
    /***
     * 库存递减
     * @param num
     * @param id
     * @return
     */
    @PostMapping(value = "/decCount")
    Result decCount(@RequestParam(value = "id") Long id,@RequestParam(value = "num") Integer num);

}
