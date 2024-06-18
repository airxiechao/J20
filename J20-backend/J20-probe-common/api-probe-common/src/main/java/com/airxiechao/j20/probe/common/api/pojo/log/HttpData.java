package com.airxiechao.j20.probe.common.api.pojo.log;

import com.airxiechao.j20.probe.common.api.pojo.constant.ConstProtocolType;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * HTTP 数据
 */
@Data
@Accessors(chain = true)
public class HttpData {
    /**
     * 协议
     */
    private final String protocol = ConstProtocolType.HTTP;

    /**
     * URI
     */
    private String uri;

    /**
     * 域名
     */
    private String host;

    /**
     * 路径
     */
    private String path;

    /**
     * 参数
     */
    private String query;

    /**
     * 方法
     */
    private String method;

    /**
     * 请求体的内容类型
     */
    private String reqContentType;

    /**
     * JSON 请求体
     */
    private String reqJson;

    /**
     * 响应体的内容类型
     */
    private String respContentType;

    /**
     * JSON 响应体
     */
    private String respJson;

    /**
     * 响应状态码
     */
    private Integer respStatus;

    /**
     * 真实源 IP
     */
    private String realSrcIp;

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
}
