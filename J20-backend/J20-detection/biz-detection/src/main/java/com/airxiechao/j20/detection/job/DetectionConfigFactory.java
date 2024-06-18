package com.airxiechao.j20.detection.job;

import com.airxiechao.j20.common.util.ApplicationContextUtil;
import com.airxiechao.j20.detection.config.DetectionConfig;
import lombok.Getter;

/**
 * 检测配置工厂实现
 */
public class DetectionConfigFactory {
    @Getter
    private static DetectionConfigFactory instance = new DetectionConfigFactory();
    private DetectionConfigFactory(){}

    private DetectionConfig config;

    public DetectionConfig get(){
        if(null == config){
            this.config = load();
        }

        return this.config;
    }

    private DetectionConfig load() {
        return ApplicationContextUtil.getContext().getBean(DetectionConfig.class);
    }
}
