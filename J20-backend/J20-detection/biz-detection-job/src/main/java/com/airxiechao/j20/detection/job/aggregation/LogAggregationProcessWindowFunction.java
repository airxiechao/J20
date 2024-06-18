package com.airxiechao.j20.detection.job.aggregation;

import com.airxiechao.j20.common.api.pojo.log.Log;
import com.airxiechao.j20.detection.api.pojo.constant.ConstEmitWhich;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 日志聚合过滤器的窗口函数实现
 */
@Slf4j
public class LogAggregationProcessWindowFunction extends ProcessWindowFunction<Log, Log, Object, TimeWindow> {

    private String emitWhich;

    public LogAggregationProcessWindowFunction(String emitWhich) {
        this.emitWhich = emitWhich;
    }

    @Override
    public void process(Object o, ProcessWindowFunction<Log, Log, Object, TimeWindow>.Context context, Iterable<Log> iterable, Collector<Log> collector) throws Exception {
        List<Log> list = new ArrayList<>();
        iterable.forEach(list::add);
        if(list.isEmpty()){
            return;
        }

        list.sort(Comparator.comparing(Log::getTimestamp));

        switch (emitWhich.toUpperCase()){
            case ConstEmitWhich.FIRST:
                Log first = list.get(0);
                collector.collect(first);
                log.info("聚合窗口通过日志 [{}]", first);
                break;
            case ConstEmitWhich.LAST:
                Log last = list.get(list.size() - 1);
                collector.collect(last);
                log.info("聚合窗口通过日志 [{}]", last);
                break;
        }
    }
}
