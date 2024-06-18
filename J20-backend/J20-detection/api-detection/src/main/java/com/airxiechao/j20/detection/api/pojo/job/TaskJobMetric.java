package com.airxiechao.j20.detection.api.pojo.job;

import lombok.Data;

/**
 * 任务监测指标对象
 */
@Data
public class TaskJobMetric {
    /**
     * 任务ID
     */
    private String ruleId;

    /**
     * Flink任务实例ID
     */
    private String jobId;

    /**
     * 时间戳
     */
    private Long timestamp;

    /**
     * 任务状态
     */
    private String state;

    /**
     * 日志读入数量
     */
    private Long numLogIn;

    /**
     * 日志每秒读入数量
     */
    private Integer numLogInPerSecond;

    /**
     * 事件输出数量
     */
    private Long numEventOut;

    /**
     * 事件输出变化
     */
    private Long numEventOutDelta;

    /**
     * 事件每秒输出数量
     */
    private Integer numEventOutPerSecond;

}
