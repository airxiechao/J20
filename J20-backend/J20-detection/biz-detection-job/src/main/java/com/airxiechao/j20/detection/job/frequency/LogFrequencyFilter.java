package com.airxiechao.j20.detection.job.frequency;

import com.airxiechao.j20.common.api.pojo.log.Log;
import com.airxiechao.j20.detection.job.common.LogFieldsKeySelector;
import com.airxiechao.j20.detection.util.WindowUtil;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.triggers.EventTimeTrigger;
import org.apache.flink.streaming.api.windowing.triggers.ProcessingTimeoutTrigger;

import java.time.Duration;

/**
 * 频率过滤器的实现
 */
public class LogFrequencyFilter {

    private SingleOutputStreamOperator<Log> logs;
    private String[] groupBy;
    private long time;
    private String unit;
    private int threshold;

    public LogFrequencyFilter(
            SingleOutputStreamOperator<Log> logs,
            String[] groupBy,
            long time,
            String unit,
            int threshold
    ){
        this.logs = logs;
        this.groupBy = groupBy;
        this.time = time;
        this.unit = unit;
        this.threshold = threshold;
    }

    public SingleOutputStreamOperator<Log> filter(){
        return logs.keyBy(
                // 频率字段分组
                new LogFieldsKeySelector(groupBy)
        ).window(
                // 频率窗口
                TumblingEventTimeWindows.of(WindowUtil.of(time, unit))
        ).trigger(
                ProcessingTimeoutTrigger.of(
                        EventTimeTrigger.create(),
                        // 窗口结束一分钟后超时
                        Duration.ofMillis(WindowUtil.of(time, unit).toMilliseconds() + 60*1000))
        ).process(
                // 频率过滤
                new LogFrequencyProcessWindowFunction(threshold)
        );
    }
}
