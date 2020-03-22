package com.changgou.pay.controller;

import com.alibaba.fastjson.JSON;
import com.changgou.entity.Result;
import com.changgou.entity.StatusCode;
import com.changgou.pay.service.WeixinPayService;
import com.github.wxpay.sdk.WXPayUtil;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/weixin/pay")
@CrossOrigin
public class WeixinPayController {

    @Value("${mq.pay.exchange.order}")
    private String exchange;
    @Value("${mq.pay.queue.order}")
    private String queue;
    @Value("${mq.pay.routing.key}")
    private String routing;


    @Autowired
    private WeixinPayService weixinPayService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private Environment environment;

    /***
     * 参数是：需要支付的订单号 和 需要支付的金额
     * 创建二维码   这里要调用WeixinPayService的方法获取创建二维码的信息
     * @return
     */
    @RequestMapping(value = "/create/native")
    public Result createNative(String outtradeno, String money){
        Map<String,String> resultMap = weixinPayService.createNative(outtradeno,money);
        return new Result(true, StatusCode.OK,"创建二维码预付订单成功！",resultMap);
        //这个返回的resultMap中包含了 金额 订单号 和code_url
    }

    /***
     * 查询支付状态
     * @param outtradeno
     * @return resultMap 这map中包含了有订单状态的值
     */
    @GetMapping(value = "/status/query")
    public Result queryStatus(String outtradeno){
        Map<String,String> resultMap = weixinPayService.queryPayStatus(outtradeno);
        return new Result(true,StatusCode.OK,"查询状态成功！",resultMap);
    }


    @RequestMapping("notify/url")
    public String notifyurl(HttpServletRequest request) {
        String result = null;
        try {
            // 接收微信支付通知数据（数据流的形式）结果
            ServletInputStream inputStream = request.getInputStream();

            //获取流中的数据：1.创建字节数组的写流 2.获取到字节数组 3.将字节数组转出String
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int leng = 0;
            while ((leng = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, leng);
            }
            byte[] bytes = outputStream.toByteArray();//数据的字节数组

            String resultxml = new String(bytes, "utf-8");

            System.out.println(resultxml);//该数据将来有可能会用到  有 支付时间 交易流水

            Map<String, String> resultNotifyMap = WXPayUtil.xmlToMap(resultxml);//比较好处理一些
            // 发送消息 消息本身就是所有的通知的所有数据 //todo
            rabbitTemplate.convertAndSend(environment.getProperty("mq.pay.exchange.order")
                    ,environment.getProperty("mq.pay.routing.key"),
                    JSON.toJSONString(resultNotifyMap));

            // 返回给微信支付系统接收的情况
            Map<String, String> resultMap = new HashMap<>();
            resultMap.put("return_code", "SUCCESS");
            resultMap.put("return_msg", "OK");
            result = WXPayUtil.mapToXml(resultMap);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


}


