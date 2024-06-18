package com.airxiechao.j20.detection.job;

import com.airxiechao.j20.common.api.pojo.event.Event;
import com.airxiechao.j20.common.api.pojo.log.Log;
import com.airxiechao.j20.detection.api.pojo.config.JobConfig;
import com.airxiechao.j20.detection.api.pojo.rule.Rule;
import com.airxiechao.j20.detection.api.pojo.task.Task;
import com.airxiechao.j20.detection.job.common.EventSerializer;
import com.airxiechao.j20.detection.job.common.MultipleOutputStream;
import com.airxiechao.j20.detection.util.SortUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.RuntimeExecutionMode;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.time.Duration;
import java.util.List;

import static org.apache.flink.api.common.eventtime.TimestampAssigner.NO_TIMESTAMP;

/**
 * 规则测试任务的执行器
 */
@Slf4j
public class RuleJobRunner {
    protected Rule rule;
    protected JobConfig config;
    private List<Log> input;

    public RuleJobRunner(Rule rule, JobConfig config, List<Log> input){
        this.rule = rule;
        this.config = config;
        this.input = input;
    }

    /**
     * 执行
     * @throws Exception 执行异常
     */
    public void run() throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // 批处理模式
        env.setRuntimeMode(RuntimeExecutionMode.BATCH);

        String logSortField = config.getLogSortField();

        // 输入
        DataStreamSource<Log> stream = new DataStreamSource<>(env.fromCollection(input)
                .assignTimestampsAndWatermarks(
                        WatermarkStrategy
                                .<Log>forBoundedOutOfOrderness(Duration.ofSeconds(10))
                                .withTimestampAssigner((log, timestamp) -> {
                                    // 设置输入时间为日志的排序时间
                                    Long logTimestamp = SortUtil.getLogSortValue(log, logSortField);
                                    if(null == logTimestamp){
                                        return NO_TIMESTAMP;
                                    }

                                    return logTimestamp;
                                })
                )
                .setParallelism(1));

        // 识别
        MultipleOutputStream output = recognizeSingleCriteria(stream, rule);
        SingleOutputStreamOperator<Event> eventOutput = output.getEventOutputStream();

        // 输出
        if(null != eventOutput) {
            // sink
            KafkaSink<Event> eventKafkaSink = createKafkaSink(config.getDstKafkaBootstrapServers(), config.getDstTopic());
            eventOutput.sinkTo(eventKafkaSink).name("Event Sink");
        }

        env.execute(String.format("%s#%s#%s", getClass().getName(), rule.getId(), rule.getName()));
    }

    /**
     * 识别单个规则
     * @param stream 日志流
     * @param rule 规则
     * @return 多输出流
     */
    public MultipleOutputStream recognizeSingleCriteria(DataStreamSource<Log> stream, Rule rule) {
        Task dummyTask = new Task();
        dummyTask.setRules(List.of(rule));

        AbstractTaskJob taskJob = TaskJobFactory.getInstance().createJob(dummyTask, config, rule);
        return taskJob.recognizeSingleCriteria(stream, rule);
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
