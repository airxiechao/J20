package com.airxiechao.j20.probe.network.api.config;

import lombok.Data;

import java.util.Set;

/**
 * 捕获配置
 */
@Data
public class CaptureConfig {
    /**
     * 捕获设备 IP
     */
    private String ip;

    /**
     * 捕获协议集合
     */
    private Set<String> protocols;

    /**
     * 输出配置
     */
    private OutputConfig output;
}
