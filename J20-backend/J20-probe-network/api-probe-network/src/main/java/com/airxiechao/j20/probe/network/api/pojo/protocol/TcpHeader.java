package com.airxiechao.j20.probe.network.api.pojo.protocol;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * TCP 头
 */
@Data
@AllArgsConstructor
public class TcpHeader {
    /**
     * 源 IP
     */
    private String srcIp;

    /**
     * 源端口
     */
    private int srcPot;

    /**
     * 目的 IP
     */
    private String dstIp;

    /**
     * 目的端口
     */
    private int dstPort;

    /**
     * SYN 标识
     */
    private boolean syn;

    /**
     * ACK 标识
     */
    private boolean ack;

    /**
     * FIN 标识
     */
    private boolean fin;

    /**
     * PSH 标识
     */
    private boolean psh;

    /**
     * RST 标识
     */
    private boolean rst;

    /**
     * URG 标识
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
}
