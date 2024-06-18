package com.airxiechao.j20.detection.job.common;

import com.airxiechao.j20.common.api.pojo.event.Event;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.serialization.SerializationSchema;

import java.nio.charset.StandardCharsets;

/**
 * 事件序列化器
 */
@Slf4j
public class EventSerializer implements SerializationSchema<Event> {

    @Override
    public byte[] serialize(Event o) {
        return JSONObject.toJSONString(o).getBytes(StandardCharsets.UTF_8);
    }
}
