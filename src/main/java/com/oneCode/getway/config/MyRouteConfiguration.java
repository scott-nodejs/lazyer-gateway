package com.oneCode.getway.config;

import com.oneCode.getway.filter.MyRouteToRequestUrlFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author lucong
 * @date 2022/11/7 19:29
 */
@Configuration
public class MyRouteConfiguration {

    @Bean
    public MyRouteToRequestUrlFilter myRouteToRequestUrlFilter(){
        return new MyRouteToRequestUrlFilter();
    }
}
