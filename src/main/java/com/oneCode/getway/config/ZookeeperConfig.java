package com.oneCode.getway.config;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author lucong
 * @date 2022/10/28 18:59
 */
@Configuration
public class ZookeeperConfig {

    @Value("${zookeeper_ip}")
    private String ips;

    @Bean(initMethod = "start", destroyMethod = "close")
    public CuratorFramework curatorFramework(){
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 5);
        return CuratorFrameworkFactory
                .builder()
                .connectString(ips)
                .sessionTimeoutMs(10000)
                .retryPolicy(retryPolicy)
                .build();
    }
}
