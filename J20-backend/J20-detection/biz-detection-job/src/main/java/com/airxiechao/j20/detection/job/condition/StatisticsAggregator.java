package com.airxiechao.j20.detection.job.condition;

import com.airxiechao.j20.common.api.pojo.log.Log;
import com.airxiechao.j20.common.util.FieldUtil;
import com.airxiechao.j20.detection.api.pojo.constant.ConstRuleStatisticsAggregate;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.OptionalDouble;

/**
 * 统计聚合器实现
 */
@Slf4j
public class StatisticsAggregator {
    private List<Log> list;
    private String field;
    private String aggregate;

    public StatisticsAggregator(List<Log> list, String field, String aggregate) {
        this.list = list;
        this.field = field;
        this.aggregate = aggregate;
    }

    public double compute(){
        double out = 0;

        switch(aggregate){
            case ConstRuleStatisticsAggregate.COUNT:
                out = list.stream()
                        .filter(log -> FieldUtil.logFieldIsNotNull(field, log))
                        .mapToDouble(log -> 1).sum();
                break;
            case ConstRuleStatisticsAggregate.COUNT_UNIQUE:
                out = list.stream()
                        .filter(log -> FieldUtil.logFieldIsNotNull(field, log))
                        .map(log -> FieldUtil.renderLogToField(field, log))
                        .distinct()
                        .count();
                break;
            case ConstRuleStatisticsAggregate.SUM:
                out = list.stream()
                        .filter(log -> FieldUtil.logFieldIsNotNull(field, log))
                        .mapToDouble(log -> FieldUtil.renderLogToDoubleField(field, log))
                        .sum();
                break;
            case ConstRuleStatisticsAggregate.MAX:
                OptionalDouble maxOpt = list.stream()
                        .filter(log -> FieldUtil.logFieldIsNotNull(field, log))
                        .mapToDouble(log -> FieldUtil.renderLogToDoubleField(field, log))
                        .max();
                if(maxOpt.isPresent()){
                    out = maxOpt.getAsDouble();
                }
                break;
            case ConstRuleStatisticsAggregate.MIN:
                OptionalDouble minOpt = list.stream()
                        .filter(log ->FieldUtil.logFieldIsNotNull(field, log))
                        .mapToDouble(log -> FieldUtil.renderLogToDoubleField(field, log))
                        .min();
                if(minOpt.isPresent()){
                    out = minOpt.getAsDouble();
                }
                break;
        }

        return out;
    }
}
