package com.airxiechao.j20.detection.api.service;

import com.airxiechao.j20.common.api.pojo.exception.NotFoundException;
import com.airxiechao.j20.common.api.pojo.vo.PageVo;
import com.airxiechao.j20.detection.api.pojo.datasource.KafkaDataSource;

import java.util.List;

/**
 * 数据源服务接口
 */
public interface IDataSourceService {

    /**
     * 添加数据源
     * @param dataSource 数据源
     * @return 新增的数据源
     */
    KafkaDataSource add(KafkaDataSource dataSource) throws Exception;

    /**
     * 修改数据源
     * @param dataSource 数据源
     */
    void update(KafkaDataSource dataSource) throws Exception;

    /**
     * 根据ID判断是否存在
     * @param id ID
     * @return 是否存在
     */
    boolean exists(String id);

    /**
     * 检查队列名是否存在
     * @param topic 队列名
     * @return 是否存在
     */
    boolean existsByTopic(String topic);

    /**
     * 查询数据源
     * @param id ID
     * @return 数据源
     */
    KafkaDataSource get(String id) throws NotFoundException;

    /**
     * 查询数据源列表
     * @param name 名称
     * @param pageNo 页数
     * @param pageSize 页大小
     * @param orderBy 排序字段
     * @param orderAsc 是否升序排序
     * @return 数据源列表
     */
    PageVo<KafkaDataSource> list(String name, Integer pageNo, Integer pageSize, String orderBy, Boolean orderAsc);

    /**
     * 查询所有数据源列表
     * @param name 名称
     * @return 数据源
     */
    List<KafkaDataSource> list(String name);

    /**
     * 如果数据源的Topic不存在，则创建
     * @param id 数据源ID
     */
    void createTopicIfNotExists(String id) throws NotFoundException;

    /**
     * 删除数据源
     * @param id ID
     */
    void delete(String id);

}
