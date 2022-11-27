package com.oneCode.getway.filter;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.cloud.gateway.route.RouteDefinition;

import java.io.Serializable;

/**
 * 扩展此类支持序列化路由,（可将路由信息添加到数据库及Redis）
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class RouteDefinitionInDb extends RouteDefinition implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 路由名称
     */
    private String routeName;
}
