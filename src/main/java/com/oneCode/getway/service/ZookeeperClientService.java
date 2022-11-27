package com.oneCode.getway.service;

import com.oneCode.getway.filter.RouteDefinitionWriterAndReader;
import com.oneCode.getway.listen.ZkNodeEventListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.*;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author lucong
 * @date 2022/10/28 17:38
 */
@Service
@Slf4j
public class ZookeeperClientService implements IZkService, InitializingBean {

    @Autowired
    public CuratorFramework client;

    private final static String path = "/lazyer/pkNode";

    @Autowired
    private RouteDefinitionWriterAndReader routeDefinitionWriterAndReader;

    @Override
    public void getNodeList(Map<String, String> nodeMap) {

    }

    public void cacheListener(String nodePath) {
        //1.创建curatorCache对象
        CuratorCache curatorCache = CuratorCache.build(client, nodePath);
        //2.注册监听器
        curatorCache.listenable().addListener(new ZkNodeEventListener(routeDefinitionWriterAndReader));
        //3.开启监听：true加载缓冲数据
        curatorCache.start();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        cacheListener(path);
    }
}
