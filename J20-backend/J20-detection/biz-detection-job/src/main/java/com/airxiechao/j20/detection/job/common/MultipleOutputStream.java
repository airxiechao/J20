package com.airxiechao.j20.detection.job.common;

import com.airxiechao.j20.common.api.pojo.event.Event;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;

/**
 * 多输出流对象
 */
@Data
@AllArgsConstructor
public class MultipleOutputStream {
    private SingleOutputStreamOperator<Event> eventOutputStream;
}
