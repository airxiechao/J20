package com.airxiechao.j20.detection.api.rest;

import com.airxiechao.j20.common.api.pojo.rest.PageData;
import com.airxiechao.j20.common.api.pojo.rest.Resp;
import com.airxiechao.j20.detection.api.pojo.protocol.Protocol;
import com.airxiechao.j20.detection.api.rest.param.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * 协议请求接口
 */
public interface IProtocolController {

    /**
     * 添加协议
     * @param param 参数
     * @return 新增协议
     */
    @PostMapping("/api/detection/protocol/add")
    Resp<Protocol> add(@RequestBody @Valid ProtocolAddParam param);

    /**
     * 修改协议
     * @param param 参数
     * @return 响应
     */
    @PostMapping("/api/detection/protocol/update")
    Resp update(@RequestBody @Valid ProtocolUpdateParam param);

    /**
     * 查询协议
     * @param param 参数
     * @return 协议
     */
    @PostMapping("/api/detection/protocol/get")
    Resp<Protocol> get(@RequestBody @Valid ProtocolGetParam param);

    /**
     * 查询协议列表
     * @param param 参数
     * @return 协议列表
     */
    @PostMapping("/api/detection/protocol/list")
    Resp<PageData<Protocol>> list(@RequestBody(required = false) @Valid ProtocolListParam param);

    /**
     * 查询所有协议
     * @param param 参数
     * @return 协议列表
     */
    @PostMapping("/api/detection/protocol/all")
    Resp<List<Protocol>> all(@RequestBody(required = false) @Valid ProtocolListParam param);

    /**
     * 删除协议
     * @param param 参数
     * @return 响应
     */
    @PostMapping("/api/detection/protocol/delete")
    Resp delete(@RequestBody @Valid ProtocolDeleteParam param);

}
