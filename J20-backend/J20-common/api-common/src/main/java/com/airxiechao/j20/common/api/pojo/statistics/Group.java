package com.airxiechao.j20.common.api.pojo.statistics;

import lombok.Data;

/**
 * 统计分组
 */
@Data
public class Group {
    /**
     * 分组名称
     */
    private String key;

    /**
     * 聚合值
     */
    private Double value;
}
