package com.airxiechao.j20.detection.api.pojo.rule;

import com.airxiechao.j20.detection.api.pojo.event.SchemaField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 规则输出
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RuleOutput implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 事件级别
     */
    private String level;

    /**
     * 输出事件消息模板
     */
    private String messageTemplate;

    /**
     * 输出事件类型ID
     */
    private String eventTypeId;

    /**
     * 输出事件类型名称
     */
    private String eventTypeName;

    /**
     * 输出数据ID值模板
     */
    private String idTemplate;

    /**
     * 输出字段
     */
    private List<SchemaField> propertyFields;

}
