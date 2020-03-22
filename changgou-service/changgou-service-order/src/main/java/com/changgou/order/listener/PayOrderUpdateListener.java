package com.changgou.order.listener;

import com.alibaba.fastjson.JSON;
import com.changgou.order.dao.OrderMapper;
import com.changgou.order.pojo.Order;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/***
 * 监听队列 获取数据进行业务逻辑的操作  对应于 pay微服务的 发送消息给队列 这个订单服务就需要进行队列监听
 * @author ljh
 * @packagename com.changgou.order.listener
 * @version 1.0
 * @date 2020/2/11
 */
@Component
@RabbitListener(queues = "queue.order")
public class PayOrderUpdateListener {

    @Autowired
    private OrderMapper orderMapper;

    //写一个方法 用于接收消息

    @RabbitHandler//用于处理获取队列消息
    public void jiesouMsg(String msg) {
        //1.接收消息 转成 MAP
        Map<String, String> map = JSON.parseObject(msg, Map.class);
        if (map != null) {
            if (map.get("return_code").equalsIgnoreCase("SUCCESS")) {
                if ("SUCCESS".equalsIgnoreCase(map.get("result_code"))) {
                    //2.判断 如果 支付成功 ---》修改订单的状态
                    //2.1 获取订单号
                    String out_trade_no = map.get("out_trade_no");//主键
                    //2.2 查询出数据库的订单数据
                    Order order = orderMapper.selectByPrimaryKey(out_trade_no);
                    //2.3 修改状态 更新回去
                    String time_end = map.get("time_end");
                    //时间的本地化
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                    try {
                        Date parse = simpleDateFormat.parse(time_end);
                        order.setPayTime(parse);//支付完成时间
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    order.setTransactionId(map.get("transaction_id"));
                    order.setPayStatus("1");//已经完成
                    order.setUpdateTime(new Date());//数据被操作更新的时间
                    orderMapper.updateByPrimaryKeySelective(order);
                } else {
                    //3.判断如果 支付失败 ---》订单删除掉   (简单的流程) 不要订单了，将删除掉
                    String out_trade_no = map.get("out_trade_no");//主键
                    //2.2 查询出数据库的订单数据
                    Order order = orderMapper.selectByPrimaryKey(out_trade_no);
                    order.setUpdateTime(new Date());//数据被操作更新的时间
                    order.setIsDelete("1");//标识已经删除
                    orderMapper.updateByPrimaryKeySelective(order);
                }
            }
        }
    }
}
