package com.airxiechao.j20.detection.flink;

import com.airxiechao.j20.common.util.ApplicationContextUtil;
import com.airxiechao.j20.detection.config.FlinkConfig;
import lombok.Getter;

/**
 * Flink 配置工厂实现
 */
public class FlinkConfigFactory {

    @Getter
    private static final FlinkConfigFactory instance = new FlinkConfigFactory();
    private FlinkConfigFactory(){}

    private FlinkConfig config;

    public FlinkConfig get() {
        if(null == config){
            this.config = load();
        }

        return config;
    }

    private FlinkConfig load(){
        return ApplicationContextUtil.getContext().getBean(FlinkConfig.class);
    }
}
