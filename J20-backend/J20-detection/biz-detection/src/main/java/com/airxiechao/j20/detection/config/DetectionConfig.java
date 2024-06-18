package com.airxiechao.j20.detection.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 检测出配置
 */
@Configuration
@ConfigurationProperties(prefix = "detection")
@Data
public class DetectionConfig {
    /**
     * 日志排序字段
     */
    private String logSortField = "timestamp";

    /**
     * 事件统计缓存过期事件
     */
    private int eventStatisticsCacheExpireSeconds = 60;

    /**
     * 任务监视间隔秒数
     */
    private int taskJobMonitorPeriodSecond = 10;
}
