package com.airxiechao.j20.detection.api.pojo.datasource;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Kafka数据源
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class KafkaDataSource {
    /**
     * ID
     */
    private String id;

    /**
     * 名称
     */
    private String name;

    /**
     * 服务器地址
     */
    private String bootstrapServers;

    /**
     * Topic
     */
    private String topic;

    /**
     * 分区数
     */
    private Integer numPartition;

    public KafkaDataSource(String name, String bootstrapServers, String topic, Integer numPartition) {
        this.name = name;
        this.bootstrapServers = bootstrapServers;
        this.topic = topic;
        this.numPartition = numPartition;
    }
}
