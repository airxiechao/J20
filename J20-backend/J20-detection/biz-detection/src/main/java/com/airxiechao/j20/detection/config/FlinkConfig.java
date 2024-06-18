package com.airxiechao.j20.detection.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix = "flink")
@Data
public class FlinkConfig {
    /**
     * 主机地址
     */
    private String address;

    /**
     * 客户端超时毫秒数
     */
    private Integer timeoutMillis = 60000;

    /**
     * 检测任务的Jar包名称
     */
    private String detectionJobJarName = "j20-detection-job-boot.jar";

    /**
     * 检测任务的入口Class类名
     */
    private String detectionTaskJobJarEntryClass = "com.airxiechao.j20.DetectionTaskJobBoot";

    /**
     * 检测规则测试任务的入口Class类名
     */
    private String detectionRuleJobJarEntryClass = "com.airxiechao.j20.DetectionRuleJobBoot";

    /**
     * 配置flink的 allowNonRestoredState
     */
    private Boolean allowNonRestoredState = false;

    /**
     * 配置flink的 savePointPath
     */
    private String savePointPath = null;

    /**
     * flink 输入源的并行度
     */
    private Integer parallelism = null;

    /**
     * 是否启用flink的 checkpointing
     */
    private Boolean checkpointingEnabled = false;

    /**
     * 配置flink的 checkpointingInterval
     */
    private Long checkpointingInterval = 30000L;
}
