package com.airxiechao.j20.probe.network.protocol;

import com.airxiechao.j20.probe.common.api.pojo.constant.ConstProtocolType;
import com.airxiechao.j20.probe.network.api.pojo.protocol.Protocol;
import com.airxiechao.j20.probe.network.api.pojo.tree.LayerNode;
import lombok.extern.slf4j.Slf4j;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.Packet;

/**
 * IP 协议解析器
 */
@Slf4j
@Protocol(name = ConstProtocolType.IP, outer = ConstProtocolType.RAW)
public class ProtocolIp implements IProtocol {

    private LayerNode outerPayload;
    private IpV4Packet ipV4Packet;

    @Override
    public boolean parse(LayerNode outerPayload) {
        this.outerPayload = outerPayload;
        Object packet = this.outerPayload.getNode();
        if(null == packet){
            return false;
        }

        if(packet instanceof Packet) {
            Packet p = (Packet)packet;
            ipV4Packet = p.get(IpV4Packet.class);
            return null != ipV4Packet;
        }

        return false;
    }

    @Override
    public LayerNode getPayload() {
        return new LayerNode(ipV4Packet, outerPayload);
    }

    @Override
    public Object getOutput() {
        return null;
    }
}
