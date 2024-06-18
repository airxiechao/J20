package com.airxiechao.j20.common.api.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 树形节点的值对象
 * @param <T> 节点数据的类型
 */
@Data
@AllArgsConstructor
public class TreeNodeVo<T> {
    /**
     * 节点数据
     */
    private T node;

    /**
     * 子节点
     */
    private List<TreeNodeVo<T>> children;
}
