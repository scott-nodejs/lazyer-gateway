package com.oneCode.getway.listen;

import com.oneCode.getway.filter.RouteDefinitionWriterAndReader;
import com.oneCode.getway.service.LocalDataSync;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lucong
 * @date 2022/10/28 19:02
 */
@Slf4j
public class ZkNodeEventListener extends LocalDataSync  implements CuratorCacheListener {

    private RouteDefinitionWriterAndReader routeDefinitionWriterAndReader;

    public ZkNodeEventListener(RouteDefinitionWriterAndReader routeDefinitionWriterAndReader){
        this.routeDefinitionWriterAndReader = routeDefinitionWriterAndReader;
    }


    @Override
    public void event(Type type, ChildData childData, ChildData childData1) {
        switch (type) {
            case NODE_CHANGED:
                log.info("节点数据修改");
                log.info("修改前的节点路径：" + childData.getPath());
                log.info("修改前的节点数据：" + new String(childData.getData()));
                log.info("修改后的节点路径：" + childData1.getPath());
                log.info("修改后的节点数据：" + new String(childData1.getData()));
                String data1 = new String(childData1.getData());
                String url = childData1.getPath().replace("/lazyer/pkNode/", "");
                if(StringUtils.isNotEmpty(data1)){
                    List<String> urls = roomIdMap.putIfAbsent(url, new ArrayList<>());
                    if(urls == null){
                        List<String> urls1 = roomIdMap.get(url);
                        urls1.add(data1);
                    }else{
                        urls.add(data1);
                    }
                }
                break;
            case NODE_CREATED:
                log.info("新增节点");
                log.info("新增节点的路径"+childData1.getPath());
                log.info("新增节点的数据：" + new String(childData1.getData()));
                String data = childData1.getPath().replace("/lazyer/pkNode/", "");
                if(StringUtils.isNotEmpty(data) && data.contains(":")){
                    List<String> urls = urlMap.putIfAbsent("url", new ArrayList<>());
                    if(urls == null){
                        List<String> urls1 = urlMap.get("url");
                        urls1.add(data);
                    }else{
                        urls.add(data);
                    }
                    List<String> roomIds = roomIdMap.putIfAbsent(data, new ArrayList<>());
                    if(roomIds == null){
                        List<String> roomIds1 = roomIdMap.get(data);
                        roomIds1.add(new String(childData1.getData()));
                    }else{
                        roomIds.add(new String(childData1.getData()));
                    }
                }
                routeDefinitionWriterAndReader.refreshRoutes();
                break;
            case NODE_DELETED:
                log.info("删除节点");
                log.info("删除节点的路径"+childData.getPath());
                log.info("删除节点的数据：" + new String(childData.getData()));
                String path = childData1.getPath().replace("/lazyer/pkNode/", "");
                List<String> urlList = urlMap.getOrDefault("url", new ArrayList<>());
                if(urlList != null && urlList.size() > 0){
                    urlList.remove(path);
                }
                roomIdMap.remove(path);
                break;
            default:
                break;
        }
    }
}
