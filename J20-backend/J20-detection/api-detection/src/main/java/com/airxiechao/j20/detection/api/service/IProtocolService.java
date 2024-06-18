package com.airxiechao.j20.detection.api.service;

import com.airxiechao.j20.common.api.pojo.exception.NotFoundException;
import com.airxiechao.j20.common.api.pojo.vo.PageVo;
import com.airxiechao.j20.detection.api.pojo.protocol.Protocol;

import java.util.List;

/**
 * 日志协议服务接口
 */
public interface IProtocolService {
    /**
     * 查询日志协议
     * @param code 编码
     * @return 日志协议
     */
    Protocol get(String code) throws NotFoundException;

    /**
     * 根据编码判断是否存在
     * @param code 编码
     * @return 是否存在
     */
    boolean exists(String code);

    /**
     * 查询日志协议列表
     * @param code 编码
     * @param pageNo 页数
     * @param pageSize 页大小
     * @param orderBy 排序字段
     * @param orderAsc 是否升序排序
     * @return 日志协议列表
     */
    PageVo<Protocol> list(String code, Integer pageNo, Integer pageSize, String orderBy, Boolean orderAsc);

    /**
     * 查询所有日志协议列表
     * @param code 编码
     * @return 日志协议列表
     */
    List<Protocol> list(String code);

    /**
     * 添加日志协议
     * @param protocol 日志协议
     * @return 日志协议
     */
    Protocol add(Protocol protocol);

    /**
     * 更新日志协议
     * @param protocol 日志协议
     */
    void update(Protocol protocol);

    /**
     * 删除日志协议
     * @param code 编码
     */
    void delete(String code);
}