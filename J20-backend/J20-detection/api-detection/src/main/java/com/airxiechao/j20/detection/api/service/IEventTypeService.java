package com.airxiechao.j20.detection.api.service;

import com.airxiechao.j20.common.api.pojo.exception.NotFoundException;
import com.airxiechao.j20.common.api.pojo.vo.TreeNodeVo;
import com.airxiechao.j20.detection.api.pojo.event.EventType;
import com.airxiechao.j20.detection.api.pojo.event.EventTypeNumTreeNodeVo;

import java.util.List;

/**
 * 事件类型服务接口
 */
public interface IEventTypeService {
    /**
     * 查询事件类型
     * @param id ID
     * @return 事件类型
     */
    EventType get(String id) throws NotFoundException;

    /**
     * 根据ID判断是否存在
     * @param id ID
     * @return 是否存在
     */
    boolean exists(String id);

    /**
     * 根据类型名称判断是否已存在
     * @param name 事件名称
     * @return 是否存在
     */
    boolean existsByName(String name);

    /**
     * 查询事件类型的树形列表
     * @param name 名称
     * @return 事件类型的树形列表
     */
    List<EventTypeNumTreeNodeVo> listAsTree(String name);

    /**
     * 查询事件类型的列表
     * @param name 名称
     * @return 事件类型的列表
     */
    List<EventType> listAsList(String name);

    /**
     * 添加事件类型
     * @param eventType 事件类型
     * @return 事件类型
     */
    EventType add(EventType eventType);

    /**
     * 更新事件类型
     * @param eventType 事件类型
     */
    void update(EventType eventType);

    /**
     * 删除事件类型
     * @param id ID
     */
    void delete(String id);
}