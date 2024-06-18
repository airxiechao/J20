package com.airxiechao.j20.detection.api.service;

import com.airxiechao.j20.common.api.pojo.exception.NotFoundException;
import com.airxiechao.j20.common.api.pojo.vo.PageVo;
import com.airxiechao.j20.detection.api.pojo.task.Task;

import java.util.List;

/**
 * 任务服务接口
 */
public interface ITaskService {
    /**
     * 查询任务列表
     * @param name 名称
     * @param pageNo 页数
     * @param pageSize 页大小
     * @param orderBy 排序字段
     * @param orderAsc 是否升序
     * @return 任务列表
     */
    PageVo<Task> list(String name, Integer pageNo, Integer pageSize, String orderBy, Boolean orderAsc);

    /**
     * 查询所有任务列表
     * @param name 名称
     * @return 任务列表
     */
    List<Task> list(String name);

    /**
     * 是否存在任务
     * @param id 任务ID
     * @return 是否存在
     */
    boolean exists(String id);

    /**
     * 查询任务
     * @param id 任务ID
     * @return 任务
     * @throws NotFoundException 任务不存在
     */
    Task get(String id) throws NotFoundException;

    /**
     * 是否存在名称
     * @param name 名称
     * @return 是否存在
     */
    boolean existsByName(String name);

    /**
     * 通过名称获取任务
     * @param name 名称
     * @return 任务
     * @throws NotFoundException 任务不存在
     */
    Task getByName(String name) throws NotFoundException;

    /**
     * 添加任务
     * @param task 任务
     * @return 任务
     */
    Task add(Task task) throws Exception;

    /**
     * 更新任务
     * @param task 任务
     */
    void update(Task task) throws Exception;

    /**
     * 删除任务
     * @param id ID
     * @throws Exception 删除异常
     */
    void delete(String id) throws Exception;

    /**
     * 更行任务的Flink任务实例
     * @param id 任务ID
     * @param jobId Flink任务实例ID
     * @param jobStatus 任务状态
     * @param jobNumLogIn 日志读入数
     * @param jobNumEventOut 事件输出数
     */
    void updateJob(String id, String jobId, String jobStatus, Long jobNumLogIn, Long jobNumEventOut);
    /**
     * 更行任务的Flink任务实例
     * @param id 任务ID
     * @param jobId Flink任务实例ID
     * @param jobStatus 任务状态
     */
    void updateJob(String id, String jobId, String jobStatus);

    /**
     * 启动任务执行
     * @param id ID
     * @throws Exception 启动异常
     */
    void startJob(String id) throws Exception;

    /**
     * 停止任务执行
     * @param id ID
     * @throws Exception 停止异常
     */
    void stopJob(String id) throws Exception;


}
