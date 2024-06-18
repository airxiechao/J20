package com.airxiechao.j20.detection.job.condition;

import com.airxiechao.j20.common.api.pojo.log.Log;
import com.airxiechao.j20.common.util.FieldOperatorUtil;
import com.airxiechao.j20.detection.api.pojo.config.JobConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.streaming.api.windowing.triggers.Trigger;
import org.apache.flink.streaming.api.windowing.triggers.TriggerResult;
import org.apache.flink.streaming.api.windowing.windows.Window;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 窗口尽早触发器
 * @param <W> 窗口类型
 */
@Slf4j
public class StatisticsEagerTrigger<W extends Window> extends Trigger<Log, W> {

    private String field;
    private String aggregate;
    private String operator;
    private Double value;
    private JobConfig config;

    /**
     * 窗口状态表
     */
    private Map<String, StatisticsEagerState> stateMap = new HashMap<>();

    public StatisticsEagerTrigger(String field, String aggregate, String operator, Double value, JobConfig config) {
        this.field = field;
        this.aggregate = aggregate;
        this.operator = operator;
        this.value = value;
        this.config = config;
    }

    @Override
    public TriggerResult onElement(Log element, long timestamp, W window, TriggerContext ctx) throws Exception {
        String stateKey = ctx.toString();
        if(!stateMap.containsKey(stateKey)){
            stateMap.put(stateKey, new StatisticsEagerState());
        }

        StatisticsEagerState state = stateMap.get(stateKey);

        if(state.isFired()){
            return TriggerResult.PURGE;
        }

        state.getList().add(element);

        if(process(state.getList())){
            log.info("[EAGER]统计窗口触发");
            state.setFired(true);
            state.getList().clear();
            return TriggerResult.FIRE_AND_PURGE;
        }

        return TriggerResult.CONTINUE;
    }

    @Override
    public TriggerResult onProcessingTime(long time, W window, TriggerContext ctx) throws Exception {
        return TriggerResult.CONTINUE;
    }

    @Override
    public TriggerResult onEventTime(long time, W window, TriggerContext ctx) throws Exception {
        return TriggerResult.CONTINUE;
    }

    @Override
    public void clear(W window, TriggerContext ctx) throws Exception {
        String stateKey = ctx.toString();
        stateMap.remove(stateKey);
    }

    private boolean process(List<Log> list){
        double out = 0;
        try{
            out = new StatisticsAggregator(list, field, aggregate).compute();
            log.info("[EAGER]统计窗口计算输出：{}", out);
        }catch (Exception e){
            log.error("[EAGER]统计过滤器计算统计值发生错误", e);
        }

        boolean passed = FieldOperatorUtil.doubleOperate(out, operator, value);
        return passed;
    }
}
