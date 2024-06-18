package com.airxiechao.j20.detection.api.pojo.protocol;

import com.airxiechao.j20.detection.api.pojo.event.SchemaField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 日志协议
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Protocol {
    /**
     * 编码
     */
    private String code;

    /**
     * 协议字段
     */
    private List<SchemaField> fieldSchema;
}
