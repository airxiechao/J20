package com.airxiechao.j20.detection.service;

import com.airxiechao.j20.common.api.pojo.constant.ConstOrderType;
import com.airxiechao.j20.common.api.pojo.exception.NotFoundException;
import com.airxiechao.j20.common.api.pojo.vo.PageVo;
import com.airxiechao.j20.common.util.ApplicationContextUtil;
import com.airxiechao.j20.common.util.UuidUtil;
import com.airxiechao.j20.detection.api.pojo.constant.ConstJobStatus;
import com.airxiechao.j20.detection.api.pojo.event.EventType;
import com.airxiechao.j20.detection.api.pojo.exception.TaskJobFailedException;
import com.airxiechao.j20.detection.api.pojo.job.TaskJobMetric;
import com.airxiechao.j20.detection.api.pojo.rule.Rule;
import com.airxiechao.j20.detection.api.pojo.rule.RuleOutput;
import com.airxiechao.j20.detection.api.pojo.task.Task;
import com.airxiechao.j20.detection.api.service.IDataSourceService;
import com.airxiechao.j20.detection.api.service.IEventTypeService;
import com.airxiechao.j20.detection.api.service.IRuleService;
import com.airxiechao.j20.detection.api.service.ITaskService;
import com.airxiechao.j20.detection.db.record.TaskRecord;
import com.airxiechao.j20.detection.db.reposiroty.TaskRepository;
import com.airxiechao.j20.detection.job.manager.TaskJobManager;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
public class TaskService implements ITaskService {

    private TaskRepository taskRepository = ApplicationContextUtil.getContext().getBean(TaskRepository.class);

