package com.airxiechao.j20.probe.network.api.config;

import lombok.Data;

import java.util.List;

/**
 * 探针配置
 */
@Data
public class ProbeCaptureConfig {
    /**
     * 捕获配置列表
     */
    private List<CaptureConfig> captures;
}
