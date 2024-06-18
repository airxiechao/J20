package com.airxiechao.j20.detection.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 事件存储器配置
 */
@Configuration
@ConfigurationProperties(prefix = "event-warehouse")
@Data
public class EventWarehouseConfig {
    /**
     * 并行度。
     * 当从Kafka读到一批 batchSize 事件，分割成 parallelism 个小组，再通过 parallelism 个线程并行存储
     */
    private int parallelism = 1;

    /**
     * 一次读取事件的批量大小
     */
    private int batchSize = 1;

    /**
     * 读取事件的初始偏移时间戳
     */
    private Long offsetTimestamp = null;

    /**
     * 存储发生超时后的延迟秒数
     */
    private int timeoutDelaySecs = 30;

    /**
     * 事件保留天数
     */
    private Integer eventRetentionDays = null;
}
