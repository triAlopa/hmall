package com.hmall.api.config;


import feign.Logger;
import org.springframework.context.annotation.Bean;

public class DefaultFeignLoggerConfiguration {

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
}
