package com.airxiechao.j20.detection.db.record;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * 事件类型记录
 */
@Data
@Entity(name = "t_detection_event_type")
public class EventTypeRecord {
    /**
     * ID
     */
    @Id
    private String id;

    /**
     * 名称
     */
    @Column(unique = true)
    private String name;

    /**
     * 事件级别
     */
    private String level;

    /**
     * 上级 ID
     */
    private String parentId;

    /**
     * 字段格式
     */
    @Column(columnDefinition = "TEXT")
    private String fieldSchema;
}
