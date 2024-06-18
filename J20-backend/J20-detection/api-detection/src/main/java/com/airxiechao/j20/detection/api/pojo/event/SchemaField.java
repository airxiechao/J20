package com.airxiechao.j20.detection.api.pojo.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SchemaField implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 字段路径
     */
    private String key;

    /**
     * 字段显示名
     */
    private String value;
}
