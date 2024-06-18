package com.airxiechao.j20.detection.job;

import com.airxiechao.j20.detection.api.pojo.config.JobConfig;
import com.airxiechao.j20.detection.api.pojo.job.TaskJob;
import com.airxiechao.j20.detection.api.pojo.rule.Rule;
import com.airxiechao.j20.detection.api.pojo.task.Task;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 规则执行器的工厂实现
 */
@Slf4j
public class TaskJobFactory {
    private static TaskJobFactory instance = new TaskJobFactory();
    private Map<String, Class<? extends AbstractTaskJob>> ruleJobClassMap = new HashMap<>();

    private TaskJobFactory(){
        registerRuleJobClass();
    }

    public static TaskJobFactory getInstance(){
        return instance;
    }

    /**
     * 注册规则的实现类
     */
    private void registerRuleJobClass(){
        String pkg = this.getClass().getPackage().getName();
        Reflections reflections = new Reflections(pkg);
        Set<Class<? extends AbstractTaskJob>> subTypes = reflections.getSubTypesOf(AbstractTaskJob.class);
        subTypes.forEach(t -> {
            TaskJob taskJob = t.getAnnotation(TaskJob.class);
            String taskType = taskJob.value();
            ruleJobClassMap.put(taskType, t);

            log.info("注册规则任务[{}]->[{}]", taskType, t);
        });
    }

    /**
     * 创建规则执行器
     * @param task 任务
     * @param config 任务配置
     * @param rule 规则
     * @return 规则执行器
     */
    public AbstractTaskJob createJob(Task task, JobConfig config, Rule rule){
        String ruleType = rule.getCriteriaType();
        Class<? extends AbstractTaskJob> ruleJobClass = ruleJobClassMap.get(ruleType);
        if(null == ruleJobClass){
            throw new RuntimeException(String.format("规则类型[%s]不支持", ruleType));
        }

        try {
            AbstractTaskJob ruleJob = ruleJobClass.getDeclaredConstructor(Task.class, JobConfig.class).newInstance(task, config);
            return ruleJob;
        }catch (Exception e){
            throw new RuntimeException(String.format("规则类型[%s]创建发生错误", ruleType));
        }
    }
}
