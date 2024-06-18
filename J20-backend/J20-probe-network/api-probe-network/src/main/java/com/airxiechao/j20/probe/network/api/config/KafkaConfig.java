package com.airxiechao.j20.probe.network.api.config;

import lombok.Data;

/**
 * Kafka 配置
 */
@Data
public class KafkaConfig {
    /**
     * 服务器地址
     */
    private String bootstrapServers;

    /**
     * 主题名
     */
    private String topic;
}
