package com.hmall.gateway.filters;

import cn.hutool.core.util.StrUtil;
import com.hmall.common.exception.UnauthorizedException;
import com.hmall.gateway.config.AuthProperties;
import com.hmall.gateway.utils.JwtTool;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class LoginAuthGlobalFilter implements GlobalFilter, Ordered {//order过滤器排名 一定要比NettyRoutingFilter高

    private final AuthProperties authProperties;

    private final JwtTool jwtTool;

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //1.获取请求
        ServerHttpRequest request = exchange.getRequest();
        //2.获取请求头
        HttpHeaders headers = request.getHeaders();
        //3.检验是否放行
        String requestPath = request.getPath().value();
        System.out.println(requestPath);
        //3.1存在放行
        if (isExcludePath(requestPath)) {
            return chain.filter(exchange);
        }
        //4.获取登录校验解析token
        String token = headers.getFirst("authorization");
        ServerHttpResponse response = exchange.getResponse();
        //5.不存在
        if (StrUtil.isBlank(token)) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }


        try {
            Long userId = jwtTool.parseToken(token);

            //保存给后面业务使用
            ServerWebExchange requestExchange = exchange.mutate()
                    .request(builder -> builder.header("user-info", userId.toString())).build();

            return chain.filter(requestExchange);
        } catch (UnauthorizedException e) {
            //6.解析失败
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

    }

    private boolean isExcludePath(String path) {
        List<String> excludePaths = authProperties.getExcludePaths();
        for (String excludePath : excludePaths) {
            if (antPathMatcher.match(excludePath, path)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
