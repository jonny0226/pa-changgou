package com.changgou.order.service.impl;

import com.changgou.entity.Result;
import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.feign.SpuFeign;
import com.changgou.goods.pojo.Sku;
import com.changgou.goods.pojo.Spu;
import com.changgou.order.pojo.OrderItem;
import com.changgou.order.service.CartService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/***
 * 描述
 * @author ljh
 * @packagename com.changgou.order.service.impl
 * @version 1.0
 * @date 2020/2/7
 */
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private SkuFeign skuFeign;

    @Autowired
    private SpuFeign spuFeign;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void add(Integer num, Long id, String username) {

        //前端点击加减号实现购物车商品数量的变化 需要先判断数量是多少来决定如何处理
        if(num<=0){
            //删除商品
            redisTemplate.boundHashOps("Cart_" + username).delete(id);
            //删除之后就不需要向下进行了 否则下面代码对应数据就加进缓存了
            return;
        }



        //1.先根据id获取到sku的数据(用feign的方式来调用)
        //1.1 定义feign
        //1.2 引入feign的依赖
        //1.3 启用feign 调用
        Result<Sku> skuResult = skuFeign.findById(id);
        Sku sku = skuResult.getData();
        Result<Spu> resultspu = spuFeign.findById(sku.getSpuId());//根据spu的ID 获取spu的数据---》获取里面的1  2 3 分类的数据。
        Spu spu = resultspu.getData();
        //2.将数据获取到出来设置到 orderitem

        OrderItem orderItem = new OrderItem();

        //todo 将数据设置到orderitem

        orderItem.setCategoryId1(spu.getCategory1Id());//一级分类
        orderItem.setCategoryId2(spu.getCategory2Id());//二级分类
        orderItem.setCategoryId3(sku.getCategoryId());//三级分类
        orderItem.setSpuId(sku.getSpuId());
        orderItem.setSkuId(id);
        orderItem.setName(sku.getName());//商品的名称
        orderItem.setPrice(sku.getPrice());//商品的单价
        orderItem.setNum(num);//商品的购买的数量
        orderItem.setMoney(num*sku.getPrice());//金额
        orderItem.setPayMoney(num*sku.getPrice());//金额
        orderItem.setImage(sku.getImage());//图片的路径
        //3.添加到redis中
        redisTemplate.boundHashOps("Cart_"+username).put(id,orderItem);

    }


    /**
     * 根据登录用户的用户名 查询获取该用户加入到购物车的详情
     * @param username
     * @return
     */
    @Override
    public List<OrderItem> list(String username) {

        // Cart_zhangsan      skuID1      pojo1 (orderitem1)
        List values = redisTemplate.boundHashOps("Cart_" + username).values();//values()方法 将添加到购物车的所有hash值key对应的field中的value全部查询出来
        return values;//将查询出的所有数据返回
    }
}
