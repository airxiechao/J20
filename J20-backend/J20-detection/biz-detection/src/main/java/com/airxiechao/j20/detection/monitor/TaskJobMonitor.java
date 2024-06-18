package com.airxiechao.j20.detection.monitor;

import com.airxiechao.j20.detection.api.pojo.job.TaskJobMetric;
import com.airxiechao.j20.detection.api.pojo.task.Task;
import com.airxiechao.j20.detection.api.service.ITaskJobMetricService;
import com.airxiechao.j20.detection.api.service.ITaskService;
import com.airxiechao.j20.detection.job.DetectionConfigFactory;
import com.airxiechao.j20.detection.job.manager.TaskJobManager;
import com.airxiechao.j20.detection.service.TaskJobMetricService;
import com.airxiechao.j20.detection.service.TaskService;
import com.nextbreakpoint.flinkclient.api.ApiException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 任务监测器
 * 负责更新任务状态，记录任务处理性能，清理没有被任务关联的Flink任务实例。
 */
@Slf4j
public class TaskJobMonitor {
    @Getter
    private static final TaskJobMonitor instance = new TaskJobMonitor();

    private int periodSecond = DetectionConfigFactory.getInstance().get().getTaskJobMonitorPeriodSecond();

    private ITaskService taskService = new TaskService();
    private ITaskJobMetricService taskJobMetricService = new TaskJobMetricService();
    private ScheduledExecutorService scheduler;

    private TaskJobMonitor(){}

    public void start(){
        log.info("启动任务监视器");
        getScheduler().scheduleAtFixedRate(() -> {
            try{
                // 查询所有规则
                List<Task> tasks = taskService.list(null);
                for (Task task : tasks) {
                    String taskId = task.getId();
                    String jobId = task.getJobId();

                    if(StringUtils.isBlank(jobId)){
                        continue;
                    }

                    if(null == task.getJobLastUpdateTime() || ((System.currentTimeMillis() - task.getJobLastUpdateTime().getTime()) / 1000) >= periodSecond){
                        try{
                            // 查询指标
                            TaskJobMetric metric = TaskJobManager.getInstance().getMetric(taskId, jobId);

                            // 更新任务状态
                            taskService.updateJob(taskId, jobId, metric.getState(), metric.getNumLogIn(), metric.getNumEventOut());

                            // 保存指标
                            taskJobMetricService.add(metric);
                        }catch (Exception e){
                            if(e.getCause() instanceof ApiException && e.getCause().getMessage().trim().equals("Not Found")){
                                // 不处理
                            }else {
                                log.error("存储任务指标发生错误", e);
                            }
                        }
                    }
                }

                // 清理没有被识别规则关联的任务
                Set<String> allRuleJobId = tasks.stream()
                        .filter(r -> StringUtils.isNotBlank(r.getJobId()))
                        .map(r -> r.getJobId())
                        .collect(Collectors.toSet());
                TaskJobManager.getInstance().listRunningJobs("com.airxiechao.j20.detection.job.TaskJobRunner").stream()
                        .filter(jobId -> !allRuleJobId.contains(jobId)).forEach(jobId -> {
                            log.info("清理未关联任务: {}", jobId);
                            try {
                                TaskJobManager.getInstance().terminate(jobId);
                            } catch (Exception e) {
                                log.error("清理任务[{}]发生错误", jobId, e);
                            }
                        });
            }catch (Exception e){
                log.error("任务监视器发生错误" + e);
            }
        }, 0, periodSecond, TimeUnit.SECONDS);
    }

    public void stop() {
        scheduler.shutdownNow();
    }

    private ScheduledExecutorService getScheduler(){
        if(null == scheduler || scheduler.isShutdown()){
            scheduler = Executors.newSingleThreadScheduledExecutor((r) -> {
                Thread thread = new Thread(r);
                thread.setDaemon(true);
                return thread;
            });
        }

        return scheduler;
    }
}
