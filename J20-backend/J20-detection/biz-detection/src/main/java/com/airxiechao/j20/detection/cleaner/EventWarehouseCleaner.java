package com.airxiechao.j20.detection.cleaner;

import com.airxiechao.j20.common.util.TimeUtil;
import com.airxiechao.j20.detection.api.service.IEventService;
import com.airxiechao.j20.detection.service.EventService;
import com.airxiechao.j20.detection.warehouse.EventWarehouseConfigFactory;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 事件清理器
 * 定时清理超过存储期限的事件
 */
@Slf4j
public class EventWarehouseCleaner {
    @Getter
    private static final EventWarehouseCleaner instance = new EventWarehouseCleaner();

    private Integer retentionDays = EventWarehouseConfigFactory.getInstance().get().getEventRetentionDays();
    private IEventService eventService = new EventService();

    private ScheduledExecutorService scheduler;

    private EventWarehouseCleaner(){}

    public void start(){
        log.info("启动事件清理器");

        Date beginTime = TimeUtil.timeOfDays(new Date(), 1);
        long millisDelay = beginTime.getTime() - System.currentTimeMillis();
        getScheduler().scheduleAtFixedRate(this::clean, millisDelay, 24 * 60 * 60 * 1000, TimeUnit.MILLISECONDS);
    }

    public void clean(){
        try{
            // 查询保留期限
            if(null == retentionDays || retentionDays < 1){
                return;
            }

            // 清理过期事件
            Date untilTime = TimeUtil.timeOfDays(new Date(), -retentionDays);
            log.info("清理[{}]之前的事件", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(untilTime));

            eventService.deleteUntil(untilTime);
        }catch (Exception e){
            log.error("事件清理器发生错误", e);
        }
    }

    public void stop() throws IOException {
        scheduler.shutdownNow();
    }

    private ScheduledExecutorService getScheduler(){
        if(null == scheduler || scheduler.isShutdown()){
            scheduler = Executors.newSingleThreadScheduledExecutor((r) -> {
                Thread thread = new Thread(r);
                thread.setDaemon(true);
                return thread;
            });
        }

        return scheduler;
    }
}
