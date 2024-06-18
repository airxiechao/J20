package com.airxiechao.j20.probe.common.api.pojo.log;

import com.airxiechao.j20.probe.common.api.pojo.constant.ConstProtocolType;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * TCP 数据
 */
@Data
@AllArgsConstructor
public class TcpData {
    /**
     * 协议
     */
    private final String protocol = ConstProtocolType.TCP;

    /**
     * 源 IP
     */
    private String srcIp;

    /**
     * 源端口
     */
    private Integer srcPort;

    /**
     * 目的 IP
     */
    private String dstIp;

    /**
     * 目的端口
     */
    private Integer dstPort;

    /**
     * SYN 标记
     */
    private boolean syn;

    /**
     * ACK 标记
     */
    private boolean ack;

    /**
     * FIN 标记
     */
    private boolean fin;

    /**
     * PSH 标记
     */
    private boolean psh;

    /**
     * RST 标记
     */
    private boolean rst;

    /**
     * URG 标记
     */
    private boolean urg;

    /**
     * 序列号
     */
    private long sequenceNumber;

    /**
     * 确认号
     */
    private long acknowledgmentNumber;

    /**
     * 包长度
     */
    private Integer length;
}
