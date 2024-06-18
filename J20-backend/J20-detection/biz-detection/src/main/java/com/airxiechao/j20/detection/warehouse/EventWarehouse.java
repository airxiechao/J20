package com.airxiechao.j20.detection.warehouse;

import com.airxiechao.j20.common.api.pojo.event.Event;
import com.airxiechao.j20.common.es.EsClient;
import com.airxiechao.j20.common.kafka.KafkaAdmin;
import com.airxiechao.j20.common.kafka.KafkaClient;
import com.airxiechao.j20.detection.api.service.IEventService;
import com.airxiechao.j20.detection.es.EsConfigFactory;
import com.airxiechao.j20.detection.es.EsManager;
import com.airxiechao.j20.detection.kafka.KafkaConfigFactory;
import com.airxiechao.j20.detection.kafka.KafkaManager;
import com.airxiechao.j20.detection.service.EventService;
import com.airxiechao.j20.detection.util.SpeedCounter;
import com.alibaba.fastjson2.JSON;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 事件存储器。
 * 负责批量读取一批事件，按并行度进行切分，通过线程池并行存储切分出来的小份。
 */
@Slf4j
public class EventWarehouse {
    @Getter
    private static final EventWarehouse instance = new EventWarehouse();
    private EventWarehouse(){}

    private String eventTopic = KafkaConfigFactory.getInstance().get().getEventTopic();
    private Integer parallelism = EventWarehouseConfigFactory.getInstance().get().getParallelism();
    private Integer batchSize = EventWarehouseConfigFactory.getInstance().get().getBatchSize();
    private Long offsetTimestamp = EventWarehouseConfigFactory.getInstance().get().getOffsetTimestamp();
    private Integer timeoutDelaySec = EventWarehouseConfigFactory.getInstance().get().getTimeoutDelaySecs();
    private String eventIndexPrefix = EsConfigFactory.getInstance().get().getEventIndexPrefix();
    private String eventOriginalLogIndexPrefix = EsConfigFactory.getInstance().get().getEventOriginalLogIndexPrefix();

    private IEventService eventService = new EventService();

    private KafkaClient kafkaClient;
    private ExecutorService executor;

