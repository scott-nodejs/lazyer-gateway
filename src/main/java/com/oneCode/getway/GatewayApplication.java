package com.oneCode.getway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author lucong
 * @date 2022/10/27 17:19
 */

@SpringBootApplication
public class GatewayApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(GatewayApplication.class, args);

    }
}
