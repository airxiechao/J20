package com.airxiechao.j20.probe.common.kafka;

import org.apache.kafka.clients.admin.*;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

/**
 * Kafka的管理客户端实现
 */
public class KafkaAdmin implements Closeable {

    private AdminClient adminClient;

    /**
     * 构造Kafka管理客户端
     * @param bootstrapServers Kafka地址
     */
    public KafkaAdmin(String bootstrapServers) {
        Properties props = new Properties();
        props.setProperty(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        adminClient = KafkaAdminClient.create(props);
    }

    /**
     * 创建 Topic
     * @param name Topic 名称
     * @param numPartitions 分区数
     * @param replicationFactor 副本数
     */
    public void createTopic(String name, int numPartitions, int replicationFactor){
        NewTopic newTopic = new NewTopic(name, numPartitions, (short)replicationFactor);
        CreateTopicsResult res = adminClient.createTopics(List.of(newTopic));
    }

    /**
     * 是否存在 Topic
     * @param name Topic 名称
     * @return 是否存在
     */
    public  boolean hasTopic(String name){
        DescribeTopicsResult res = adminClient.describeTopics(List.of(name));
        try {
            res.allTopicNames().get();
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    /**
     * 删除 Topic
     * @param name Topic 名称
     */
    public void deleteTopic(String name){
        DeleteTopicsResult res = adminClient.deleteTopics(List.of(name));
    }

    /**
     * 关闭客户端
     * @throws IOException 关闭异常
     */
    @Override
    public void close() throws IOException {
        adminClient.close();
    }
}
