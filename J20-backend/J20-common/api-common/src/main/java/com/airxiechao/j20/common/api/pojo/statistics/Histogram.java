package com.airxiechao.j20.common.api.pojo.statistics;

import lombok.Data;

/**
 * 统计直方图分组
 */
@Data
public class Histogram {
    /**
     * 分组键
     */
    private Long key;

    /**
     * 统计值
     */
    private Double value;
}
