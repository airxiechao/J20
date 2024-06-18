package com.airxiechao.j20.detection.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 启动配置
 */
@Configuration
@ConfigurationProperties(prefix = "startup")
@Data
public class StartupConfig {
    /**
     * 是否启用任务监视器
     */
    private boolean taskJobMonitorEnabled = true;

    /**
     * 是否启用规则监视器
     */
    private boolean ruleMonitorEnabled = true;

    /**
     * 是否启用事件存储器
     */
    private boolean eventWarehouseEnabled = true;

    /**
     * 是否启用事件清理器
     */
    private boolean eventWarehouseCleanerEnabled = true;

}
