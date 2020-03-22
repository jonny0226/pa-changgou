package com.changgou.order.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

@Component //写这个拦截器就是需要在feign调用的时候携带上token在header里 到商品服务中去调用
public class MyFeignInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        //1获取当前请求中的token信息

        //1.1 先获取当前请求的线程的请求request对象
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes!=null){
            HttpServletRequest request = requestAttributes.getRequest();
        //1.2 获取请求头中所有的头信息
            Enumeration<String> headerNames = request.getHeaderNames();
            if (headerNames != null) {
                while (headerNames.hasMoreElements()) {
                    //头文件的key
                    String name = headerNames.nextElement();
                    //头文件的value
                    String values = request.getHeader(name);
                    //将令牌数据添加到头文件中
                    //2 将获取到的token设置到头信息中 携带过去
                    requestTemplate.header(name, values);
                }
            }

        }
    }
}
