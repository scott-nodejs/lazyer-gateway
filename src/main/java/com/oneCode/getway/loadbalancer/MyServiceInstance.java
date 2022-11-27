package com.oneCode.getway.loadbalancer;

import lombok.Data;
import org.springframework.cloud.client.ServiceInstance;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author lucong
 * @date 2022/10/28 11:28
 */

public class MyServiceInstance implements ServiceInstance {

    private URI uri;

    private String host;

    private int port;

    private boolean secure;

    private Map<String, String> metadata = new LinkedHashMap<>();

    private String instanceId;

    private String serviceId;

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setSecure(boolean secure) {
        this.secure = secure;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    @Override
    public String getServiceId() {
        return serviceId;
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    @Override
    public URI getUri() {
        return uri;
    }

    @Override
    public Map<String, String> getMetadata() {
        return metadata;
    }
}
