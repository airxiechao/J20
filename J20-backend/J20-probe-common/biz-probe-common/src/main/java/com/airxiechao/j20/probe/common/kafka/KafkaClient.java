package com.airxiechao.j20.probe.common.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndTimestamp;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;

import java.io.Closeable;
import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.function.Consumer;

/**
 * Kafka 消费/生成客户端的实现
 */
public class KafkaClient implements Closeable {

    /**
     * 目的 Topic
     */
    private String topics;

    /**
     * 消费的起始偏移时间戳
     */
    private Long offsetTimestamp;

    /**
     * 停止标识
     */
    private volatile boolean flagStop;

    /**
     * 消费者
     */
    private KafkaConsumer<String, String> kafkaConsumer;

    /**
     * 生产者
     */
    private KafkaProducer<String, String> kafkaProducer;

    /**
     * 构造客户端。默认初始偏移时间戳为空
     * @param bootstrapServers Kafka 地址
     * @param topics Topic 名称。多个用英文逗号连接
     */
    public KafkaClient(String bootstrapServers, String topics) {
        this(bootstrapServers, topics, null, null);
    }

    /**
     * 构造客户端。默认初始偏移时间戳为空
     * @param bootstrapServers Kafka 地址
     * @param topics Topic 名称。多个用英文逗号连接
     * @param groupId 客户端组标识
     */
    public KafkaClient(String bootstrapServers, String topics, String groupId) {
        this(bootstrapServers, topics, null, groupId);
    }

    /**
     * 构造客户端
     * @param bootstrapServers Kafka 地址
     * @param topics Topic 名称。多个用英文逗号连接
     * @param offsetTimestamp 初始偏移时间戳
     * @param groupId 客户端组标识
     */
    public KafkaClient(String bootstrapServers, String topics, Long offsetTimestamp, String groupId) {
        this.topics = topics;
        this.offsetTimestamp = offsetTimestamp;

        Properties props = new Properties();
        props.setProperty("bootstrap.servers", bootstrapServers);
        if(null != groupId && !groupId.isBlank()) {
            props.setProperty("group.id", groupId);
            props.setProperty("auto.offset.reset", "earliest");
            props.setProperty("enable.auto.commit", "true");
            props.setProperty("auto.commit.interval.ms", "1000");
        }
        props.setProperty("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.setProperty("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        this.kafkaProducer = new KafkaProducer<>(props);
        this.kafkaConsumer = new KafkaConsumer<>(props);
    }

    /**
     * 写入数据
     * @param key 数据键
     * @param value 数据值
     */
    public void produce(String key, String value){
        kafkaProducer.send(new ProducerRecord<>(topics.split(",")[0], key, value));
        kafkaProducer.flush();
    }

    /**
     * 消费数据，阻塞线程，直到停止标识置为 true
     * @param consumer 消费函数
     */
    public void consume(Consumer<ConsumerRecord<String, String>> consumer){

        kafkaConsumer.subscribe(Arrays.asList(topics.split(",")));

        if(null != offsetTimestamp){
            seek(offsetTimestamp);
        }

        this.flagStop = false;
        while (!flagStop){
            ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofMillis(1000));
            for (ConsumerRecord<String, String> record : records){
                consumer.accept(record);
            }
        }
    }

    /**
     * 批量消费数据，阻塞线程，直到停止标识置为 true
     * @param consumer 批量消费函数
     */
    public void consumeBatch(Consumer<ConsumerRecords<String, String>> consumer){

        kafkaConsumer.subscribe(Arrays.asList(topics.split(",")));

        if(null != offsetTimestamp){
            seek(offsetTimestamp);
        }

        this.flagStop = false;
        while (!flagStop){
            ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofMillis(1000));
            consumer.accept(records);
        }
    }

    /**
     * 消费数据，阻塞线程，直到 Topic 为空
     * @param consumer 消费函数
     */
    public void consumeUtilEmpty(Consumer<ConsumerRecord<String, String>> consumer){

        kafkaConsumer.subscribe(Arrays.asList(topics.split(",")));

        if(null != offsetTimestamp){
            seek(offsetTimestamp);
        }

        while (true){
            ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofMillis(1000));
            if(records.isEmpty()){
                break;
            }

            for (ConsumerRecord<String, String> record : records){
                consumer.accept(record);
            }
        }
    }

    /**
     * 关闭客户端
     * @throws IOException 关闭异常
     */
    @Override
    public void close() throws IOException {
        this.flagStop = true;
        this.kafkaProducer.close();
        this.kafkaConsumer.close();
    }

    /**
     * 调整位置
     * @param timestamp 位置时间戳
     */
    private void seek(long timestamp){
        Set<TopicPartition> assignment = new HashSet<>();
        while(assignment.isEmpty()){
            kafkaConsumer.poll(Duration.ofMillis(1000));
            assignment = kafkaConsumer.assignment();
        }

        Map<TopicPartition, Long> times = new HashMap<>();
        for (TopicPartition partition : assignment) {
            times.put(partition, timestamp);
        }

        Map<TopicPartition, OffsetAndTimestamp> offsets = kafkaConsumer.offsetsForTimes(times);

        for (TopicPartition partition : assignment) {
            OffsetAndTimestamp offset = offsets.get(partition);
            if(null != offset){
                kafkaConsumer.seek(partition, offset.offset());
            }
        }
    }
}
