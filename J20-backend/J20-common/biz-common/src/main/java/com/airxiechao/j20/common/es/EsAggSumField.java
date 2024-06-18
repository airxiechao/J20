package com.airxiechao.j20.common.es;

import com.airxiechao.j20.common.api.pojo.constant.ConstEsAggType;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * ES 求和聚合字段
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EsAggSumField extends EsAggField {
    public EsAggSumField(String field) {
        this.setAggType(ConstEsAggType.SUM);
        this.setField(field);
    }
}
