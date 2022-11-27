package com.oneCode.getway.filter;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONObject;
import com.oneCode.getway.service.LocalDataSync;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.handler.predicate.AbstractRoutePredicateFactory;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 自定义路由系列化加载配置,负责对路由信息的读、写、删除的方法
 */
@Slf4j
@Component
public class RouteDefinitionWriterAndReader extends LocalDataSync implements RouteDefinitionRepository, ApplicationEventPublisherAware {

    /**
     * 这个是 Gateway 默认加载路由信息的配置（示例代码并没有使用Redis 和 MySQL ，读者自行变更为 MySQL 中存储，启动时加载到 Redis 中即可）
     */
    @Autowired
    private GatewayProperties properties;

    private ApplicationEventPublisher applicationEventPublisher;

    /**
     * 后续改为 Redis 和 数据库方式存储 路由信息,此处改为相应的 RedisTemplate
     */
    private static volatile Map<String, RouteDefinition> ROUTES_MAP = new ConcurrentHashMap<>(1 << 4);


    /**
     * 初始化配置文件中的路由到自定义容器中,后续直接从数据库中存储到 Redis 去
     */
//    @PostConstruct
//    public void initRoute() {
//        properties.getRoutes().forEach(route -> {
//            BeanUtil.copyProperties(route, routeInDb);
//            // 暂时没有路由名称,ID代替
//            routeInDb.setRouteName("路由名称：" + route.getId());
//            log.info("加载路由:{},路由信息为：{}", routeInDb.getRouteName());
//            //ROUTES_MAP.put(route.getId(), routeInDb);
//        });
//        log.info("路由加载完毕,路由数量为：{}", ROUTES_MAP.size());
//    }


    /**
     * 获取路由配置
     *
     * @return
     */
    @SneakyThrows
    @Override
    public Flux<RouteDefinition> getRouteDefinitions() {
        // RouteDefinition 后续可改为数据库存储的对象 RouteDefinitionInDb
        log.info("**************更新路由*************");
        List<String> url = urlMap.get("url");
        RouteDefinition routeDefinition = properties.getRoutes().get(0);
        if(url != null) {
            for (String s : url) {
                RouteDefinition routeDefinition1 = new RouteDefinition();
                BeanUtil.copyProperties(routeDefinition, routeDefinition1);
                URI uri = new URI("ws://" + s);
                routeDefinition1.setUri(uri);
                ROUTES_MAP.put(routeDefinition1.getId(), routeDefinition1);
            }
        }

        List<RouteDefinition> routesInDb = ROUTES_MAP.entrySet().stream()
                .map(routeEntry -> routeEntry.getValue())
                .collect(Collectors.toList());
        return Flux.fromIterable(routesInDb);
    }


    /**
     * 保存路由配置（暂时未实现具体逻辑）
     *
     * @param route
     * @return
     */
    @Override
    public Mono<Void> save(Mono<RouteDefinition> route) {
        // 暂时使用默认方式配置,即使用配置文件读取的方式配置,后续改为Redis存储
        Mono<Void> mono = route.flatMap(r -> {
            RouteDefinitionInDb routeInDb = new RouteDefinitionInDb();
            BeanUtil.copyProperties(r, routeInDb);
            ROUTES_MAP.put(routeInDb.getId(), routeInDb);
            return Mono.empty();
        });
        return mono;
    }

    /**
     * 删除路由配置（暂时未实现具体逻辑）
     *
     * @param routeId
     * @return
     */
    @Override
    public Mono<Void> delete(Mono<String> routeId) {
        // 时使用默认方式配置,即使用配置文件读取的方式配置,后续改为Redis存储
        return routeId.flatMap(id -> {
            if (ROUTES_MAP.containsKey(id)) {
                ROUTES_MAP.remove(id);
                return Mono.empty();
            }
            return Mono.defer(() -> Mono.error(new NotFoundException("RouteDefinition not found: " + routeId)));
        });
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void refreshRoutes(){
        this.applicationEventPublisher.publishEvent(new RefreshRoutesEvent(this));
    }
}

