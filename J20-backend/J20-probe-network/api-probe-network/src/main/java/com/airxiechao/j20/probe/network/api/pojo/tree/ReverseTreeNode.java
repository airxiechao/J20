package com.airxiechao.j20.probe.network.api.pojo.tree;

import lombok.Data;

/**
 * 反向树节点
 * @param <T> 节点数据类型
 */
@Data
public class ReverseTreeNode<T> {
    /**
     * 节点名称
     */
    private String name;

    /**
     * 节点数据
     */
    private T node;

    /**
     * 上级节点
     */
    private ReverseTreeNode<T> parent;
}
