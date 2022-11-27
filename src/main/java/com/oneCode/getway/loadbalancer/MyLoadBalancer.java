package com.oneCode.getway.loadbalancer;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.reactive.ReactiveLoadBalancer;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.cloud.loadbalancer.core.ReactorServiceInstanceLoadBalancer;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;

import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * @author lucong
 * @date 2022/10/28 10:51
 */
@Slf4j
@AllArgsConstructor
public class MyLoadBalancer extends LoadBalancerClientFactory {
    private DiscoveryClient discoveryClient;


    /**
     * 根据serviceId 筛选可用服务
     * @param request   当前请求
     * @return
     */
    public ServiceInstance choose(URI uri, ServerHttpRequest request) {
        List<ServiceInstance> instances = discoveryClient.getInstances("providerService");

        //注册中心无实例 抛出异常
        if (CollUtil.isEmpty(instances)) {
            log.warn("No instance available for {}");
            throw new NotFoundException("No instance available for " );
        }

        // 获取请求version，无则随机返回可用实例
        String reqVersion = request.getHeaders().getFirst("aa");
        if (StrUtil.isBlank(reqVersion)) {
            return instances.get(RandomUtil.randomInt(instances.size()));
        }

        // 遍历可以实例元数据，若匹配则返回此实例
        for (ServiceInstance instance : instances) {
            // TODO 可以直接选择一个返回
        }
        return instances.get(RandomUtil.randomInt(instances.size()));
    }
}
