package com.airxiechao.j20.detection.job.aggregation;

import com.airxiechao.j20.common.api.pojo.log.Log;
import com.airxiechao.j20.detection.job.common.LogFieldsKeySelector;
import com.airxiechao.j20.detection.util.WindowUtil;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.triggers.EventTimeTrigger;
import org.apache.flink.streaming.api.windowing.triggers.ProcessingTimeoutTrigger;

import java.time.Duration;

/**
 * 日志聚合过滤器的实现
 */
public class LogAggregationFilter {

    private SingleOutputStreamOperator<Log> logs;
    private String[] groupBy;
    private long time;
    private String unit;
    private String emitWhich;

    public LogAggregationFilter(SingleOutputStreamOperator<Log> logs, String[] groupBy, long time, String unit, String emitWhich) {
        this.logs = logs;
        this.groupBy = groupBy;
        this.time = time;
        this.unit = unit;
        this.emitWhich = emitWhich;
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
            // 聚合过滤
            new LogAggregationProcessWindowFunction(emitWhich)
        );
    }
}
