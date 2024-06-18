package com.airxiechao.j20.common.jwt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * JWT 配置
 */
@Configuration
@ConfigurationProperties(prefix = "jwt")
@Data
public class JwtConfig {
    /**
     * 加密密钥
     */
    private String key;

    /**
     * 过期毫秒数
     */
    private Long expiration = 18 * 60 * 60 * 1000L;
}
