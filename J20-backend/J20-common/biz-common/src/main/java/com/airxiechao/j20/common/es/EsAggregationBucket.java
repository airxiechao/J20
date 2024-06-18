package com.airxiechao.j20.common.es;

import lombok.Data;

/**
 * ES聚合查询结果的桶
 */
@Data
public class EsAggregationBucket {
    /**
     * 桶标识
     */
    private String key;

    /**
     * 桶内计数
     */
    private Integer count;

    /**
     * 子聚合结果
     */
    private EsAggregation aggregation;
}


