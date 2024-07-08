package com.airxiechao.j20.probe.network.protocol;

import com.airxiechao.j20.common.api.pojo.log.Log;
import com.airxiechao.j20.probe.common.api.pojo.log.HttpData;
import com.airxiechao.j20.probe.common.api.pojo.constant.ConstProtocolType;
import com.airxiechao.j20.probe.network.api.pojo.protocol.Protocol;
import com.airxiechao.j20.probe.network.api.pojo.protocol.TcpHeader;
import com.airxiechao.j20.probe.network.api.pojo.tree.LayerNode;
import com.airxiechao.j20.probe.network.api.pojo.protocol.TcpSequence;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.TcpPacket;
import rawhttp.core.RawHttp;
import rawhttp.core.RawHttpRequest;
import rawhttp.core.RawHttpResponse;
import rawhttp.core.body.BodyReader;
import rawhttp.core.body.ChunkedBodyContents;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * HTTP 协议解析器
 */
@Slf4j
@Protocol(name = ConstProtocolType.HTTP, outer = ConstProtocolType.TCP)
public class ProtocolHttp implements IProtocol {

    // 请求缓存
    private static final Map<TcpSequence, Pair<RawHttpRequest, Long>> httpRequestCache = new ConcurrentHashMap<>();
    private static final int HTTP_REQUEST_CACHE_SECONDS = 60;
    static {
        // 定时清理缓存
        Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(() -> {
            try{
                httpRequestCache.entrySet().removeIf(entry ->
                        System.currentTimeMillis() - entry.getValue().getRight() > HTTP_REQUEST_CACHE_SECONDS * 1000);
            }catch (Exception e){
                log.error("清理HTTP请求缓存发生错误", e);
            }
        }, 0, HTTP_REQUEST_CACHE_SECONDS, TimeUnit.SECONDS);
    }

    private LayerNode outerPayload;
    private RawHttpResponse httpResponse;

