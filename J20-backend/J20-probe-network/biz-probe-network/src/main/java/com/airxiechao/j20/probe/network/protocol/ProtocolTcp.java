package com.airxiechao.j20.probe.network.protocol;

import com.airxiechao.j20.common.api.pojo.log.Log;
import com.airxiechao.j20.probe.common.api.pojo.log.TcpData;
import com.airxiechao.j20.probe.common.api.pojo.constant.ConstProtocolType;
import com.airxiechao.j20.probe.network.api.pojo.protocol.Protocol;
import com.airxiechao.j20.probe.network.api.pojo.tree.LayerNode;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.pcap4j.packet.IcmpV4CommonPacket;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.TcpPacket;

/**
 * TCP 协议解析器
 */
@Slf4j
@Protocol(name = ConstProtocolType.TCP, outer = ConstProtocolType.IP)
public class ProtocolTcp implements IProtocol {

    private LayerNode outerPayload;
    private TcpPacket tcpPacket;

    @Override
    public boolean parse(LayerNode outerPayload) {
        this.outerPayload = outerPayload;
        Object packet = this.outerPayload.getNode();
        if(null == packet){
            return false;
        }

        if(packet instanceof IpV4Packet){
            IpV4Packet ipV4Packet = (IpV4Packet)packet;
            // 不是ICMP包
            if(null == ipV4Packet.get(IcmpV4CommonPacket.class)){
                tcpPacket = ipV4Packet.get(TcpPacket.class);
                return null != tcpPacket;
            }
        }

        return false;
    }

    @Override
    public LayerNode getPayload() {
        return new LayerNode(tcpPacket, outerPayload);
    }

    @Override
    public Object getOutput() {
        // 端口
        int srcPort = tcpPacket.getHeader().getSrcPort().valueAsInt();
        int dstPort = tcpPacket.getHeader().getDstPort().valueAsInt();
        boolean syn = tcpPacket.getHeader().getSyn();
        boolean ack = tcpPacket.getHeader().getAck();
        boolean fin = tcpPacket.getHeader().getFin();
        boolean psh = tcpPacket.getHeader().getPsh();
        boolean rst = tcpPacket.getHeader().getRst();
        boolean urg = tcpPacket.getHeader().getUrg();
        long sequenceNumber = tcpPacket.getHeader().getSequenceNumberAsLong();
        long acknowledgmentNumber = tcpPacket.getHeader().getAcknowledgmentNumberAsLong();
        int length = tcpPacket.length();

        // 只采集SYN标记的TCP包
        if(!syn){
            return null;
        }

        // IP
        Object ipOuter = outerPayload.getNode();
        if(!(ipOuter instanceof IpV4Packet)){
            return null;
        }

        IpV4Packet ipV4Packet = (IpV4Packet)ipOuter;
        String srcIp = ipV4Packet.getHeader().getSrcAddr().getHostAddress();
        String dstIp = ipV4Packet.getHeader().getDstAddr().getHostAddress();

        TcpData data = new TcpData(srcIp, srcPort, dstIp, dstPort,
                syn, ack, fin, psh, rst, urg,
                sequenceNumber, acknowledgmentNumber,
                length);

        Log log = new Log((JSONObject) JSON.toJSON(data), System.currentTimeMillis());
        return log;
    }

}
