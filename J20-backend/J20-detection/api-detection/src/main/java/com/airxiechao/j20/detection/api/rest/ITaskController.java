package com.airxiechao.j20.detection.api.rest;

import com.airxiechao.j20.common.api.pojo.rest.PageData;
import com.airxiechao.j20.common.api.pojo.rest.Resp;
import com.airxiechao.j20.detection.api.pojo.task.Task;
import com.airxiechao.j20.detection.api.rest.param.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * 任务请求接口
 */
public interface ITaskController {
    /**
     * 添加任务
     * @param param 参数
     * @return 新增任务
     */
    @PostMapping("/api/detection/task/add")
    Resp<Task> add(@RequestBody @Valid TaskAddParam param);

    /**
     * 修改任务
     * @param param 参数
     * @return 响应
     */
    @PostMapping("/api/detection/task/update")
    Resp update(@RequestBody @Valid TaskUpdateParam param);

    /**
     * 查询任务
     * @param param 参数
     * @return 任务
     */
    @PostMapping("/api/detection/task/get")
    Resp<Task> get(@RequestBody @Valid TaskGetParam param);

    /**
     * 查询任务列表
     * @param param 参数
     * @return 任务列表
     */
    @PostMapping("/api/detection/task/list")
    Resp<PageData<Task>> list(@RequestBody(required = false) @Valid TaskListParam param);

    /**
     * 删除任务
     * @param param 参数
     * @return 响应
     */
    @PostMapping("/api/detection/task/delete")
    Resp delete(@RequestBody @Valid TaskDeleteParam param);

    /**
     * 启动任务
     * @param param 参数
     * @return 响应
     */
    @PostMapping("/api/detection/task/start")
    Resp start(@RequestBody @Valid TaskStartParam param);

    /**
     * 停止任务
     * @param param 参数
     * @return 响应
     */
    @PostMapping("/api/detection/task/stop")
    Resp stop(@RequestBody @Valid TaskStopParam param);
}
