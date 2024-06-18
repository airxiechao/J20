package com.airxiechao.j20.detection.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * ES配置
 */
@Configuration
@ConfigurationProperties(prefix = "elasticsearch")
@Data
public class EsConfig {
    /**
     * 主机地址
     */
    private String hosts;

    /**
     * 用户名
     */
    private String user = null;

    /**
     * 密码
     */
    private String password = null;

    /**
     * 版本号：6、7、8
     */
    private Integer version = 8;

    /**
     * 客户端超时毫秒数
     */
    private Integer timeoutMillis = 90000;

    /**
     * 事件索引名称前缀
     */
    private String eventIndexPrefix = "j20-detection-event";

    /**
     * 事件原始日志索引名称前缀
     */
    private String eventOriginalLogIndexPrefix = "j20-detection-original-log";

    /**
     * 事件属性日志索引名称前缀
     */
    private String eventPropertyIndexPrefix = "j20-detection-property";

}
