package com.airxiechao.j20.detection.db.record;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * 仪表盘图表记录
 */
@Data
@Entity(name = "t_dashboard_chart")
public class DashboardChartRecord {
    /**
     * ID
     */
    @Id
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 事件类型 ID
     */
    private String eventTypeId;

    /**
     * 聚合类型
     */
    private String aggregateType;

    /**
     * 分组字段
     */
    private String groupByField;

    /**
     * 聚合字段
     */
    private String aggregateField;
}