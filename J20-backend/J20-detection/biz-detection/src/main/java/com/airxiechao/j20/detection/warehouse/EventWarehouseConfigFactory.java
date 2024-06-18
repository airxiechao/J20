package com.airxiechao.j20.detection.warehouse;

import com.airxiechao.j20.common.util.ApplicationContextUtil;
import com.airxiechao.j20.detection.config.EventWarehouseConfig;
import lombok.Getter;

/**
 * 事件存储器配置工厂实现
 */
public class EventWarehouseConfigFactory {
    @Getter
    private static final EventWarehouseConfigFactory instance = new EventWarehouseConfigFactory();
    private EventWarehouseConfigFactory(){}

    private EventWarehouseConfig config;

    public EventWarehouseConfig get(){
        if(null == config){
            this.config = load();
        }

        return this.config;
    }

    private EventWarehouseConfig load() {
        return ApplicationContextUtil.getContext().getBean(EventWarehouseConfig.class);
    }
}
