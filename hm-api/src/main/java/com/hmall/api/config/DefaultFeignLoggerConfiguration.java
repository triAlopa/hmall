package com.hmall.api.config;


import com.hmall.api.fallback.ItemClientFallBackFactory;
import com.hmall.common.utils.UserContext;
import feign.Logger;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;

public class DefaultFeignLoggerConfiguration {

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public RequestInterceptor requestInterceptor() {

        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate requestTemplate) {
                Long userId = UserContext.getUser();
                if (userId != null) {
                    requestTemplate.header("user-info", String.valueOf(userId));
                }
            }
        };
    }

    @Bean
    public ItemClientFallBackFactory itemClientFallBackFactory() {
        return new ItemClientFallBackFactory();
    }
}
