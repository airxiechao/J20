package com.airxiechao.j20.detection.api.service;

import com.airxiechao.j20.common.api.pojo.event.Event;
import com.airxiechao.j20.common.api.pojo.vo.PageVo;

import java.util.Date;
import java.util.List;

/**
 * 事件服务接口
 */
public interface IEventService {

    /**
     * 查询事件
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @param eventTypeId 事件类型ID
     * @param taskId 任务
     * @param ruleId 规则ID
     * @param content 事件内容
     * @param level 事件级别
     * @param pageNo 页数
     * @param pageSize 页大小
     * @param orderBy 排序字段
     * @param orderAsc 是否升序
     * @return 事件列表
     * @throws Exception 查询异常
     */
    PageVo<Event> list(Date beginTime, Date endTime, String eventTypeId, String taskId, String ruleId, String content, String level,
                       Integer pageNo, Integer pageSize, String orderBy, Boolean orderAsc) throws Exception;

    /**
     * 查询特定事件类型的事件
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @param eventTypeId 事件类型ID
     * @param query 查询 lucene 语句
     * @param pageNo 页数
     * @param pageSize 页大小
     * @param orderBy 排序字段
     * @param orderAsc 是否升序
     * @return 事件列表
     * @throws Exception 查询异常
     */
    PageVo<Event> listByProperty(Date beginTime, Date endTime, String eventTypeId, String query,
                       Integer pageNo, Integer pageSize, String orderBy, Boolean orderAsc) throws Exception;

    /**
     * 获取事件
     * @param id ID
     * @param timestamp 时间戳
     * @return 事件
     * @throws Exception 查询异常
     */
    Event get(String id, Long timestamp) throws Exception;

    /**
     * 批量添加事件
     * @param events 事件列表
     * @throws Exception 添加异常
     */
    void bulkAdd(List<Event> events) throws Exception;

    /**
     * 删除某时间之前事件
     * @param time 时间
     * @throws Exception 删除异常
     */
    void deleteUntil(Date time) throws Exception;
}
