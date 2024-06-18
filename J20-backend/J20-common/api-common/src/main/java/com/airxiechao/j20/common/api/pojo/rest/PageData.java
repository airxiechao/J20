package com.airxiechao.j20.common.api.pojo.rest;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 分页结果数据
 * @param <T> 数据类型
 */
@Data
@AllArgsConstructor
public class PageData<T> {
    /**
     * 当前页码
     */
    private Integer current;

    /**
     * 页面大小
     */
    private Integer size;

    /**
     * 数据总数
     */
    private Long total;

    /**
     * 数据列表
     */
    private List<T> records;
}
