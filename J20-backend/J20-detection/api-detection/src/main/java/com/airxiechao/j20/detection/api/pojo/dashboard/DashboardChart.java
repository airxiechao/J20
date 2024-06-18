package com.airxiechao.j20.detection.api.pojo.dashboard;

import lombok.Data;

/**
 * 仪表盘图表
 */
@Data
public class DashboardChart {
    /**
     * ID
     */
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
     * @see com.airxiechao.j20.detection.api.pojo.constant.ConstDashboardAggregateType
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
