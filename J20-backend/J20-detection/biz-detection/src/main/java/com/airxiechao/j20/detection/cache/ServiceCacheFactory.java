package com.airxiechao.j20.detection.cache;

import com.airxiechao.j20.common.cache.ServiceCache;
import com.airxiechao.j20.detection.job.DetectionConfigFactory;
import lombok.Getter;

/**
 * 服务缓存工厂
 */
public class ServiceCacheFactory {
    @Getter
    private static final ServiceCacheFactory instance = new ServiceCacheFactory();

    private int expireSeconds = DetectionConfigFactory.getInstance().get().getEventStatisticsCacheExpireSeconds();
    private ServiceCache serviceCache;

    private ServiceCacheFactory() {
        serviceCache = new ServiceCache(expireSeconds);
    }

    /**
     * 获取服务的缓存代理对象
     * @param object 服务对象
     * @return 服务的缓存代理对象
     * @param <T> 服务类型
     */
    public <T> T get(T object) {
        return serviceCache.get(object);
    }

}
