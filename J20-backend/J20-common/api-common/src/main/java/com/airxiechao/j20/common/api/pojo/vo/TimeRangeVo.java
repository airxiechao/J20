package com.airxiechao.j20.common.api.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

/**
 * 时间范围显示对象
 */
@Data
@AllArgsConstructor
public class TimeRangeVo {
    /**
     * 开始时间
     */
    private Date begin;

    /**
     * 结束时间
     */
    private Date end;
}
