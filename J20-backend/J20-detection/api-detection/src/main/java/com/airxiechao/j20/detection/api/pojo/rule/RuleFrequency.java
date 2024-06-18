package com.airxiechao.j20.detection.api.pojo.rule;

import lombok.Data;

import java.io.Serializable;

/**
 * 规则中频率描述对象
 */
@Data
public class RuleFrequency implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 是否启用频率
     */
    private Boolean enabled;

    /**
     * 分组字段
     */
    private String[] groupBy;

    /**
     * 时间大小
     */
    private Long time;

    /**
     * 时间单位
     */
    private String unit;

    /**
     * 频率阈值
     */
    private Integer threshold;
}
