package com.airxiechao.j20.detection.listener;

import com.airxiechao.j20.detection.monitor.TaskJobMonitor;
import com.airxiechao.j20.detection.warehouse.EventWarehouse;
import com.airxiechao.j20.detection.cleaner.EventWarehouseCleaner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * 系统启动监听器
 */
@Slf4j
@Component
public class StartupListener implements ApplicationListener<ApplicationReadyEvent> {

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        boolean eventWarehouseEnabled = StartupConfigFactory.getInstance().get().isEventWarehouseEnabled();
        boolean eventWarehouseCleanerEnabled = StartupConfigFactory.getInstance().get().isEventWarehouseCleanerEnabled();
        boolean taskJobMonitorEnabled = StartupConfigFactory.getInstance().get().isTaskJobMonitorEnabled();
        boolean ruleMonitorEnabled = StartupConfigFactory.getInstance().get().isRuleMonitorEnabled();

        // 启动事件存储器
        if (eventWarehouseEnabled) {
            EventWarehouse.getInstance().start();
        }

        // 启动事件清理器
        if (eventWarehouseCleanerEnabled){
            EventWarehouseCleaner.getInstance().start();
        }

        // 启动任务监视器
        if (taskJobMonitorEnabled) {
            TaskJobMonitor.getInstance().start();
        }

        // 启动规则监视器
        if (ruleMonitorEnabled) {

        }
    }
}
