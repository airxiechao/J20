package com.airxiechao.j20.detection.api.pojo.config;

import lombok.Data;

import java.io.Serializable;

/**
 * Flink中执行任务的配置对象
 */
@Data
public class JobConfig implements Serializable {
    private static final long serialVersionUID = 1L;

    // 任务配置文件路径
    private String jobFilePath;

    // flink

    /**
     * 是否启用Flink的checkpointing
     */
    private Boolean checkpointingEnabled;

    /**
     * 配置flink的 checkpointingInterval
     */
    private Long checkpointingInterval;

    /**
     * Flink读取数据源的并行度
     */
    private Integer srcParallelism;

    // kafka

    /**
     * 输入Kafka地址
     */
    private String srcKafkaBootstrapServers;

    /**
     * Kafka源Topic
     */
    private String srcTopic;

    /**
     * Kafka源起始读取位置
     */
    private String startingOffsetStrategy;

    /**
     * 输出Kafka地址
     */
    private String dstKafkaBootstrapServers;

    /**
     * 输出源的Topic
     */
    private String dstTopic;

    // minio

    /**
     * minio地址
     */
    private String minioEndpoint;

    /**
     * minio 访问用户名
     */
    private String minioAccessKey;

    /**
     * minio 访问密码
     */
    private String minioSecretKey;

    /**
     * minio 存储桶
     */
    private String minioBucket;

    // log

    /**
     * 日志排序字段
     */
    private String logSortField;
}
