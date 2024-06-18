package com.airxiechao.j20.common.api.pojo.rest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 响应对象
 * @param <T> 响应数据类型
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Resp<T> {

    /**
     * 编码
     * @see com.airxiechao.j20.common.api.pojo.constant.ConstRespCode
     */
    private String code;

    /**
     * 消息
     */
    private String message;

    /**
     * 数据
     */
    private T data;
}
