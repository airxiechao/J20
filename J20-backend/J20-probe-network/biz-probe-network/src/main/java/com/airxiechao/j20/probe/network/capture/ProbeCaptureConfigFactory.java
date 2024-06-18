package com.airxiechao.j20.probe.network.capture;

import com.airxiechao.j20.probe.common.util.PropertyUtil;
import com.airxiechao.j20.probe.network.api.config.ProbeCaptureConfig;
import com.airxiechao.j20.probe.network.util.YamlUtil;
import lombok.Getter;

/**
 * 探针配置工厂
 */
public class ProbeCaptureConfigFactory {
    @Getter
    private static final ProbeCaptureConfigFactory instance = new ProbeCaptureConfigFactory();
    private ProbeCaptureConfigFactory(){}

    private ProbeCaptureConfig config;

    /**
     * 获取配置
     * @return 探针配置
     */
    public ProbeCaptureConfig get() {
        if(null == config){
            this.config = load();
        }

        return this.config;
    }

    /**
     * 加载配置
     * @return 探针配置
     */
    private ProbeCaptureConfig load() {
        String configFile = PropertyUtil.get("CONFIG", "probe-network.yml");

        ProbeCaptureConfig config;
        try {
            config = YamlUtil.load(configFile, ProbeCaptureConfig.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // 检查捕获配置
        if(null == config.getCaptures() || config.getCaptures().isEmpty()){
            throw new RuntimeException("捕获设置为空");
        }

        return config;
    }
}
