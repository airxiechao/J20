package com.airxiechao.j20.probe.network.protocol;

import com.airxiechao.j20.probe.network.api.pojo.tree.LayerNode;

/**
 * 协议解析器接口
 */
public interface IProtocol {
    /**
     * 解析
     * @param outerPayload 外层节点
     * @return 是否解析成功
     */
    boolean parse(LayerNode outerPayload);

    /**
     * 获取内层负载节点
     * @return 内层负载节点
     */
    LayerNode getPayload();

    /**
     * 获取输出对象
     * @return 输出对象
     */
    Object getOutput();
}
