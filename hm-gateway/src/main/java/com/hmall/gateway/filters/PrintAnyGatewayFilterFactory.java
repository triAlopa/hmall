package com.hmall.gateway.filters;

import lombok.Data;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class PrintAnyGatewayFilterFactory extends AbstractGatewayFilterFactory<PrintAnyGatewayFilterFactory.Config> {
    @Override
    public GatewayFilter apply(PrintAnyGatewayFilterFactory.Config config) {
        /*return new GatewayFilter() {
            @Override
            public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
                System.out.println("print any filter");
                return chain.filter(exchange);
            }
        };*/
        return new OrderedGatewayFilter(new GatewayFilter() {
            @Override
            public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

                System.out.println(config);

                System.out.println("print any filter");
                return chain.filter(exchange);
            }
        }, 1);
    }


    @Data
    public static class  Config{
        private String C;
        private String H;
        private String X;
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return List.of("C","H","X");
    }

    public PrintAnyGatewayFilterFactory() {
        super(Config.class);
    }
}
