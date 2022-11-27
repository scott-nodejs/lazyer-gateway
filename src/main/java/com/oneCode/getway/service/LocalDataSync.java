package com.oneCode.getway.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lucong
 * @date 2022/10/29 12:25
 */
public class LocalDataSync implements IDataSync{

    public static Map<String, List<String>> urlMap = new ConcurrentHashMap<>();

    public static Map<String, List<String>> roomIdMap = new ConcurrentHashMap<>();

    @Override
    public List<String> getUrlList(String key) {
        return urlMap.get(key);
    }
}
