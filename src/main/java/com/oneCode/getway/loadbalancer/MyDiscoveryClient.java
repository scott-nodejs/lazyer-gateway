package com.oneCode.getway.loadbalancer;

import com.oneCode.getway.service.LocalDataSync;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lucong
 * @date 2022/10/28 11:21
 */
@Slf4j
public class MyDiscoveryClient extends LocalDataSync implements DiscoveryClient {
    @Override
    public String description() {
        return null;
    }

    @Override
    public List<ServiceInstance> getInstances(String serviceId) {
        try {
            List<String> url = urlMap.get("url");
            if (url == null || url.size() == 0) {
                throw new RuntimeException();
            }

            List<ServiceInstance> list = new ArrayList<>();
            url.forEach(u -> {
                try {
                    MyServiceInstance myServiceInstance = new MyServiceInstance();
                    URI uri = new URI("http://" + u);
                    myServiceInstance.setUri(uri);
                    String[] split = u.split(":");
                    myServiceInstance.setHost(split[0]);
                    myServiceInstance.setPort(Integer.parseInt(split[1]));
                    list.add(myServiceInstance);
                } catch (URISyntaxException e) {
                    log.error("error : {}", e);
                }
            });
            return list;
        }catch (Exception e){

        }
        return null;
    }

    @Override
    public List<String> getServices() {
        return null;
    }
}
