package com.airxiechao.j20.common.cache;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务缓存的实现。缓存某个服务对象的所有方法的返回结果
 */
@Slf4j
public class ServiceCache {
    /**
     * 缓存池
     */
    private ConcurrentHashMap<String, ExpireCacheItem> cacheMap = new ConcurrentHashMap<>();

    /**
     * 过期秒数
     */
    private int expireSeconds;

    /**
     * 构造服务缓存
     * @param expireSeconds 过期秒数
     */
    public ServiceCache(int expireSeconds) {
        this.expireSeconds = expireSeconds;
    }

    /**
     * 返回服务对象的代理对象。代理对象将代理服务对象的方法调用，并实现对方法返回结果的缓存
     * @param object 服务对象
     * @return 代理对象
     * @param <T> 服务对象的类型
     */
    public <T> T get(T object) {
        T proxyInstance = (T) Proxy.newProxyInstance(
                ServiceCache.class.getClassLoader(),
                object.getClass().getInterfaces(),
                (proxy, method, args) -> invoke(object, method, args));

        return proxyInstance;
    }

    /**
     * 代理对象实现方法调用，并实现对结果的缓存
     * @param object 服务对象
     * @param method 调用方法
     * @param args 方法参数
     * @return 方法返回结果
     */
    private Object invoke(Object object, Method method, Object[] args) {
        String cacheName = buildCacheName(method, args);
        Object value = getCache(cacheName);
        if(null != value){
            log.info("命中服务缓存：{}", cacheName);
            return value;
        }

        try {
            value = method.invoke(object, args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        putCache(cacheName, value);
        return value;
    }

    /**
     * 放入缓存
     * @param cacheName 缓存名称
     * @param value 缓存数据
     */
    private void putCache(String cacheName, Object value){
        log.info("放入服务缓存：{}", cacheName);
        cacheMap.put(
                cacheName,
                new ExpireCacheItem(value, expireSeconds)
        );
    }

    /**
     * 读取缓存
     * @param cacheName 缓存名称
     * @return 缓存数据，如果存在。null，如果不存在
     */
    private Object getCache(String cacheName){
        ExpireCacheItem cache = cacheMap.get(cacheName);
        if(null != cache && !cache.isExpire()){
            return cache.getItem();
        }

        cacheMap.remove(cacheName);
        return null;
    }

    /**
     * 构造某个调用的缓存名称
     * @param method 调用的方法
     * @param args 调用的参数
     * @return 缓存名称
     */
    private String buildCacheName(Method method, Object[] args){
        StringBuilder sb = new StringBuilder();
        sb.append(method.getDeclaringClass().getName());
        sb.append(".");
        sb.append(method.getName());
        if(null != args){
            for (Object arg : args) {
                sb.append("|");
                sb.append(arg);
            }
        }
        String cacheName = sb.toString();
        return cacheName;
    }
}
