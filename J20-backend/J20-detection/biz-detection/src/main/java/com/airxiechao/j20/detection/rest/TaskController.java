package com.airxiechao.j20.detection.rest;

import com.airxiechao.j20.common.api.pojo.constant.ConstRespCode;
import com.airxiechao.j20.common.api.pojo.rest.PageData;
import com.airxiechao.j20.common.api.pojo.rest.Resp;
import com.airxiechao.j20.common.api.pojo.vo.PageVo;
import com.airxiechao.j20.detection.api.pojo.task.Task;
import com.airxiechao.j20.detection.api.rest.ITaskController;
import com.airxiechao.j20.detection.api.rest.param.*;
import com.airxiechao.j20.detection.api.service.ITaskService;
import com.airxiechao.j20.detection.service.TaskService;
import com.airxiechao.j20.detection.util.JobUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.annotation.RequestScope;

@Slf4j
@RestController
@RequestScope
public class TaskController implements ITaskController {

    private ITaskService taskService = new TaskService();

    @Override
    public Resp<Task> add(TaskAddParam param) {
        log.info("创建任务：{}", param);

        try {
            if(param.getRules().isEmpty()){
                throw new Exception("任务规则为空");
            }

            if(taskService.existsByName(param.getName())){
                throw new Exception("任务名称已存在");
            }

            Task task = new Task(
                    param.getName(),
                    param.getDescription(),
                    param.getSrcDataSourceId(),
                    param.getStartingOffsetStrategy(),
                    param.getRules());

            task = taskService.add(task);

            // 启动
            boolean started = false;
            try {
                taskService.startJob(task.getId());
                started = true;
            }catch (Exception e){
                log.error("启动任务发生错误", e);
            }

            return new Resp<>(ConstRespCode.OK, started ? null : "任务已创建，但启动时发生错误", task);
        }catch (Exception e){
            log.error("创建任务发生错误", e);
            return new Resp<>(ConstRespCode.ERROR, e.getMessage(), null);
        }
    }

    @Override
    public Resp update(TaskUpdateParam param) {
        log.info("修改任务：{}", param);

        try {
            Task old = taskService.get(param.getId());

            if(param.getRules().isEmpty()){
                throw new Exception("任务规则为空");
            }

            if(!param.getName().equals(old.getName())) {
                if (taskService.existsByName(param.getName())) {
                    throw new Exception("任务名称已存在");
                }
            }

            Task task = new Task(
                    param.getId(),
                    param.getName(),
                    param.getDescription(),
                    param.getSrcDataSourceId(),
                    param.getStartingOffsetStrategy(),
                    param.getRules());

            taskService.update(task);
            return new Resp<>(ConstRespCode.OK, null, task);
        }catch (Exception e){
            log.error("修改任务发生错误", e);
            return new Resp<>(ConstRespCode.ERROR, e.getMessage(), null);
        }
    }

    @Override
    public Resp<Task> get(TaskGetParam param) {
        log.info("查询任务：{}", param);

        try {
            Task task = taskService.get(param.getId());
            return new Resp<>(ConstRespCode.OK, null, task);
        }catch (Exception e){
            log.error("查询任务发生错误", e);
            return new Resp<>(ConstRespCode.ERROR, e.getMessage(), null);
        }
    }

    @Override
    public Resp<PageData<Task>> list(TaskListParam param) {
        log.info("查询任务列表：{}", param);

        try {
            PageVo<Task> page = taskService.list(param.getName(),
                    param.getCurrent(), param.getSize(), param.getOrderBy(), param.getOrderAsc());
            PageData<Task> data = new PageData<>(param.getCurrent(), param.getSize(), page.getTotal(), page.getPage());
            return new Resp<>(ConstRespCode.OK, null, data);
        }catch (Exception e){
            log.error("查询任务列表发生错误", e);
            return new Resp<>(ConstRespCode.ERROR, e.getMessage(), null);
        }
    }

    @Override
    public Resp delete(TaskDeleteParam param) {
        log.info("删除任务：{}", param);

        try {
            for(String id : param.getId().split(",")){
                Task task = taskService.get(id);
                if(!JobUtil.isStopped(task.getJobStatus())){
                    throw new Exception("任务正在运行");
                }
            }

            for (String id : param.getId().split(",")) {
                if(taskService.exists(id)){
                    taskService.delete(id);
                }
            }

            return new Resp<>(ConstRespCode.OK, null, null);
        }catch (Exception e){
            log.error("删除任务发生错误", e);
            return new Resp<>(ConstRespCode.ERROR, e.getMessage(), null);
        }
    }

    @Override
    public Resp start(TaskStartParam param) {
        log.info("启动任务：{}", param);

        try {
            Task task = taskService.get(param.getId());
            if(!JobUtil.isStopped(task.getJobStatus())){
                throw new Exception("任务正在运行");
            }

            taskService.startJob(param.getId());
            return new Resp<>(ConstRespCode.OK, null, null);
        }catch (Exception e){
            log.error("启动任务发生错误", e);
            return new Resp<>(ConstRespCode.ERROR, e.getMessage(), null);
        }
    }

    @Override
    public Resp stop(TaskStopParam param) {
        log.info("停止任务：{}", param);

        try {
            Task task = taskService.get(param.getId());
            if(JobUtil.isStopped(task.getJobStatus())){
                throw new Exception("任务已停止");
            }

            taskService.stopJob(param.getId());
            return new Resp<>(ConstRespCode.OK, null, null);
        }catch (Exception e){
            log.error("停止任务发生错误", e);
            return new Resp<>(ConstRespCode.ERROR, e.getMessage(), null);
        }
    }
}
