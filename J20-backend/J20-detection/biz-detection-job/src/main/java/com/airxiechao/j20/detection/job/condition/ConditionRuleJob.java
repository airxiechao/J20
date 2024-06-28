package com.airxiechao.j20.detection.job.condition;

import com.airxiechao.j20.common.api.pojo.event.Event;
import com.airxiechao.j20.common.api.pojo.log.Log;
import com.airxiechao.j20.detection.api.pojo.config.JobConfig;
import com.airxiechao.j20.detection.api.pojo.constant.ConstRuleCriteriaType;
import com.airxiechao.j20.detection.api.pojo.job.TaskJob;
import com.airxiechao.j20.detection.api.pojo.rule.*;
import com.airxiechao.j20.detection.api.pojo.task.Task;
import com.airxiechao.j20.detection.job.AbstractTaskJob;
import com.airxiechao.j20.detection.job.aggregation.LogAggregationFilter;
import com.airxiechao.j20.detection.job.common.LogToEventMapFunction;
import com.airxiechao.j20.detection.job.frequency.LogFrequencyFilter;
import com.airxiechao.j20.detection.util.RuleCriteriaUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;

/**
 * 条件规则的实现
 */
@TaskJob(ConstRuleCriteriaType.CONDITION)
public class ConditionRuleJob extends AbstractTaskJob {

    public ConditionRuleJob(Task task, JobConfig config) {
        super(task, config);
    }

    @Override
    public SingleOutputStreamOperator<Event> recognizeSingleCriteria(DataStreamSource<Log> stream, Rule rule) {
        RuleCriteriaCondition ruleCriteria = JSON.parseObject(rule.getCriteria().toJSONString(), new TypeReference<>(){});

        RuleFilterNode filterNode = ruleCriteria.getFilter();

        // 添加来源标识条件
        filterNode = RuleCriteriaUtil.addFilter(filterNode, "protocol", rule.getProtocol());

        // 按条件过滤日志
        SingleOutputStreamOperator<Log> logs = stream.filter(new ConditionFilterFunction(filterNode, config));

        // 按窗口过滤日志
        RuleStatistics ruleStatistics = ruleCriteria.getStatistics();
        boolean statisticsEnabled = (null != ruleStatistics && null != ruleStatistics.getEnabled()) ? ruleStatistics.getEnabled() : false;
        if(statisticsEnabled){
            logs = new StatisticsFilter(logs, ruleStatistics, config).filter();
        }

        // 按频率过滤日志
        RuleFrequency ruleFrequency = rule.getFrequency();
        boolean frequencyEnabled = (null != ruleFrequency && null != ruleFrequency.getEnabled()) ? ruleFrequency.getEnabled() : false;
        if(frequencyEnabled){
            logs = new LogFrequencyFilter(
                    logs,
                    ruleFrequency.getGroupBy(),
                    ruleFrequency.getTime(),
                    ruleFrequency.getUnit(), ruleFrequency.getThreshold()
            ).filter();
        }

        // 聚合日志
        RuleAggregation ruleAggregation = rule.getAggregation();
        boolean aggregationEnabled = (null != ruleAggregation && null != ruleAggregation.getEnabled()) ? ruleAggregation.getEnabled() : false;
        if(aggregationEnabled){
            logs = new LogAggregationFilter(
                    logs,
                    ruleAggregation.getGroupBy(),
                    ruleAggregation.getTime(),
                    ruleAggregation.getUnit(),
                    ruleAggregation.getEmitWhich()
            ).filter();
        }

        // 转换输出
        SingleOutputStreamOperator<Event> eventOutputStream = null;

        if(null != rule.getOutput()) {
            eventOutputStream = logs.map(
                    new LogToEventMapFunction(task.getId(), task.getName(), rule.getId(), rule.getName(), rule.getOutput(), config)
            );
        }

        return eventOutputStream;
    }
}
