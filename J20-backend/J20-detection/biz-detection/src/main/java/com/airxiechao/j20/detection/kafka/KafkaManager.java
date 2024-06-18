package com.airxiechao.j20.detection.kafka;

import com.airxiechao.j20.common.kafka.KafkaAdmin;
import com.airxiechao.j20.common.kafka.KafkaClient;
import lombok.Getter;

/**
 * Kafka 客户端管理器实现。
 * 负责创建Kafka管理端/客户端。
 */
public class KafkaManager {
    @Getter
    public static final KafkaManager instance = new KafkaManager();
    private KafkaManager(){}

    private static String bootstrapServers = KafkaConfigFactory.getInstance().get().getBootstrapServers();

    public KafkaClient getClient(String topic, String groupId){
        return getClient(topic, null, groupId);
    }

    public KafkaClient getClient(String topic, Long offsetTimestamp, String groupId){
        return new KafkaClient(bootstrapServers, topic, offsetTimestamp, groupId);
    }

    public KafkaAdmin getAdmin(){
        return new KafkaAdmin(bootstrapServers);
    }

    public KafkaAdmin getAdmin(String bootstrapServers){
        return new KafkaAdmin(bootstrapServers);
    }
}
