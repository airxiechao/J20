package com.airxiechao.j20.detection.es;

import com.airxiechao.j20.common.util.ApplicationContextUtil;
import com.airxiechao.j20.detection.config.EsConfig;
import lombok.Getter;

/**
 * ES配置工场实现
 */
public class EsConfigFactory {
    @Getter
    private static final EsConfigFactory instance = new EsConfigFactory();
    private EsConfigFactory(){}

    private EsConfig config;

    public EsConfig get(){
        if(null == config){
            this.config = load();
        }

        return this.config;
    }

    private EsConfig load() {
        return ApplicationContextUtil.getContext().getBean(EsConfig.class);
    }
}
