package com.airxiechao.j20.detection.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Minio配置
 */
@Configuration
@ConfigurationProperties(prefix = "minio")
@Data
public class MinioConfig {
    /**
     * 主机地址
     */
    private String endpoint;

    /**
     * 访问用户名
     */
    private String accessKey;

    /**
     * 访问密码
     */
    private String secretKey;

    /**
     * 存储桶
     */
    private String bucket = "j20";

}
