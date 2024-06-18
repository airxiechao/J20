package com.airxiechao.j20.probe.network.api.pojo.tree;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 树节点
 * @param <T> 节点数据类型
 */
@Data
public class TreeNode<T> {
    /**
     * 节点名称
     */
    private String name;

    /**
     * 节点数据
     */
    private T node;

    /**
     * 下级节点列表
     */
    private List<TreeNode<T>> children = new ArrayList<>();
}
