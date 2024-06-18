package com.airxiechao.j20.detection.job;

import com.airxiechao.j20.common.api.pojo.log.Log;
import com.airxiechao.j20.detection.api.pojo.config.JobConfig;
import com.airxiechao.j20.detection.api.pojo.rule.Rule;
import com.airxiechao.j20.detection.api.pojo.task.Task;
import com.airxiechao.j20.detection.job.common.MultipleOutputStream;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.streaming.api.datastream.DataStreamSource;

/**
 * 规则实现的抽象类
 */
@Slf4j
public abstract class AbstractTaskJob {
    protected Task task;
    protected JobConfig config;

    public AbstractTaskJob(Task task, JobConfig config){
        this.task = task;
        this.config = config;
    }

    /**
     * 从日志流中产生输出
     * @param stream 日志流
     * @param rule 规则
     * @return 输出
     */
    public abstract MultipleOutputStream recognizeSingleCriteria(DataStreamSource<Log> stream, Rule rule);

}
