package com.oneCode.getway.filter;
/*
 * Copyright 2013-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.oneCode.getway.service.LocalDataSync;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import reactor.core.publisher.Mono;

import org.springframework.cloud.gateway.route.Route;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.*;

/**
 * @author Spencer Gibb
 */
public class MyRouteToRequestUrlFilter extends LocalDataSync implements GlobalFilter, Ordered {

    /**
     * Order of Route to URL.
     */
    public static final int ROUTE_TO_URL_FILTER_ORDER = 10001;

    private static final Log log = LogFactory.getLog(org.springframework.cloud.gateway.filter.RouteToRequestUrlFilter.class);

    private static final String SCHEME_REGEX = "[a-zA-Z]([a-zA-Z]|\\d|\\+|\\.|-)*:.*";
    static final Pattern schemePattern = Pattern.compile(SCHEME_REGEX);

    /* for testing */
    static boolean hasAnotherScheme(URI uri) {
        return schemePattern.matcher(uri.getSchemeSpecificPart()).matches()
                && uri.getHost() == null && uri.getRawPath() == null;
    }

    @Override
    public int getOrder() {
        return ROUTE_TO_URL_FILTER_ORDER;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        Route route = exchange.getAttribute(GATEWAY_ROUTE_ATTR);
        if (route == null) {
            return chain.filter(exchange);
        }
        log.trace("RouteToRequestUrlFilter start");
        URI uri = exchange.getRequest().getURI();
        boolean encoded = containsEncodedParts(uri);
        URI routeUri = null;

        if(uri.getScheme().equals("ws") || uri.getScheme().equals("wss")) {
            Map<String, String> attribute = exchange.getAttribute(URI_TEMPLATE_VARIABLES_ATTRIBUTE);
            String roomId = attribute.get("roomId");

            for (Map.Entry<String, List<String>> entry : roomIdMap.entrySet()) {
                if (entry.getValue().contains(roomId)) {
                    try {
                        routeUri = new URI("ws://" + entry.getKey());
                        break;
                    } catch (Exception e) {
                        throw new IllegalStateException("Invalid host: " + routeUri.toString());
                    }
                }
            }
        }else {
            routeUri = route.getUri();
        }

        if (hasAnotherScheme(routeUri)) {
            // this is a special url, save scheme to special attribute
            // replace routeUri with schemeSpecificPart
            exchange.getAttributes().put(GATEWAY_SCHEME_PREFIX_ATTR,
                    routeUri.getScheme());
            routeUri = URI.create(routeUri.getSchemeSpecificPart());
        }

        if ("lb".equalsIgnoreCase(routeUri.getScheme()) && routeUri.getHost() == null) {
            // Load balanced URIs should always have a host. If the host is null it is
            // most
            // likely because the host name was invalid (for example included an
            // underscore)
            throw new IllegalStateException("Invalid host: " + routeUri.toString());
        }

        URI mergedUrl = UriComponentsBuilder.fromUri(uri)
                // .uri(routeUri)
                .scheme(routeUri.getScheme()).host(routeUri.getHost())
                .port(routeUri.getPort()).build(encoded).toUri();
        exchange.getAttributes().put(GATEWAY_REQUEST_URL_ATTR, mergedUrl);
        return chain.filter(exchange);
    }

}

