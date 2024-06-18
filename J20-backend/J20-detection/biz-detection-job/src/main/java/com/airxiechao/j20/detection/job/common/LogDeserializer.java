package com.airxiechao.j20.detection.job.common;

import com.airxiechao.j20.common.api.pojo.log.Log;
import com.airxiechao.j20.common.util.LogUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.connectors.kafka.KafkaDeserializationSchema;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.nio.charset.StandardCharsets;

/**
 * 日志反序列化器
 */
@Slf4j
public class LogDeserializer implements KafkaDeserializationSchema<Log> {

    @Override
    public boolean isEndOfStream(Log kafkaLog) {
        return false;
    }

    @Override
    public Log deserialize(ConsumerRecord<byte[], byte[]> consumerRecord) throws Exception {
        try{
            long timestamp = consumerRecord.timestamp();
            String value = new String(consumerRecord.value(), StandardCharsets.UTF_8);
            return LogUtil.parseLog(value, timestamp);
        }catch (Exception e){
            log.error("解码Kafka日志发生错误", e);
        }

        return null;
    }

    @Override
    public TypeInformation<Log> getProducedType() {
        return TypeInformation.of(Log.class);
    }

}
