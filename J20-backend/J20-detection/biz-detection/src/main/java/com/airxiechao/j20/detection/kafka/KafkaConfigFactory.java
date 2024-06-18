package com.airxiechao.j20.detection.kafka;

import com.airxiechao.j20.detection.config.KafkaConfig;
import com.airxiechao.j20.common.util.ApplicationContextUtil;
import lombok.Getter;

/**
 * Kafka 配置工厂实现
 */
public class KafkaConfigFactory {
    @Getter
    private static final KafkaConfigFactory instance = new KafkaConfigFactory();

    private KafkaConfig config;

    private KafkaConfigFactory(){}

    public KafkaConfig get(){
        if(null == config){
            this.config = load();
        }

        return this.config;
    }

    private KafkaConfig load() {
        return ApplicationContextUtil.getContext().getBean(KafkaConfig.class);
    }
}
