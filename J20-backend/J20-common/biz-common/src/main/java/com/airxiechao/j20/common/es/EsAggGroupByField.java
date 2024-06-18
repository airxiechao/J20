package com.airxiechao.j20.common.es;

import com.airxiechao.j20.common.api.pojo.constant.ConstEsAggType;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * ES 分组聚合字段
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EsAggGroupByField extends EsAggField {
    /**
     * 组内最大数据量
     */
    private Integer size;

    public EsAggGroupByField(String field, Integer size) {
        this.setAggType(ConstEsAggType.GROUP_BY);
        this.setField(field);
        this.size = size;
    }
}
