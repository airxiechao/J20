package com.airxiechao.j20.detection.rest;

import com.airxiechao.j20.common.api.pojo.constant.ConstRespCode;
import com.airxiechao.j20.common.api.pojo.rest.PageData;
import com.airxiechao.j20.common.api.pojo.rest.Resp;
import com.airxiechao.j20.common.api.pojo.vo.PageVo;
import com.airxiechao.j20.detection.api.pojo.protocol.Protocol;
import com.airxiechao.j20.detection.api.rest.IProtocolController;
import com.airxiechao.j20.detection.api.rest.param.*;
import com.airxiechao.j20.detection.api.service.IProtocolService;
import com.airxiechao.j20.detection.service.ProtocolService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.annotation.RequestScope;

import java.util.List;

@Slf4j
@RestController
@RequestScope
public class ProtocolController implements IProtocolController {

    private IProtocolService protocolService = new ProtocolService();

    @Override
    public Resp<Protocol> add(ProtocolAddParam param) {
        log.info("创建协议：{}", param);

        try {
            if(protocolService.exists(param.getCode())){
                throw new Exception("协议已存在");
            }

            Protocol protocol = new Protocol(param.getCode().toUpperCase(), param.getFieldSchema());
            protocol = protocolService.add(protocol);
            return new Resp<>(ConstRespCode.OK, null, protocol);
        }catch (Exception e){
            log.error("创建协议发生错误", e);
            return new Resp<>(ConstRespCode.ERROR, e.getMessage(), null);
        }
    }

    @Override
    public Resp update(ProtocolUpdateParam param) {
        log.info("修改协议：{}", param);

        try {
            if(!protocolService.exists(param.getCode())){
                throw new Exception("协议不存在");
            }

            Protocol protocol = new Protocol(param.getCode().toUpperCase(), param.getFieldSchema());
            protocolService.update(protocol);
            return new Resp<>(ConstRespCode.OK, null, null);
        }catch (Exception e){
            log.error("修改协议发生错误", e);
            return new Resp<>(ConstRespCode.ERROR, e.getMessage(), null);
        }
    }

    @Override
    public Resp<Protocol> get(ProtocolGetParam param) {
        log.info("查询协议：{}", param);

        try {
            if(!protocolService.exists(param.getCode())){
                throw new Exception("协议不存在");
            }

            Protocol protocol = protocolService.get(param.getCode());
            return new Resp<>(ConstRespCode.OK, null, protocol);
        }catch (Exception e){
            log.error("查询协议发生错误", e);
            return new Resp<>(ConstRespCode.ERROR, e.getMessage(), null);
        }
    }

    @Override
    public Resp<PageData<Protocol>> list(ProtocolListParam param) {
        log.info("查询协议列表：{}", param);

        try {
            if(null == param){
                param = new ProtocolListParam();
            }

            PageVo<Protocol> page = protocolService.list(param.getCode(), param.getCurrent(), param.getSize(), param.getOrderBy(), param.getOrderAsc());
            PageData<Protocol> data = new PageData<>(param.getCurrent(), param.getSize(), page.getTotal(), page.getPage());
            return new Resp<>(ConstRespCode.OK, null, data);
        }catch (Exception e){
            log.error("查询协议列表发生错误", e);
            return new Resp<>(ConstRespCode.ERROR, e.getMessage(), null);
        }
    }

    @Override
    public Resp<List<Protocol>> all(ProtocolListParam param) {
        log.info("查询所有协议列表：{}", param);

        try {
            if(null == param){
                param = new ProtocolListParam();
            }

            List<Protocol> list = protocolService.list(param.getCode());
            return new Resp<>(ConstRespCode.OK, null, list);
        }catch (Exception e){
            log.error("查询所有协议列表发生错误", e);
            return new Resp<>(ConstRespCode.ERROR, e.getMessage(), null);
        }
    }

    @Override
    public Resp delete(ProtocolDeleteParam param) {
        log.info("删除协议：{}", param);

        try {
            for (String code : param.getCode().split(",")) {
                if(protocolService.exists(code)){
                    protocolService.delete(code);
                }
            }

            return new Resp<>(ConstRespCode.OK, null, null);
        }catch (Exception e){
            log.error("删除协议发生错误", e);
            return new Resp<>(ConstRespCode.ERROR, e.getMessage(), null);
        }
    }
}
