package com.airxiechao.j20.detection.listener;

import com.airxiechao.j20.common.util.ApplicationContextUtil;
import com.airxiechao.j20.detection.config.StartupConfig;
import lombok.Getter;

/**
 * 系统启动监听器配置工厂
 */
public class StartupConfigFactory {

    @Getter
    private static final StartupConfigFactory instance = new StartupConfigFactory();
    private StartupConfigFactory(){}

    private StartupConfig config;

    public StartupConfig get() {
        if(null == config){
            this.config = load();
        }

        return config;
    }

    private StartupConfig load(){
        return ApplicationContextUtil.getContext().getBean(StartupConfig.class);
    }
}
