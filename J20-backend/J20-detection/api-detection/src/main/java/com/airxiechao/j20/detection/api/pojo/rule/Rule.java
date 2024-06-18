package com.airxiechao.j20.detection.api.pojo.rule;

import com.alibaba.fastjson2.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 规则描述对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Rule implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    private String id;

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 匹配日志的协议
     */
    private String protocol;

    /**
     * 过滤条件类型
     */
    private String criteriaType;

    /**
     * 过滤条件
     */
    private JSONObject criteria;

    /**
     * 输出配置
     */
    private RuleOutput output;

    /**
     * 频率配置
     */
    private RuleFrequency frequency;

    /**
     * 聚合配置
     */
    private RuleAggregation aggregation;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 最后更新时间
     */
    private Date lastUpdateTime;

    public Rule(String name, String description, String protocol, String criteriaType, JSONObject criteria, RuleOutput output, RuleFrequency frequency, RuleAggregation aggregation) {
        this.name = name;
        this.description = description;
        this.protocol = protocol;
        this.criteriaType = criteriaType;
        this.criteria = criteria;
        this.output = output;
        this.frequency = frequency;
        this.aggregation = aggregation;
    }

    public Rule(String id, String name, String description, String protocol, String criteriaType, JSONObject criteria, RuleOutput output, RuleFrequency frequency, RuleAggregation aggregation) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.protocol = protocol;
        this.criteriaType = criteriaType;
        this.criteria = criteria;
        this.output = output;
        this.frequency = frequency;
        this.aggregation = aggregation;
    }
}
