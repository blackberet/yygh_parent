package com.atguigu.yygh.gateway.filter;

import com.google.gson.JsonObject;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

//@Component
public class AuthenticationGlobalFilter implements GlobalFilter, Ordered {

    private AntPathMatcher antPathMatcher=new AntPathMatcher();

    //执行过滤功能
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        ServerHttpResponse response = exchange.getResponse();
        //登录请求不拦截
        if(antPathMatcher.match("/admin/hosp/hospitalSet/user/**",path)){
            return chain.filter(exchange);
        }else if (antPathMatcher.match("/admin/**",path)) {
            HttpHeaders headers = request.getHeaders();
            List<String> strings = headers.get("X-Token");
            if (strings != null) {
                if (strings.get(0).equals("admin-token")) {
                    return chain.filter(exchange);//放行
                }
            }
            response.setStatusCode(HttpStatus.SEE_OTHER);
            response.getHeaders().set(HttpHeaders.LOCATION,"http://localhost:9528");
            return response.setComplete();//结束请求
        }
        else {
            return out(response);
        }
    }

    private Mono<Void> out(ServerHttpResponse response) {
        JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty("code","20001");
        jsonObject.addProperty("message","路径有误");
        jsonObject.addProperty("success","false");
        byte[] bits =jsonObject.toString().getBytes(StandardCharsets.UTF_8);

        DataBuffer buffer = response.bufferFactory().wrap(bits);
        response.setStatusCode(HttpStatus.NOT_FOUND);
        //指定编码，否则在浏览器中会中文乱码
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        return response.writeWith(Mono.just(buffer));
    }


    //影响的是全局过滤器的执行顺序:值越小优先级越高。
    @Override
    public int getOrder() {
        return 0;
    }
}
