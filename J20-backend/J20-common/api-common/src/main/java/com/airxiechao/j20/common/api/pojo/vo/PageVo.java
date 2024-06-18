package com.airxiechao.j20.common.api.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 页数据的值对象
 * @param <T> 页数据的类型
 */
@Data
@AllArgsConstructor
public class PageVo<T> {
    /**
     * 数据总数
     */
    private long total;

    /**
     * 页数据
     */
    private List<T> page;
}
