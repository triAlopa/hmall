package com.hmall.cart.domain.properties;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "hm.cart")
@Data
public class MaxItemProperties {

    private Integer maxItem;
}
