package com.airxiechao.j20.detection.job;

import com.airxiechao.j20.common.api.pojo.event.Event;
import com.airxiechao.j20.common.api.pojo.log.Log;
import com.airxiechao.j20.detection.api.pojo.config.JobConfig;
import com.airxiechao.j20.detection.api.pojo.constant.ConstRuleSrcOffsetStartingStrategy;
import com.airxiechao.j20.detection.api.pojo.rule.Rule;
import com.airxiechao.j20.detection.api.pojo.task.Task;
import com.airxiechao.j20.detection.job.common.EventSerializer;
import com.airxiechao.j20.detection.job.common.LogDeserializer;
import com.airxiechao.j20.detection.util.SortUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.connector.kafka.source.reader.deserializer.KafkaRecordDeserializationSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.apache.flink.api.common.eventtime.TimestampAssigner.NO_TIMESTAMP;

/**
 * 任务执行器实现
 */
@Slf4j
public class TaskJobRunner {

    protected Task task;
    protected JobConfig config;

    public TaskJobRunner(Task task, JobConfig config){
        this.task = task;
        this.config = config;
    }

    /**
     * 执行任务
     * @throws Exception 任务执行异常
     */
    public void run() throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // checkpointing
        if(null != config.getCheckpointingEnabled() && config.getCheckpointingEnabled()){
            env.enableCheckpointing(config.getCheckpointingInterval());
        }

        String logSortField = config.getLogSortField();

        // 输入
        KafkaSource<Log> kafkaSource = createKafkaSource(config.getSrcKafkaBootstrapServers(), config.getSrcTopic(), config.getStartingOffsetStrategy());
        DataStreamSource<Log> stream = env.fromSource(
                kafkaSource,
                WatermarkStrategy
                        .<Log>forBoundedOutOfOrderness(Duration.ofSeconds(10))
                        .withTimestampAssigner((log, timestamp) -> {
                            // 设置输入时间为日志的排序时间
                            Long logTimestamp = SortUtil.getLogSortValue(log, logSortField);
                            return Objects.requireNonNullElse(logTimestamp, NO_TIMESTAMP);

                        })
                        .withIdleness(Duration.ofMinutes(1)),
                "Kafka Source")
        .setParallelism(config.getSrcParallelism());

        // 识别
        SingleOutputStreamOperator<Event> eventOutput = recognize(stream);

        // 输出
        if(null != eventOutput) {
            // sink
            KafkaSink<Event> eventKafkaSink = createKafkaSink(config.getDstKafkaBootstrapServers(), config.getDstTopic());
            eventOutput.sinkTo(eventKafkaSink).name("Kafka Sink");
        }

        env.execute(String.format("%s#%s#%s", getClass().getName(), task.getId(), task.getName()));
    }

    /**
     * 识别多组规则
     * @param stream 日志流
     * @return 输出流
     */
    protected SingleOutputStreamOperator<Event> recognize(DataStreamSource<Log> stream) {
        List<SingleOutputStreamOperator<Event>> eventStreams = task.getRules().stream()
                .map(rule -> recognizeSingleCriteria(stream, rule))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        SingleOutputStreamOperator<Event> eventStream = null;
        if(!eventStreams.isEmpty()){
            eventStream = eventStreams.get(0);
            if(eventStreams.size() > 1){
                eventStream = eventStream.union(eventStreams.subList(1, eventStreams.size()).toArray(new DataStream[0])).filter(value -> true);
            }
        }


        return eventStream;
    }

    /**
     * 识别单个规则
     * @param stream 日志流
     * @param rule 规则
     * @return 输出流
     */
    public SingleOutputStreamOperator<Event> recognizeSingleCriteria(DataStreamSource<Log> stream, Rule rule) {
        AbstractTaskJob taskJob = TaskJobFactory.getInstance().createJob(task, config, rule);
        return taskJob.recognizeSingleCriteria(stream, rule);
    }

    /**
     * 创建Kafka输入源
     * @param bootstrapServers Kafka地址
     * @param topic Kafka Topic
     * @param srcStartingOffsetStrategy 起始位置
     * @return Kafka输入源
     * @throws Exception 创建Kafka输入源异常
     */
    private KafkaSource<Log> createKafkaSource(String bootstrapServers, String topic, String srcStartingOffsetStrategy) throws Exception {
        OffsetResetStrategy offsetResetStrategy = OffsetResetStrategy.LATEST;
        if(StringUtils.isNotBlank(srcStartingOffsetStrategy) && ConstRuleSrcOffsetStartingStrategy.EARLIEST.equals(srcStartingOffsetStrategy)){
            offsetResetStrategy = OffsetResetStrategy.EARLIEST;
        }

        KafkaSource<Log> kafkaSource = KafkaSource.<Log>builder()
                .setBootstrapServers(bootstrapServers)
                .setTopics(topic)
                .setGroupId(String.format("%s#%s", this.getClass().getName(), task.getId()))
                .setStartingOffsets(OffsetsInitializer.committedOffsets(offsetResetStrategy))
                .setDeserializer(KafkaRecordDeserializationSchema.of(new LogDeserializer()))
                .build();

        return kafkaSource;
    }

    /**
     * 创建Kafka事件输出
     * @param bootstrapServers Kafka地址
     * @param topic Kafka Topic
     * @return Kafka事件输出
     * @throws Exception 创建Kafka事件输出异常
     */
    private KafkaSink<Event> createKafkaSink(String bootstrapServers, String topic) throws Exception {
        KafkaSink<Event> kafkaSink = KafkaSink.<Event>builder()
                .setBootstrapServers(bootstrapServers)
                .setRecordSerializer(KafkaRecordSerializationSchema.builder()
                        .setTopic(topic)
                        .setValueSerializationSchema(new EventSerializer())
                        .build())
                .setDeliverGuarantee(DeliveryGuarantee.NONE)
                .build();

        return kafkaSink;
    }

}
