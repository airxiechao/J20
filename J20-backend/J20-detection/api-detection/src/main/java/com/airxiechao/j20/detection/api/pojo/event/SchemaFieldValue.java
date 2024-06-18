package com.airxiechao.j20.detection.api.pojo.event;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SchemaFieldValue {
    /**
     * 字段路径
     */
    private String field;

    /**
     * 字段显示名
     */
    private String name;

    /**
     * 字段的值
     */
    private Object value;
}
