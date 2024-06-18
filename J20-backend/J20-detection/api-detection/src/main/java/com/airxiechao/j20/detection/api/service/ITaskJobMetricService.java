package com.airxiechao.j20.detection.api.service;

import com.airxiechao.j20.detection.api.pojo.job.TaskJobMetric;

import java.util.List;

/**
 * 任务监视服务接口
 */
public interface ITaskJobMetricService {
    /**
     * 查询任务的监测指标列表
     * @param ruleId 任务ID
     * @param minutes 分钟数
     * @return 任务的监测指标列表
     */
    List<TaskJobMetric> list(String ruleId, int minutes);

    /**
     * 添加任务的监测指标
     * @param metric 任务的监测指标
     */
    void add(TaskJobMetric metric);
}
