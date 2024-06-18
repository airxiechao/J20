package com.airxiechao.j20.detection.service;

import com.airxiechao.j20.detection.api.pojo.job.TaskJobMetric;
import com.airxiechao.j20.detection.api.service.ITaskJobMetricService;

import java.util.List;

// todo: task job metric service
public class TaskJobMetricService implements ITaskJobMetricService {
    @Override
    public List<TaskJobMetric> list(String ruleId, int minutes) {
        return null;
    }

    @Override
    public void add(TaskJobMetric metric) {

    }
}
