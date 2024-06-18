package com.airxiechao.j20.detection.job.condition;

import com.airxiechao.j20.common.api.pojo.log.Log;
import com.airxiechao.j20.common.util.TimeUtil;
import com.airxiechao.j20.detection.api.pojo.config.JobConfig;
import com.airxiechao.j20.detection.api.pojo.constant.ConstTimeUnit;
import com.airxiechao.j20.detection.api.pojo.rule.RuleSlideWindow;
import com.airxiechao.j20.detection.api.pojo.rule.RuleStatistics;
import com.airxiechao.j20.detection.job.common.LogFieldsKeySelector;
import com.airxiechao.j20.detection.util.WindowUtil;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.triggers.EventTimeTrigger;
import org.apache.flink.streaming.api.windowing.triggers.ProcessingTimeoutTrigger;

import java.time.Duration;

/**
 * 统计过滤器的实现
 */
public class StatisticsFilter {

    private SingleOutputStreamOperator<Log> logs;
    private RuleStatistics statistics;
    private JobConfig config;

    public StatisticsFilter(SingleOutputStreamOperator<Log> logs, RuleStatistics statistics, JobConfig config
    ) {
        this.logs = logs;
        this.statistics = statistics;
        this.config = config;
    }

    public SingleOutputStreamOperator<Log> filter(){
        RuleSlideWindow slideWindow = statistics.getWindow();
        long size = slideWindow.getSize();
        String sizeUnit = slideWindow.getSizeUnit();
        long slide = slideWindow.getSlide();
        String slideUnit = slideWindow.getSlideUnit();

        String field = statistics.getField();
        String aggregate = statistics.getAggregate();
        String operator = statistics.getOperator();
        Double value = statistics.getValue();
        String[] groupBy = statistics.getGroupBy();

        return logs.keyBy(
                new LogFieldsKeySelector(groupBy)
        ).window(
                SlidingEventTimeWindows.of(
                        WindowUtil.of(size, sizeUnit),
                        WindowUtil.of(slide, slideUnit),
                        Time.hours(ConstTimeUnit.DAY.equals(slideUnit) ? -TimeUtil.getTimezoneOffsetHour() : 0)
        )).trigger(
                null != statistics.getEager() && statistics.getEager() ?
                new StatisticsEagerTrigger<>(field, aggregate, operator, value, config) :
                ProcessingTimeoutTrigger.of(
                        EventTimeTrigger.create(),
                        // 窗口结束一分钟后超时
                        Duration.ofMillis(WindowUtil.of(size, sizeUnit).toMilliseconds() + 60*1000))
        ).process(
                new StatisticsProcessWindowFunction(field, aggregate, operator, value, config)
        );
    }
}
