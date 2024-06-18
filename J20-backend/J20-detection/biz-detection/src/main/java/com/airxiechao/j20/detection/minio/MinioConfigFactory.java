package com.airxiechao.j20.detection.minio;

import com.airxiechao.j20.detection.config.MinioConfig;
import com.airxiechao.j20.common.util.ApplicationContextUtil;
import lombok.Getter;

/**
 * Minio配置工厂实现
 */
public class MinioConfigFactory {
    @Getter
    private static final MinioConfigFactory instance = new MinioConfigFactory();
    private MinioConfigFactory(){}

    private MinioConfig config;

    public MinioConfig get(){
        if(null == config){
            this.config = load();
        }

        return this.config;
    }

    private MinioConfig load() {
        return ApplicationContextUtil.getContext().getBean(MinioConfig.class);
    }
}
