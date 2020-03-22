package com.changgou.order.controller;

import com.changgou.entity.Result;
import com.changgou.entity.StatusCode;
import com.changgou.order.config.TokenDecode;
import com.changgou.order.pojo.OrderItem;
import com.changgou.order.service.CartService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/***
 * 描述
 * @author ljh
 * @packagename com.changgou.order.controller
 * @version 1.0
 * @date 2020/2/7
 */
@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private TokenDecode tokenDecode;

    /** 购物车的数据  List<orderItem>
     * 给指定的商品添加到购物车
     * @param num 购买的数量
     * @param id  购买的sku（商品的）的ID
     * @return
     */
    @RequestMapping("/add")
    public Result addCart(Integer num, Long id){
//        String username = "szitheima";//获取当前的登录的用户的用户名

        //动态获取当前用户的用户名
        Map<String, String> map = tokenDecode.getUserInfo();//单独写了一个配置类 交给了spring容器 用来获取用户信息 返回结果是map

        String username = map.get("username");//通过map把用户名获取出来

        cartService.add(num,id,username);

        return new Result(true, StatusCode.OK,"添加购物车成功");

    }


    @RequestMapping("/list")
    public Result<List<OrderItem>> findCartList(){
        //1.获取当前的登录的用户名
        String username = "szitheima";//获取当前的登录的用户的用户名 先把用户名写死

        //2.根据用户名从redis中获取该用户对应的购物车列表数据
        List<OrderItem> cartList = cartService.list(username);
        //3.返回
        return  new Result<List<OrderItem>>(true,StatusCode.OK,"查询购物车成功！",cartList);
    }
}
