package com.airxiechao.j20.detection.db.record;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

/**
 * 规则记录
 */
@Data
@Entity(name = "t_detection_rule")
public class RuleRecord {
    /**
     * ID
     */
    @Id
    private String id;

    /**
     * 名称
     */
    @Column(unique = true)
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 条件类型
     */
    private String criteriaType;

    /**
     * 协议
     */
    private String protocol;

    /**
     * 条件配置
     */
    @Column(columnDefinition = "TEXT")
    private String criteria;

    /**
     * 输出配置
     */
    @Column(columnDefinition = "TEXT")
    private String output;

    /**
     * 频率配置
     */
    @Column(columnDefinition = "TEXT")
    private String frequency;

    /**
     * 聚合配置
     */
    @Column(columnDefinition = "TEXT")
    private String aggregation;

    /**
     * 输出事件类型 ID
     */
    private String outputEventTypeId;

    /**
     * 输出时间类型名称
     */
    private String outputEventTypeName;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 最后修改时间
     */
    private Date lastUpdateTime;
}
