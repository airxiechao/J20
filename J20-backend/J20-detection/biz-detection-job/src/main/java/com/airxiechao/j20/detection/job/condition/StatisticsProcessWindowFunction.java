package com.airxiechao.j20.detection.job.condition;

import com.airxiechao.j20.common.api.pojo.log.Log;
import com.airxiechao.j20.common.util.FieldOperatorUtil;
import com.airxiechao.j20.detection.api.pojo.config.JobConfig;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.util.ArrayList;
import java.util.List;

/**
 * 日志统计窗口函数的实现
 */
@Slf4j
public class StatisticsProcessWindowFunction extends ProcessWindowFunction<Log, Log, Object, TimeWindow> {

    private String field;
    private String aggregate;
    private String operator;
    private Double value;
    private JobConfig config;

    public StatisticsProcessWindowFunction(
            String field, String aggregate, String operator, Double value, JobConfig config
    ) {
        this.field = field;
        this.aggregate = aggregate;
        this.operator = operator;
        this.value = value;
        this.config = config;
    }

    @Override
    public void process(Object o, ProcessWindowFunction<Log, Log, Object, TimeWindow>.Context context, Iterable<Log> iterable, Collector<Log> collector) throws Exception {
        List<Log> list = new ArrayList<>();
        iterable.forEach(list::add);
        if(list.isEmpty()){
            list.add(new Log(new JSONObject(), context.window().getEnd()));
        }

        double out = 0;
        long windowStart = context.window().getStart();
        long windowEnd = context.window().getEnd();
        try{
            out = new StatisticsAggregator(list, field, aggregate).compute();
        }catch (Exception e){
            log.error("统计过滤器计算统计值发生错误", e);
        }

        Log first = list.get(0);
        first.getData().put(String.format("%s(%s)", aggregate, field), out);
        first.addFeature(String.format("%f", out));
        first.setWindowOut(out);
        first.setWindowStart(windowStart);
        first.setWindowEnd(windowEnd);

        boolean passed = FieldOperatorUtil.doubleOperate(out, operator, value);
        if(passed){
            log.info("统计过滤器通过日志 [{}]", first);
            collector.collect(first);
        }
    }
}
