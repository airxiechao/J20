package com.airxiechao.j20.detection.api.pojo.event;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 事件类型的带事件数量的值对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EventTypeNumTreeNodeVo extends EventType {

    /**
     * 事件数量
     */
    private Double numEvent;

    /**
     * 下级类型
     */
    private List<EventTypeNumTreeNodeVo> children;

    public EventTypeNumTreeNodeVo(String id, String name, String level, String parentId, List<SchemaField> fieldSchema, Double numEvent, List<EventTypeNumTreeNodeVo> children) {
        super(id, name, level, parentId, fieldSchema);
        this.numEvent = numEvent;
        this.children = children;
    }
}
