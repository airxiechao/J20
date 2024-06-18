package com.airxiechao.j20.probe.network.protocol;

import com.airxiechao.j20.probe.common.api.pojo.constant.ConstProtocolType;
import com.airxiechao.j20.probe.network.api.pojo.protocol.Protocol;
import com.airxiechao.j20.probe.network.api.pojo.tree.LayerNode;

/**
 * RAW 协议解析器
 */
@Protocol(name = ConstProtocolType.RAW, outer = "")
public class ProtocolRaw implements IProtocol {

    private LayerNode outerPayload;

    @Override
    public boolean parse(LayerNode outerPayload) {
        this.outerPayload = outerPayload;
        return true;
    }

    @Override
    public LayerNode getPayload() {
        return outerPayload;
    }

    @Override
    public Object getOutput() {
        return null;
    }
}
