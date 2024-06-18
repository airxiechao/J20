package com.airxiechao.j20.detection.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Kafka配置
 */
@Configuration
@ConfigurationProperties(prefix = "kafka")
@Data
public class KafkaConfig {

    /**
     * 主机地址
     */
    private String bootstrapServers;

    /**
     * 默认日志 Topic
     */
    private String logTopic = "j20-detection-log";

    /**
     * 事件 Topic
     */
    private String eventTopic = "j20-detection-event";

}
