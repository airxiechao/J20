package com.airxiechao.j20.detection.api.pojo.rule;

import lombok.Data;

import java.io.Serializable;

/**
 * 规则的聚合方式描述对象
 */
@Data
public class RuleAggregation implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 是否启用聚合
     */
    private Boolean enabled;

    /**
     * 聚合窗口大小
     */
    private Long time;

    /**
     * 聚合窗口单位
     */
    private String unit;

    /**
     * 分组字段
     */
    private String[] groupBy;

    /**
     * 结果合并方式
     */
    private String emitWhich;
}
