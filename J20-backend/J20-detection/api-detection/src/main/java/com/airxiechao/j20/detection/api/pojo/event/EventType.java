package com.airxiechao.j20.detection.api.pojo.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 事件类型对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventType {
    /**
     * ID
     */
    private String id;

    /**
     * 名称
     */
    private String name;

    /**
     * 级别
     */
    private String level;

    /**
     * 上级类型ID
     */
    private String parentId;

    /**
     * 字段格式
     */
    private List<SchemaField> fieldSchema;

    public EventType(String name, String level, String parentId, List<SchemaField> fieldSchema) {
        this.name = name;
        this.level = level;
        this.parentId = parentId;
        this.fieldSchema = fieldSchema;
    }
}
