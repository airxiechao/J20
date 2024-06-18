package com.airxiechao.j20.detection.api.pojo.rule;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 规则中条件过滤节点对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RuleFilterNode implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 节点类型
     */
    private String nodeType;

    /**
     * 操作符
     */
    private String operator;

    /**
     * 左字段
     */
    private String field;

    /**
     * 右值
     */
    private String value;

    /**
     * 子节点
     */
    private List<RuleFilterNode> children;
}
