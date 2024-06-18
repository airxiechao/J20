package com.airxiechao.j20.detection.api.event;

import lombok.Data;

/**
 * 事件引用
 */
@Data
public class EventRef {
    /**
     * 事件 ID
     */
    private String id;

    /**
     * 事件时间戳
     */
    private long timestamp;

    /**
     * 事件类型 ID
     */
    private String eventTypeId;

    /**
     * 事件类型名称
     */
    private String eventTypeName;

    /**
     * 事件消息
     */
    private String message;

    /**
     * 事件级别
     */
    private String level;

    /**
     * 事件来源任务 ID
     */
    private String taskId;

    /**
     * 事件来源任务名称
     */
    private String taskName;

    /**
     * 事件来源规则 ID
     */
    private String ruleId;

    /**
     * 事件来源规则名称
     */
    private String ruleName;
}
