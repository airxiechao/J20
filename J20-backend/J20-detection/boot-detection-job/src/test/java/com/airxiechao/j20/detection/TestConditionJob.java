package com.airxiechao.j20.detection;

import com.airxiechao.j20.DetectionTaskJobBoot;
import com.airxiechao.j20.common.api.pojo.constant.ConstEventLevel;
import com.airxiechao.j20.common.api.pojo.constant.ConstFieldOperator;
import com.airxiechao.j20.common.kafka.KafkaAdmin;
import com.airxiechao.j20.common.minio.MinioHelper;
import com.airxiechao.j20.detection.api.pojo.config.JobConfig;
import com.airxiechao.j20.detection.api.pojo.constant.*;
import com.airxiechao.j20.detection.api.pojo.event.SchemaField;
import com.airxiechao.j20.detection.api.pojo.rule.*;
import com.airxiechao.j20.detection.api.pojo.task.Task;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 测试条件规则
 */
public class TestConditionJob {
    public static void main(String[] args) throws Exception {
        Task task = new Task();
        task.setId("ttt" + System.currentTimeMillis());
        task.setName("任务ttt");
        List<Rule> rules = new ArrayList<>();
        task.setRules(rules);

        // rule

        rules.add(buildRule1());
//        rules.add(buildRule2());

        // config

        JobConfig config = new JobConfig();
        config.setCheckpointingEnabled(false);
        config.setCheckpointingInterval(30 * 1000L);
        config.setSrcTopic("test3");
        config.setDstTopic("test4");
        config.setStartingOffsetStrategy(ConstRuleSrcOffsetStartingStrategy.EARLIEST);
        config.setSrcParallelism(1);
        config.setSrcKafkaBootstrapServers("127.0.0.1:9092");
        config.setDstKafkaBootstrapServers("127.0.0.1:9092");
        config.setMinioEndpoint("http://127.0.0.1:9000");
        config.setMinioAccessKey("minioadmin");
        config.setMinioSecretKey("minioadmin");
        config.setMinioBucket("j20");
        config.setLogSortField("timestamp");

        // 创建kafka topic
        try (KafkaAdmin kafkaAdmin = new KafkaAdmin("127.0.0.1:9092")) {
            if (!kafkaAdmin.hasTopic("test3")) {
                kafkaAdmin.createTopic("test3", 1, 1);
            }
            if (!kafkaAdmin.hasTopic("test4")) {
                kafkaAdmin.createTopic("test4", 1, 1);
            }
        }

        // 将 rule 传到 minio 存储
        String taskFilePath = String.format("/task/%s", task.getId());
        try (InputStream inputStream = new ByteArrayInputStream(
                JSON.toJSONString(task, JSONWriter.Feature.PrettyFormat).getBytes(StandardCharsets.UTF_8))) {
            MinioHelper minioHelper = new MinioHelper(
                    config.getMinioEndpoint(),
                    config.getMinioAccessKey(),
                    config.getMinioSecretKey(),
                    config.getMinioBucket()
            );
            minioHelper.upload(taskFilePath, inputStream);
        }

        config.setJobFilePath(taskFilePath);

        String configParam = URLEncoder.encode(JSON.toJSONString(config), "UTF-8");

        String[] params = {
                "--config",
                configParam
        };

        DetectionTaskJobBoot.main(params);
    }

