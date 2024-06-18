package com.airxiechao.j20.probe.network.api.pojo.tree;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 分层节点
 */
@Data
@AllArgsConstructor
public class LayerNode {
    /**
     * 节点数据
     */
    private Object node;

    /**
     * 外层节点
     */
    private LayerNode outer;
}
