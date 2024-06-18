package com.airxiechao.j20.probe.common.api.pojo.log;

import com.airxiechao.j20.probe.common.api.pojo.constant.ConstProtocolType;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * UDP 数据
 */
@Data
@AllArgsConstructor
public class UdpData {
    /**
     * 协议
     */
    private final String protocol = ConstProtocolType.UDP;

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
     * 包长度
     */
    private Integer length;
}
