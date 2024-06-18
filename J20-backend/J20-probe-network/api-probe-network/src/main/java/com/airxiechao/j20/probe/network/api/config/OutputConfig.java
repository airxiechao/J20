package com.airxiechao.j20.probe.network.api.config;

import lombok.Data;

/**
 * 输出配置
 */
@Data
public class OutputConfig {
    /**
     * Kafka 配置
     */
    private KafkaConfig kafka;
}
