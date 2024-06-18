package com.airxiechao.j20.probe.network.api.pojo.protocol;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * TCP 序列段
 */
@Data
@AllArgsConstructor
public class TcpSequence {
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
     * 序列号
     */
    private long sequenceNumber;
}