    private static Rule buildRule1() {
        Rule rule = new Rule();

        rule.setProtocol("TCP");

        RuleOutput outputEvent = new RuleOutput();
        outputEvent.setEventTypeId("a001b001c001");
        outputEvent.setEventTypeName("请求统计");
        outputEvent.setLevel(ConstEventLevel.ALERT);
        outputEvent.setMessageTemplate("{{srcIp}}访问了{{dstIp}}：{{windowOut}}次");
        rule.setOutput(outputEvent);

        List<SchemaField> property = new ArrayList<>();
        property.add(new SchemaField("srcIp", "srcIp"));
        property.add(new SchemaField("dstIp", "dstIp"));
        outputEvent.setPropertyFields(property);
        rule.setCriteriaType(ConstRuleCriteriaType.CONDITION);

        RuleCriteriaCondition criteriaStatistics = new RuleCriteriaCondition();
        criteriaStatistics.setFilter(
                new RuleFilterNode(
                        ConstRuleFilterNodeType.GROUP,
                        ConstRuleGroupOperator.AND,
                        null, null, List.of(
                                new RuleFilterNode(
                                        ConstRuleFilterNodeType.FIELD,
                                        ConstFieldOperator.EXISTS,
                                        "a", null, null))
                )
        );

        criteriaStatistics.setStatistics(new RuleStatistics() {{
            setEnabled(false);
            setWindow(new RuleSlideWindow() {{
                setSize(1L);
                setSizeUnit(ConstTimeUnit.MINUTE);
                setSlide(1L);
                setSlideUnit(ConstTimeUnit.MINUTE);
            }});
            setField("srcIp");
            setAggregate(ConstRuleStatisticsAggregate.COUNT);
            setGroupBy(new String[]{"srcIp"});
            setOperator(ConstFieldOperator.GREATER_THAN);
            setValue(0.0);
            setEager(true);
        }});
        rule.setCriteria(JSON.parseObject(JSON.toJSONString(criteriaStatistics)));

        RuleAggregation aggregation = new RuleAggregation();
        aggregation.setEnabled(false);
        aggregation.setTime(1L);
        aggregation.setUnit(ConstTimeUnit.MINUTE);
        aggregation.setEmitWhich(ConstEmitWhich.FIRST);
        rule.setAggregation(aggregation);

        return rule;
    }

    private static Rule buildRule2() {
        Rule rule = new Rule();

        rule.setProtocol("TCP");

        RuleOutput outputEvent = new RuleOutput();
        outputEvent.setEventTypeId("a001b001c002");
        outputEvent.setEventTypeName("连接统计");
        outputEvent.setLevel(ConstEventLevel.ALERT);
        outputEvent.setMessageTemplate("{{srcIp}}访问了{{dstIp}}：{{windowOut}}次");
        rule.setOutput(outputEvent);

        List<SchemaField> property = new ArrayList<>();
        property.add(new SchemaField("srcIp", "srcIp"));
        property.add(new SchemaField("dstIp", "dstIp"));
        outputEvent.setPropertyFields(property);
        rule.setCriteriaType(ConstRuleCriteriaType.CONDITION);

        RuleCriteriaCondition criteriaStatistics = new RuleCriteriaCondition();
        criteriaStatistics.setFilter(
                new RuleFilterNode(
                        ConstRuleFilterNodeType.GROUP,
                        ConstRuleGroupOperator.AND,
                        null, null, null
                )
        );

        criteriaStatistics.setStatistics(new RuleStatistics() {{
            setEnabled(true);
            setWindow(new RuleSlideWindow() {{
                setSize(1L);
                setSizeUnit(ConstTimeUnit.MINUTE);
                setSlide(1L);
                setSlideUnit(ConstTimeUnit.MINUTE);
            }});
            setField("srcIp");
            setAggregate(ConstRuleStatisticsAggregate.COUNT);
            setGroupBy(new String[]{"srcIp", "dstIp"});
            setOperator(ConstFieldOperator.GREATER_THAN);
            setValue(0.0);
            setEager(false);
        }});
        rule.setCriteria(JSON.parseObject(JSON.toJSONString(criteriaStatistics)));

        RuleAggregation aggregation = new RuleAggregation();
        aggregation.setEnabled(false);
        aggregation.setTime(1L);
        aggregation.setUnit(ConstTimeUnit.MINUTE);
        aggregation.setEmitWhich(ConstEmitWhich.FIRST);
        rule.setAggregation(aggregation);

        return rule;
    }
}
