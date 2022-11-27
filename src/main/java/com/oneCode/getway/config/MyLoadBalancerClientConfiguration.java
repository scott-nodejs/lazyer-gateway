package com.oneCode.getway.config;

import com.oneCode.getway.loadbalancer.MyDiscoveryClient;
import com.oneCode.getway.loadbalancer.MyLoadBalancer;
import com.oneCode.getway.loadbalancer.MyReactiveLoadBalancerClientFilter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.simple.SimpleDiscoveryClient;
import org.springframework.cloud.client.discovery.simple.SimpleDiscoveryProperties;
import org.springframework.cloud.gateway.config.GatewayReactiveLoadBalancerClientAutoConfiguration;
import org.springframework.cloud.gateway.config.LoadBalancerProperties;
import org.springframework.cloud.gateway.filter.ReactiveLoadBalancerClientFilter;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: dong
 * @Date: 2021/7/6 11:23
 * @Description:
 */
@Configuration
//@EnableConfigurationProperties(SimpleDiscoveryProperties.class)
@AutoConfigureBefore(GatewayReactiveLoadBalancerClientAutoConfiguration.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class MyLoadBalancerClientConfiguration {

    @Bean
    public MyDiscoveryClient getMyDiscoveryClient(){
        return new MyDiscoveryClient();
    }

    @Bean
    public MyLoadBalancer getMyLoadBalancer(MyDiscoveryClient myDiscoveryClient){
        return new MyLoadBalancer(myDiscoveryClient);
    }

//    @Bean
//    public SimpleDiscoveryClient getSimpleDiscoveryClient(SimpleDiscoveryProperties simpleDiscoveryProperties){
//        return new SimpleDiscoveryClient(simpleDiscoveryProperties);
//    }
//
//    @Bean
//    public MyLoadBalancer getMyLoadBalancer(SimpleDiscoveryClient simpleDiscoveryClient){
//        return new MyLoadBalancer(simpleDiscoveryClient);
//    }

    @Bean
    public ReactiveLoadBalancerClientFilter gatewayLoadBalancerClientFilter(MyLoadBalancer factory){
        return new MyReactiveLoadBalancerClientFilter(factory);
    }
}

