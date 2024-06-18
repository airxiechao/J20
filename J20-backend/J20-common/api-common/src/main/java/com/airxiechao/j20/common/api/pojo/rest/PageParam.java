package com.airxiechao.j20.common.api.pojo.rest;

import lombok.Data;

/**
 * 分页请求参数
 */
@Data
public class PageParam {
    /**
     * 当前页码
     */
    private Integer current;

    /**
     * 页面大小
     */
    private Integer size;

    /**
     * 排序字段
     */
    private String orderBy;

    /**
     * 是否升序
     */
    private Boolean orderAsc;
}
