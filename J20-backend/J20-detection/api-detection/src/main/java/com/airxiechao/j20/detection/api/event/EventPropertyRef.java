package com.airxiechao.j20.detection.api.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 事件属性的引用
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventPropertyRef {
    /**
     * 事件属性ID
     */
    private String id;
}
