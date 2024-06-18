package com.airxiechao.j20.detection.api.pojo.rule;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 规则中的统计描述对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RuleStatistics implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 是否启用窗口统计
     */
    private Boolean enabled;

    /**
     * 滑动窗口
     */
    private RuleSlideWindow window;

    /**
     * 左字段
     */
    private String field;

    /**
     * 统计聚合方式
     */
    private String aggregate;

    /**
     * 操作符
     */
    private String operator;

    /**
     * 右值
     */
    private Double value;

    /**
     * 分组字段
     */
    private String[] groupBy;

    /**
     * 尽早触发
     */
    private Boolean eager;
}
