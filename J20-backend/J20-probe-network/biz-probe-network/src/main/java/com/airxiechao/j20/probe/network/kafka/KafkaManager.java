package com.airxiechao.j20.probe.network.kafka;

import com.airxiechao.j20.probe.common.kafka.KafkaAdmin;
import com.airxiechao.j20.probe.common.kafka.KafkaClient;
import com.airxiechao.j20.probe.network.api.config.KafkaConfig;
import com.airxiechao.j20.probe.network.capture.ProbeCaptureConfigFactory;
import lombok.Getter;

/**
 * Kafka 管理器
 */
public class KafkaManager {
    @Getter
    private static final KafkaManager instance = new KafkaManager();
    private KafkaManager(){}

    /**
     * 获取客户端
     * @param bootstrapServers Kafka 服务器地址
     * @param topic 主题
     * @return Kafka 客户端
     */
    public KafkaClient getClient(String bootstrapServers, String topic){
        return new KafkaClient(bootstrapServers, topic);
    }

    /**
     * 获取管理端
     * @param bootstrapServers Kafka 服务器地址
     * @return Kafka 管理端
     */
    public KafkaAdmin getAdmin(String bootstrapServers){
        return new KafkaAdmin(bootstrapServers);
    }
}
