package com.airxiechao.j20.detection.api.pojo.rule;

import lombok.Data;

import java.io.Serializable;

/**
 * 条件规则的过滤器描述
 */
@Data
public class RuleCriteriaCondition implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 过滤条件
     */
    private RuleFilterNode filter;

    /**
     * 统计条件
     */
    private RuleStatistics statistics;
}