    /**
     * 启动
     */
    public void start(){
        log.info("启动事件存储器，并行度：{}， BATCH：{}", parallelism, batchSize);

        executor = Executors.newFixedThreadPool(Math.max(1, parallelism));

        // 检查仓库
        checkWarehouse();

        // 启动事件存储线程
        Thread thread = new Thread(() -> {
            try{
                // 计数器
                SpeedCounter counter = new SpeedCounter();
                AtomicLong writeMinute = new AtomicLong(0);

                List<Event> events = new ArrayList<>();

                // 读取事件
                kafkaClient = KafkaManager.getInstance().getClient(eventTopic, offsetTimestamp, EventWarehouse.class.getName());
                kafkaClient.consumeBatch(rs -> {
                    for (ConsumerRecord<String, String> r : rs) {
                        String strEvent = r.value();
                        Event event = JSON.parseObject(strEvent, Event.class);
                        normalizeEvent(event);
                        events.add(event);
                    }

                    counter.addNum(rs.count());

                    // 当前时间
                    long millis = System.currentTimeMillis();
                    long minute = millis / (60 * 1000);
                    boolean minutePassed = minute != writeMinute.get();

                    // 存储事件
                    if(minutePassed || events.size() >= batchSize){
                        try{
                            AtomicBoolean timeout = new AtomicBoolean(false);

                            // 分组存储
                            Map<String, List<Event>> bulks = splitBatch(events, parallelism);
                            if(!bulks.isEmpty()){
                                CountDownLatch latch = new CountDownLatch(bulks.size());
                                for (Map.Entry<String, List<Event>> entry : bulks.entrySet()) {
                                    String key = entry.getKey();
                                    List<Event> list = entry.getValue();
                                    executor.submit(() -> {
                                        try{
                                            eventService.bulkAdd(list);
                                        }catch (Exception e){
                                            log.error("事件分组[分组：{}, 数量：{}]存储发生错误", key, list.size(), e);
                                            if(e instanceof SocketTimeoutException){
                                                timeout.set(true);
                                            }
                                        }finally {
                                            latch.countDown();
                                        }
                                    });
                                }
                                latch.await();
                            }

                            // 超时后等待
                            if(timeout.get()){
                                log.info(String.format("事件存储[总数量：%d]发生超时，等待[%d]秒", events.size(), timeoutDelaySec));

                                try {
                                    Thread.sleep(timeoutDelaySec*1000);
                                } catch (Exception ignored) {}
                            }
                        }catch (Exception e){
                            log.error("事件存储发生错误", e);
                        } finally {
                            events.clear();
                            writeMinute.set(minute);
                        }
                    }

                    // 统计速度
                    if(minutePassed){
                        if(counter.getMillis() > 0) {
                            double dps = counter.getDps(millis);
                            log.info("事件存储速度：每秒[{}]条", dps);
                        }

                        counter.reset(millis);
                    }
                });
            }catch (Exception e){
                log.error("事件存储器发生错误", e);

                try {
                    stop();
                } catch (IOException ignored) {}

                try {
                    Thread.sleep(60*1000);
                } catch (InterruptedException ignored) {}

                start();
            }
        });
        thread.setName("事件存储器");
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * 停止
     * @throws IOException 停止异常
     */
    public void stop() throws IOException {
        if(null != kafkaClient){
            kafkaClient.close();
        }

        if(null != executor) {
            executor.shutdown();
        }
    }

    /**
     * 检查存储仓库
     */
    private void checkWarehouse(){

        // 检查事件的 Kafka 队列
        try(KafkaAdmin kafkaAdmin = KafkaManager.getInstance().getAdmin()) {
            if (!kafkaAdmin.hasTopic(eventTopic)) {
                kafkaAdmin.createTopic(eventTopic, parallelism, 1);
            }
        } catch (Exception e){
            throw new RuntimeException(e);
        }

        // 检查事件的 ES 索引模板
        EsClient esClient = EsManager.getInstance().getClient();
        if(!esClient.hasIndexTemplate(eventIndexPrefix)) {
            try {
                esClient.createOrUpdateIndexTemplate(eventIndexPrefix, new HashMap() {{
                    put("index_patterns", Arrays.asList(String.format("%s-*", eventIndexPrefix)));
                    put("template", new HashMap() {{
                        put("mappings", new HashMap() {{
                            put("properties", new HashMap() {{
                                put("originalLog", new HashMap() {{
                                    put("type", "object");
                                    put("enabled", false);
                                }});
                            }});
                        }});
                    }});
                }});
            } catch (Exception e) {
                log.error("创建或更新事件索引模板发生错误", e);
            }
        }

        // 检查事件原始日志的 ES 索引模板
        if(!esClient.hasIndexTemplate(eventOriginalLogIndexPrefix)) {
            try {
                esClient.createOrUpdateIndexTemplate(eventOriginalLogIndexPrefix, new HashMap() {{
                    put("index_patterns", Arrays.asList(String.format("%s-*", eventOriginalLogIndexPrefix)));
                    put("template", new HashMap() {{
                        put("mappings", new HashMap() {{
                            put("properties", new HashMap() {{
                                put("log", new HashMap() {{
                                    put("type", "object");
                                    put("enabled", false);
                                }});
                            }});
                        }});
                    }});
                }});
            } catch (Exception e) {
                log.error("创建或更新事件原始日志索引模板发生错误", e);
            }
        }
    }

    /**
     * 规范化事件内容
     * @param event 事件
     */
    private void normalizeEvent(Event event){
        // 设置时间戳
        event.setTimestamp(System.currentTimeMillis());
    }

    /**
     * 分割事件序列
     * @param events 事件序列
     * @param parallelism 并行度
     * @return 切分出来事件序列
     */
    private Map<String, List<Event>> splitBatch(List<Event> events, int parallelism){
        Map<String, List<Event>> bulks = new HashMap<>();

        int size = events.size() / parallelism;
        if(size == 0){
            size = events.size();
        }

        for (int i = 0; i < events.size(); i++) {
            Event event = events.get(i);
            try{
                String key = String.valueOf(i / size);

                List<Event> list = bulks.computeIfAbsent(key, k -> new ArrayList<>());

                list.add(event);
            }catch (Exception e){
                log.error("事件存储器切分事件发生错误", e);
            }
        }

        return bulks;
    }
}
