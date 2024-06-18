package com.airxiechao.j20.detection.job.condition;

import com.airxiechao.j20.common.api.pojo.log.Log;
import com.airxiechao.j20.detection.api.pojo.config.JobConfig;
import com.airxiechao.j20.detection.api.pojo.rule.RuleFilterNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.functions.FilterFunction;

/**
 * 条件过滤函数的实现
 */
@Slf4j
public class ConditionFilterFunction implements FilterFunction<Log> {

    private RuleFilterNode filterNode;
    private JobConfig config;

    public ConditionFilterFunction(RuleFilterNode filterNode, JobConfig config){
        this.filterNode = filterNode;
        this.config = config;
    }

    @Override
    public boolean filter(Log value) throws Exception {
        if(null == filterNode){
            return false;
        }

        boolean pass = false;
        try{
            pass = new ConditionFilterNodeVerifier(filterNode, config).verify(value);
            if(pass){
                log.info("条件过滤器通过日志 [{}]", value);
            }
        }catch (Exception e){
            log.info("条件过滤器过滤日志发生错误", e);
        }

        return pass;
    }


}
