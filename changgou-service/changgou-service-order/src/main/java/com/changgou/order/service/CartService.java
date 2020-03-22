package com.changgou.order.service;

import com.changgou.order.pojo.OrderItem;

import java.util.List;

/***
 * 描述
 * @author ljh
 * @packagename com.changgou.order.service
 * @version 1.0
 * @date 2020/2/7
 */
public interface CartService {
    /**
     * 给指定的当前的用户 进行添加购物车
     * @param num  购买的数量
     * @param id 购买的商品的ID
     * @param username 购买商品的的用户
     */
    void add(Integer num, Long id, String username);


    /**
     * 根据登录用户的用户名 查询获取该用户加入到购物车的详情
     * @param username
     * @return
     */
    List<OrderItem> list(String username);
}
