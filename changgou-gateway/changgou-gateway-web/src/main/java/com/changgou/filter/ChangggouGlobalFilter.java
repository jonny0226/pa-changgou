package com.changgou.filter;

import com.changgou.util.JwtUtil;
import io.netty.util.internal.StringUtil;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 网关服务中的全局过滤器
 */
@Component//把全局过滤器交给spring容器
public class ChangggouGlobalFilter implements GlobalFilter,Ordered {

    //令牌头名字
    private static final String AUTHORIZE_TOKEN = "Authorization";


    //过滤器作用：
    //1 拦截请求 2 获取令牌 3 校验令牌信息 通过就放行 如果没通过 就返回页面 告知没有权限
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //1获取请求对象
        ServerHttpRequest request = exchange.getRequest();
        //2 获取响应对象
        ServerHttpResponse response = exchange.getResponse();
        //3 获取当前请求的路径 如果是登录就直接放行 如果是其他 就先让客户去登录
        String path = request.getURI().getPath();//挡墙请求路径  登陆的请求路径是 /api/user/login
        if (path.startsWith("/api/user/login")){//如果以这个开头 说明是登录请求/api/user/login
            return chain.filter(exchange);//是登录请求就直接方放行 执行链进行处理
        }
        //4 先从头部信息header中获取令牌 如果没有令牌 从请求参数中获取
        String token = request.getHeaders().getFirst(AUTHORIZE_TOKEN);//取出头部信息中的第一个信息
        if (StringUtils.isEmpty(token)){
            //5 从请求参数中获取令牌 如果没有 从cookie中获取令牌
            token = request.getQueryParams().getFirst(AUTHORIZE_TOKEN);//从请求参数中尝试获取token 再赋值给上面的token
        }
        if (StringUtils.isEmpty(token)){//如果请求参数中还是为空 就从cookie中再找
            //6 从cookie中获取 再没有直接返回无权限
            HttpCookie cookie = request.getCookies().getFirst(AUTHORIZE_TOKEN);
            if (cookie!=null){
                token = cookie.getValue();//把cookie中尝试取出的赋值给token
            }
        }
        if (StringUtils.isEmpty(token)){//如果还是为空 就只能返回页面告知没有权限
            //给返回一个请求的状态码
            //response.setStatusCode(HttpStatus.UNAUTHORIZED);//告知一个没有认证的状态码

            //1.设置状态码为重定向
            response.setStatusCode(HttpStatus.SEE_OTHER);//303 就是重定向的状态码

            //request.getURI().toString();//客户最开始访问的地址（当前访问的地址 需要把这个地址带上传过去 发送ajax'请求时一并带上 后面重定向时可以再定向回当前访问地址）
            //2.设置重定向的url地址 就是客户没有权限登录 我们不能直接401无法显示 需要重定向到登录页面让客户登录 获取token信息
            response.getHeaders().set("Location","http://localhost:9001/oauth/login?from="+request.getURI().toString());

            return response.setComplete();//请求结束
        }
        //为了让各个微服务自身完成权限校验 先把网关这里的校验注释掉
        //7 获取令牌之后再进行JWT的解析校验 解析通过就放行 未通过就返回无权限到页面
//        try {
//            JwtUtil.parseJWT(token);//解析成功 后面放行
//        } catch (Exception e) {//解析失败
//            e.printStackTrace();
//            response.setStatusCode(HttpStatus.UNAUTHORIZED);//告知一个没有认证的状态码
//            return response.setComplete();//请求结束
//        }

        //7、网关服务获取到token之后，将token传递给对应的微服务
        request.mutate().header(AUTHORIZE_TOKEN,"bearer "+token);


        return chain.filter(exchange);//解析成功就放行
    }

    //设置过滤器执行顺序  值越小 优先执行
    @Override
    public int getOrder() {
        return 0;
    }
}
