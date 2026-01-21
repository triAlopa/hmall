package com.hmall.gateway.config;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;

@Component
@RequiredArgsConstructor
@Slf4j
public class DynamicRouterLoader {


    private final NacosConfigManager nacosConfigManager;

    private final RouteDefinitionWriter writer;

    private final String dataId = "gateway-routes.json";

    private final String group = "DEFAULT_GROUP";

    @PostConstruct
    public void initRouterListener() throws NacosException {
// 1.注册监听器并首次拉取配置
        String configInfo = nacosConfigManager.getConfigService()
                .getConfigAndSignListener(dataId, group, 5000,
                        new Listener() {
                            @Override
                            public Executor getExecutor() {
                                return null;
                            }

                            //
                            @Override
                            public void receiveConfigInfo(String configInfo) {
                                updateConfigInfo(configInfo);
                            }
                        }
                );
        // 2.首次启动时，更新一次配置
        updateConfigInfo(configInfo);
    }

    private final Set<String> routIds = new HashSet<>();

    private void updateConfigInfo(String configInfo) {

        log.debug("~~~~~~~~ （*＾-＾*） listener update nacos config info : {}", configInfo);
        //1.json反序列化 为路由配置所需要的类
        List<RouteDefinition> routes = JSONUtil.toList(configInfo, RouteDefinition.class);
        //2.清空更新之前的路由
        if (CollectionUtil.isNotEmpty(routIds)) {
            routIds.forEach(id -> {
                writer.delete(Mono.just(id)).subscribe();
            });
            //3.清空记录列表
            routIds.clear();
        }

        if (CollectionUtil.isEmpty(routes)) {
            return;
        }

        //4.添加更新的路由，并加入routeIds
        for (RouteDefinition routeDefinition : routes) {
            //4.1批量更新并订阅
            writer.save(Mono.just(routeDefinition)).subscribe();
            //4.2以便于下次更新时候删除
            routIds.add(routeDefinition.getId());
        }

    }

}
