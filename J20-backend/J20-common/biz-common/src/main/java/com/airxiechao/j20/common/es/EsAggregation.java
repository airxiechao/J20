package com.airxiechao.j20.common.es;

import lombok.Data;

import java.util.List;

/**
 * ES聚合查询结果
 */
@Data
public class EsAggregation {

    /**
     * 聚合名称
     */
    private String name;

    /**
     * 聚合的值
     */
    private Double value;

    /**
     * 桶列表
     */
    private List<EsAggregationBucket> buckets;
}
