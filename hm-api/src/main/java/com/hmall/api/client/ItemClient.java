package com.hmall.api.client;

import com.hmall.api.config.DefaultFeignLoggerConfiguration;
import com.hmall.api.domain.dto.ItemDTO;
import com.hmall.api.domain.dto.OrderDetailDTO;
import com.hmall.api.fallback.ItemClientFallBackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.List;

@FeignClient(value = "item-service",configuration = DefaultFeignLoggerConfiguration.class
,fallbackFactory = ItemClientFallBackFactory.class)
public interface ItemClient {

    @GetMapping("items")
    public List<ItemDTO> queryItemByIds(@RequestParam("ids") Collection<Long> ids);


    @PutMapping("items/stock/deduct")
    public void deductStock(@RequestBody List<OrderDetailDTO> items);
}
