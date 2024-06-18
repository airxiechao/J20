package com.airxiechao.j20.common.es;

import com.airxiechao.j20.common.api.pojo.constant.ConstEsAggType;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * ES 直方图聚合字段
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EsAggHistogramField extends EsAggField {
    /**
     * 间隔
     */
    private String histogramInterval;

    /**
     * 下界值
     */
    private Long histogramBoundMin;

    /**
     * 上界值
     */
    private Long histogramBoundMax;

    public EsAggHistogramField(String field, String histogramInterval, Long histogramBoundMin, Long histogramBoundMax) {
        this.setAggType(ConstEsAggType.HISTOGRAM);
        this.setField(field);
        this.histogramInterval = histogramInterval;
        this.histogramBoundMin = histogramBoundMin;
        this.histogramBoundMax = histogramBoundMax;
    }
}
