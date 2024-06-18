package com.airxiechao.j20.probe.network.protocol;

import com.airxiechao.j20.common.api.pojo.log.Log;
import com.airxiechao.j20.probe.common.api.pojo.constant.ConstProtocolType;
import com.airxiechao.j20.probe.common.api.pojo.log.UdpData;
import com.airxiechao.j20.probe.network.api.pojo.protocol.Protocol;
import com.airxiechao.j20.probe.network.api.pojo.tree.LayerNode;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.UdpPacket;

/**
 * UDP 协议解析器
 */
@Slf4j
@Protocol(name = ConstProtocolType.UDP, outer = ConstProtocolType.IP)
public class ProtocolUdp implements IProtocol {

    private LayerNode outerPayload;
    private UdpPacket udpPacket;

    @Override
    public boolean parse(LayerNode outerPayload) {
        this.outerPayload = outerPayload;
        Object packet = this.outerPayload.getNode();
        if(null == packet){
            return false;
        }

        if(packet instanceof IpV4Packet){
            IpV4Packet ipV4Packet = (IpV4Packet)packet;
            udpPacket = ipV4Packet.get(UdpPacket.class);
            return null != udpPacket;
        }

        return false;
    }

    @Override
    public LayerNode getPayload() {
        return new LayerNode(udpPacket, outerPayload);
    }

    @Override
    public Object getOutput() {
        // 端口
        int srcPort = udpPacket.getHeader().getSrcPort().valueAsInt();
        int dstPort = udpPacket.getHeader().getDstPort().valueAsInt();
        int length = udpPacket.length();

        // IP
        Object ipOuter = outerPayload.getNode();
        if(!(ipOuter instanceof IpV4Packet)){
            return null;
        }

        IpV4Packet ipV4Packet = (IpV4Packet)ipOuter;
        String srcIp = ipV4Packet.getHeader().getSrcAddr().getHostAddress();
        String dstIp = ipV4Packet.getHeader().getDstAddr().getHostAddress();

        UdpData data = new UdpData(srcIp, srcPort, dstIp, dstPort, length);

        Log log = new Log((JSONObject) JSON.toJSON(data), System.currentTimeMillis());
        return log;
    }
}
