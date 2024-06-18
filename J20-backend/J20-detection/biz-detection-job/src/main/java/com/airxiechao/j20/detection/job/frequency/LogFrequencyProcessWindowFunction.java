package com.airxiechao.j20.detection.job.frequency;

import com.airxiechao.j20.common.api.pojo.log.Log;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 频率过滤窗口函数的实现
 */
@Slf4j
public class LogFrequencyProcessWindowFunction extends ProcessWindowFunction<Log, Log, Object, TimeWindow> {

    private int threshold;

    public LogFrequencyProcessWindowFunction(int threshold){
        this.threshold = threshold;
    }

    @Override
    public void process(Object o, ProcessWindowFunction<Log, Log, Object, TimeWindow>.Context context, Iterable<Log> iterable, Collector<Log> collector) throws Exception {
        List<Log> list = new ArrayList<>();
        iterable.forEach(list::add);
        if(list.isEmpty()){
            return;
        }

        list.sort(Comparator.comparing(Log::getTimestamp));

        if(list.size() >= threshold){
            Log first = list.get(0);
            collector.collect(first);
            log.info("频率窗口通过日志 [{}]", first);
        }
    }
}
