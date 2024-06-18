package com.airxiechao.j20.detection.db.record;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;

/**
 * Kafka 数据源记录
 */
@Data
@Entity(name = "t_detection_datasource")
public class KafkaDataSourceRecord {
    /**
     * ID
     */
    @Id
    private String id;

    /**
     * 名称
     */
    @Column(unique = true)
    private String name;

    /**
     * 服务器地址
     */
    private String bootstrapServers;

    /**
     * 主题
     */
    private String topic;

    /**
     * 分区数
     */
    private Integer numPartition;
}