    @Override
    public boolean parse(LayerNode outerPayload) {
        this.outerPayload = outerPayload;
        Object packet = this.outerPayload.getNode();
        if (null == packet) {
            return false;
        }

        if (!(packet instanceof TcpPacket)) {
            return false;
        }

        TcpPacket p = (TcpPacket) packet;
        if (null == p.getPayload()) {
            return false;
        }

        byte[] rawData = p.getPayload().getRawData();
        if (null == rawData) {
            return false;
        }

        // 判断是请求还响应
        boolean isHttpRequest = false;
        boolean isHttpResponse = false;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(rawData)))) {
            String line = reader.readLine();
            if(line.startsWith("HTTP/1.")){
                isHttpResponse = true;
            }else if(line.endsWith("HTTP/1.1") || line.endsWith("HTTP/1.0")){
                isHttpRequest = true;
            }
        } catch (Exception ignored) {

        }

        if (!isHttpRequest && !isHttpResponse) {
            return false;
        }

        // 解析
        RawHttp http = new RawHttp();
        if (isHttpRequest) {
            // 解析请求
            try {
                TcpHeader tcpHeader = getTcpHeader();
                if(null == tcpHeader){
                    return false;
                }

                RawHttpRequest httpRequest = http.parseRequest(new ByteArrayInputStream(rawData));

                // 放入请求缓存
                TcpSequence tcpSequence = new TcpSequence(tcpHeader.getSrcIp(), tcpHeader.getSrcPot(), tcpHeader.getDstIp(), tcpHeader.getDstPort(), tcpHeader.getAcknowledgmentNumber());
                httpRequestCache.put(tcpSequence, Pair.of(httpRequest, System.currentTimeMillis()));

                log.debug("HTTP请求：{}", httpRequest.getUri().toString());

                return true;
            } catch (Exception e) {
                log.error("解析HTTP请求发生错误", e);
                return false;
            }
        } else {
            // 解析响应
            try {
                httpResponse = http.parseResponse(new ByteArrayInputStream(rawData));
                return true;
            } catch (Exception e) {
                log.error("解析HTTP响应发生错误", e);
                return false;
            }
        }
    }

    @Override
    public LayerNode getPayload() {
        return new LayerNode(null, outerPayload);
    }

    @Override
    public Object getOutput() {
        if (null == httpResponse){
            return null;
        }

        TcpHeader tcpHeader = getTcpHeader();
        if(null == tcpHeader){
            return null;
        }

        // 读取请求缓存
        String srcIp = tcpHeader.getDstIp();
        int srcPort = tcpHeader.getDstPort();
        String dstIp = tcpHeader.getSrcIp();
        int dstPort = tcpHeader.getSrcPot();
        TcpSequence tcpSequence = new TcpSequence(srcIp, srcPort, dstIp, dstPort, tcpHeader.getSequenceNumber());
        if(!httpRequestCache.containsKey(tcpSequence)){
            return null;
        }

        RawHttpRequest httpRequest  = httpRequestCache.remove(tcpSequence).getLeft();

        // HTTP
        String realSrcIp = srcIp;
        Optional<String> optRealSrcIp = httpRequest.getHeaders().getFirst("X-Forwarded-For");
        if (optRealSrcIp.isPresent()) {
            realSrcIp = optRealSrcIp.get();
        }

        String reqContentType = null;
        Optional<String> optReqContentType = httpRequest.getHeaders().getFirst("CONTENT-TYPE");
        if (optReqContentType.isPresent()) {
            reqContentType = optReqContentType.get();
        }

        String respContentType = null;
        Optional<String> optRespContentType = httpResponse.getHeaders().getFirst("CONTENT-TYPE");
        if (optRespContentType.isPresent()) {
            respContentType = optRespContentType.get();
        }

        int respStatus = httpResponse.getStatusCode();

        URI requestUri = httpRequest.getStartLine().getUri();
        HttpData data = new HttpData()
                .setUri(requestUri.toString())
                .setHost(requestUri.getHost())
                .setPath(requestUri.getPath())
                .setQuery(requestUri.getQuery())
                .setMethod(httpRequest.getMethod())
                .setReqContentType(reqContentType)
                .setRespContentType(respContentType)
                .setRespStatus(respStatus)
                .setRealSrcIp(realSrcIp)
                .setSrcIp(srcIp)
                .setSrcPort(srcPort)
                .setDstIp(dstIp)
                .setDstPort(dstPort);

        // 解析 JSON 请求体
        if(StringUtils.isNotBlank(reqContentType) && reqContentType.startsWith("application/json")){
            Optional<? extends BodyReader> optReqBody = httpRequest.getBody();
            if(optReqBody.isPresent()){
                try {
                    BodyReader bodyReader = optReqBody.get();
                    int available = bodyReader.asRawStream().available();
                    if(available > 0){
                        String body = null;
                        if(bodyReader.isChunked()){
                            Optional<Iterator<ChunkedBodyContents.Chunk>> optChunkStream = bodyReader.asChunkStream();
                            if(optChunkStream.isPresent()){
                                ChunkedBodyContents.Chunk firstChunk = optChunkStream.get().next();
                                body = new String(firstChunk.getData());
                            }
                        }else{
                            body = bodyReader.decodeBodyToString(StandardCharsets.UTF_8);
                        }

                        // 去除敏感信息
                        body = desensitizationRequest(body);

                        data.setReqJson(body);
                    }
                } catch (Exception e) {
                    log.error("提取HTTP请求体发生错误", e);
                }
            }
        }

        // 解析 JSON 响应体
        if(StringUtils.isNotBlank(respContentType) && respContentType.startsWith("application/json")){
            Optional<? extends BodyReader> optRespBody = httpResponse.getBody();
            if(optRespBody.isPresent()){
                try {
                    BodyReader bodyReader = optRespBody.get();
                    int available = bodyReader.asRawStream().available();
                    if(available > 0){
                        String body = null;
                        if(bodyReader.isChunked()){
                            Optional<Iterator<ChunkedBodyContents.Chunk>> optChunkStream = bodyReader.asChunkStream();
                            if(optChunkStream.isPresent()){
                                ChunkedBodyContents.Chunk firstChunk = optChunkStream.get().next();
                                body = new String(firstChunk.getData());
                            }
                        }else{
                            body = bodyReader.decodeBodyToString(StandardCharsets.UTF_8);
                        }

                        // 去除敏感信息
                        body = desensitizationResponse(body);

                        data.setRespJson(body);
                    }
                } catch (Exception e) {
                    log.error("提取HTTP响应体发生错误", e);
                }
            }
        }

        // 输出
        Log log = new Log((JSONObject) JSON.toJSON(data), System.currentTimeMillis());
        return log;
    }

    /**
     * 获取 TCP 头
     * @return TCP 头
     */
    private TcpHeader getTcpHeader(){
        // 端口
        Object tcpOuter = outerPayload.getNode();
        if (!(tcpOuter instanceof TcpPacket)) {
            return null;
        }

        TcpPacket tcpPacket = (TcpPacket) tcpOuter;
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

        // IP
        Object ipOuter = outerPayload.getOuter().getNode();
        if (!(ipOuter instanceof IpV4Packet)) {
            return null;
        }

        IpV4Packet ipV4Packet = (IpV4Packet) ipOuter;
        String srcIp = ipV4Packet.getHeader().getSrcAddr().getHostAddress();
        String dstIp = ipV4Packet.getHeader().getDstAddr().getHostAddress();

        return new TcpHeader(srcIp, srcPort, dstIp, dstPort,
                syn, ack, fin, psh, rst, urg,
                sequenceNumber, acknowledgmentNumber);
    }

    /**
     * 内容脱敏
     * @param body 内容
     * @return 脱敏内容
     */
    private String desensitizationRequest(String body){
        JSONObject jsonBody = null;
        try{
            jsonBody = JSON.parseObject(body);
        }catch (Exception ignored){

        }

        if(null == jsonBody){
            return null;
        }

        if(jsonBody.containsKey("password")){
            jsonBody.put("password", "***");
        }

        if(jsonBody.containsKey("data")){
            Object data = jsonBody.get("data");
            if(data instanceof JSONObject){
                JSONObject jsonData = (JSONObject) data;
                for (String key : jsonData.keySet()) {
                    if(key.toLowerCase().endsWith("token")){
                        jsonData.put(key, "***");
                    }
                }
            }
        }

        return jsonBody.toJSONString();
    }

    private String desensitizationResponse(String body){
        JSONObject jsonBody = null;
        try{
            jsonBody = JSON.parseObject(body);
        }catch (Exception ignored){

        }

        if(null == jsonBody){
            return null;
        }

        if(jsonBody.containsKey("data")){
            Object data = jsonBody.get("data");
            if(data instanceof JSONObject){
                JSONObject jsonData = (JSONObject) data;
                for (String key : jsonData.keySet()) {
                    if(key.toLowerCase().endsWith("token")){
                        jsonData.put(key, "***");
                    }
                }
            }
        }

        return jsonBody.toJSONString();
    }
}
