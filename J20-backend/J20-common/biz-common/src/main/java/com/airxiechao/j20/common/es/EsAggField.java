package com.airxiechao.j20.common.es;

import lombok.Data;

/**
 * ES 聚合字段
 */
@Data
public class EsAggField {
    /**
     * 字段
     */
    private String field;

    /**
     * 聚合方式
     * @see com.airxiechao.j20.common.api.pojo.constant.ConstEsAggType
     */
    private String aggType;
}