    @Override
    public PageVo<Task> list(String name, Integer pageNo, Integer pageSize, String orderBy, Boolean orderAsc) {
        if (StringUtils.isBlank(orderBy)) {
            orderBy = "createTime";
        }
        String orderType = (null != orderAsc && orderAsc) ? ConstOrderType.ASC : ConstOrderType.DESC;

        Sort sort;
        if (ConstOrderType.ASC.equalsIgnoreCase(orderType)) {
            sort = Sort.by(orderBy).ascending();
        } else {
            sort = Sort.by(orderBy).descending();
        }

        if (null == pageNo) {
            pageNo = 1;
        }
        if (null == pageSize) {
            pageSize = 20;
        }

        PageRequest pageRequest = PageRequest.of(pageNo - 1, pageSize, sort);

        Page<TaskRecord> page = taskRepository.findAll((Specification<TaskRecord>) (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.isNotBlank(name)) {
                Expression<Boolean> contains = criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), String.format("%%%s%%", name.toLowerCase()));
                predicates.add(criteriaBuilder.and(criteriaBuilder.and(), contains));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }, pageRequest);

        List<Task> list = page.stream().map(this::buildTask).collect(Collectors.toList());

        return new PageVo<>(page.getTotalElements(), list);
    }

    @Override
    public List<Task> list(String name) {
        List<TaskRecord> records;
        if(StringUtils.isNotBlank(name)){
            records = taskRepository.findAllByNameIgnoreCase(name);
        }else{

            records = StreamSupport
                    .stream(taskRepository.findAll().spliterator(), false)
                    .collect(Collectors.toList());
        }

        List<Task> tasks = records.stream().map(this::buildTask).collect(Collectors.toList());
        return tasks;
    }

    @Override
    public boolean exists(String id) {
        return taskRepository.existsById(id);
    }

    @Override
    public Task get(String id) throws NotFoundException {
        Optional<TaskRecord> opt = taskRepository.findById(id);
        if (opt.isEmpty()) {
            throw new NotFoundException();
        }

        Task task = buildTask(opt.get());
        return task;
    }

    @Override
    public boolean existsByName(String name) {
        return taskRepository.existsByNameIgnoreCase(name);
    }

    @Override
    public Task getByName(String name) throws NotFoundException {
        Optional<TaskRecord> opt = taskRepository.findByNameIgnoreCase(name);
        if (opt.isEmpty()) {
            throw new NotFoundException();
        }

        Task task = buildTask(opt.get());
        return task;
    }

    @Override
    public Task add(Task task) throws Exception {
        // 补充任务
        fillTask(task);

        // 检查任务
        checkTask(task);

        // 添加任务
        Date now = new Date();
        task.setJobStatus(ConstJobStatus.NOT_CREATED);
        task.setCreateTime(now);
        task.setLastUpdateTime(now);

        TaskRecord record = buildTaskRecord(task);
        task = buildTask(taskRepository.save(record));

        return task;
    }

    @Override
    public void update(Task task) throws Exception {
        // 补充任务
        fillTask(task);

        // 检查任务
        checkTask(task);

        // 修改任务
        Task old = get(task.getId());
        task.setJobId(old.getJobId());
        task.setJobStatus(old.getJobStatus());
        task.setJobNumLogIn(old.getJobNumLogIn());
        task.setJobNumEventOut(old.getJobNumEventOut());
        task.setJobLastUpdateTime(old.getJobLastUpdateTime());
        task.setCreateTime(old.getCreateTime());
        task.setLastUpdateTime(new Date());

        TaskRecord record = buildTaskRecord(task);
        taskRepository.save(record);
    }

    @Override
    public void delete(String id) throws Exception {
        // 停止任务
        stopJob(id);

        // 删除任务
        taskRepository.deleteById(id);
    }

    @Override
    public void updateJob(String id, String jobId, String jobStatus) {
        taskRepository.update((criteriaBuilder -> {
            CriteriaUpdate<TaskRecord> criteriaUpdate = criteriaBuilder.createCriteriaUpdate(TaskRecord.class);
            Root<TaskRecord> root = criteriaUpdate.from(TaskRecord.class);
            criteriaUpdate
                    .set(root.get("jobId"), jobId)
                    .set(root.get("jobStatus"), jobStatus)
                    .set(root.get("jobLastUpdateTime"), new Date())
                    .where(criteriaBuilder.equal(root.get("id"), id));
            return criteriaUpdate;
        }));
    }

    @Override
    public void updateJob(String id, String jobId, String jobStatus, Long jobNumLogIn, Long jobNumEventOut) {
        taskRepository.update((criteriaBuilder -> {
            CriteriaUpdate<TaskRecord> criteriaUpdate = criteriaBuilder.createCriteriaUpdate(TaskRecord.class);
            Root<TaskRecord> root = criteriaUpdate.from(TaskRecord.class);
            criteriaUpdate
                    .set(root.get("jobId"), jobId)
                    .set(root.get("jobStatus"), jobStatus)
                    .set(root.get("jobNumLogIn"), jobNumLogIn)
                    .set(root.get("jobNumEventOut"), jobNumEventOut)
                    .set(root.get("jobLastUpdateTime"), new Date())
                    .where(criteriaBuilder.equal(root.get("id"), id));
            return criteriaUpdate;
        }));
    }

    @Override
    public void startJob(String id) throws Exception {
        // 查询任务
        Task task = get(id);

        // 获取最新规则
        reloadTask(task);

        // 启动任务
        try {
            String jobId = TaskJobManager.getInstance().run(task);

            // 更新任务
            updateJob(id, jobId, ConstJobStatus.CREATING, 0L, 0L);

            // 推送一条任务的零指标
            try {
                new TaskJobMetricService().add(buildZeroTaskJobMetric(id, jobId));
            } catch (Exception e) {
                log.error("推送任务零指标发生错误", e);
            }
        } catch (Throwable e) {
            log.error("启动任务发生错误", e);
            updateJob(task.getId(), null, ConstJobStatus.FAILED);

            throw new TaskJobFailedException("启动任务发生错误", e);
        }
    }

    @Override
    public void stopJob(String id) throws Exception {
        // 查询任务
        Task task = get(id);

        // 结束任务
        String jobId = task.getJobId();
        if (null != jobId) {
            TaskJobManager.getInstance().terminate(jobId);

            // 更新任务
            updateJob(id, jobId, ConstJobStatus.CANCELLING);
        }
    }

    private void fillTask(Task task) throws Exception {
        IRuleService ruleService = new RuleService();

        // 补充规则
        List<Rule> rules = task.getRules();
        if (null != rules) {
            for (int i = 0; i < rules.size(); i++) {
                Rule rule = rules.get(i);

                String ruleId = rule.getId();
                if (StringUtils.isNotBlank(ruleId)) {
                    Rule fullRule = ruleService.get(ruleId);
                    rules.set(i, fullRule);
                }
            }
        }
    }

    private void reloadTask(Task task) throws Exception {
        IRuleService ruleService = new RuleService();

        // 重载规则
        List<Rule> rules = task.getRules();
        if (null != rules) {

            // 获取最新规则输出条件
            for (int i = 0; i < rules.size(); i++) {
                Rule rule = rules.get(i);
                String ruleId = rule.getId();
                if (StringUtils.isNotBlank(ruleId)) {
                    Rule newRule = ruleService.get(ruleId);
                    rules.set(i, newRule);
                }
            }
        }
    }

    private void checkTask(Task task) throws Exception {
        IDataSourceService dataSourceService = new DataSourceService();
        IRuleService ruleService = new RuleService();
        IEventTypeService eventTypeService = new EventTypeService();

        // 检查输入数据源
        if(StringUtils.isNotBlank(task.getSrcDataSourceId()) && !dataSourceService.exists(task.getSrcDataSourceId())){
            throw new Exception("输入数据源不存在");
        }

        // 检查规则
        List<Rule> rules = task.getRules();
        if (null != rules) {
            for (Rule rule : rules) {
                String ruleId = rule.getId();
                if (StringUtils.isNotBlank(ruleId)) {
                    rule = ruleService.get(ruleId);

                }

                // 检查输出事件是否存在
                RuleOutput outputEvent = rule.getOutput();
                if (null != outputEvent) {
                    try {
                        EventType eventType = eventTypeService.get(outputEvent.getEventTypeId());
                    } catch (NotFoundException e) {
                        throw new Exception(String.format("输出事件类型[%s]不存在", outputEvent.getEventTypeName()));
                    }
                }

            }
        }

    }

    private Task buildTask(TaskRecord record) {
        Task task = new Task(
                record.getId(),
                record.getName(),
                record.getDescription(),
                record.getSrcDataSourceId(),
                record.getStartingOffsetStrategy(),
                JSON.parseObject(record.getRules(), new TypeReference<>() {}),
                record.getJobId(),
                record.getJobStatus(),
                record.getJobNumLogIn(),
                record.getJobNumEventOut(),
                record.getJobLastUpdateTime(),
                record.getCreateTime(),
                record.getLastUpdateTime()
        );

        return task;
    }

    private TaskRecord buildTaskRecord(Task task) {
        TaskRecord record = new TaskRecord();
        record.setId(null != task.getId() ? task.getId() : UuidUtil.random());
        record.setName(task.getName());
        record.setDescription(task.getDescription());
        record.setSrcDataSourceId(task.getSrcDataSourceId());
        record.setStartingOffsetStrategy(task.getStartingOffsetStrategy());
        record.setRules(JSON.toJSONString(task.getRules()));
        record.setJobId(task.getJobId());
        record.setJobStatus(task.getJobStatus());
        record.setJobNumLogIn(task.getJobNumLogIn());
        record.setJobNumEventOut(task.getJobNumEventOut());
        record.setJobLastUpdateTime(task.getJobLastUpdateTime());

        record.setCreateTime(task.getCreateTime());
        record.setLastUpdateTime(task.getLastUpdateTime());

        return record;
    }

    private TaskJobMetric buildZeroTaskJobMetric(String ruleId, String jobId) {
        TaskJobMetric taskJobMetric = new TaskJobMetric();
        taskJobMetric.setRuleId(ruleId);
        taskJobMetric.setJobId(jobId);
        taskJobMetric.setTimestamp(new Date().getTime());
        taskJobMetric.setState(ConstJobStatus.CREATING);
        taskJobMetric.setNumLogIn(0L);
        taskJobMetric.setNumLogInPerSecond(0);
        taskJobMetric.setNumEventOut(0L);
        taskJobMetric.setNumEventOutDelta(0L);
        taskJobMetric.setNumEventOutPerSecond(0);

        return taskJobMetric;
    }
}
