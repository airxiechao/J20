package com.airxiechao.j20.detection.service;

import com.airxiechao.j20.common.api.pojo.constant.ConstOrderType;
import com.airxiechao.j20.common.api.pojo.exception.NotFoundException;
import com.airxiechao.j20.common.api.pojo.vo.PageVo;
import com.airxiechao.j20.common.kafka.KafkaAdmin;
import com.airxiechao.j20.common.kafka.KafkaClient;
import com.airxiechao.j20.common.util.ApplicationContextUtil;
import com.airxiechao.j20.common.util.UuidUtil;
import com.airxiechao.j20.detection.api.pojo.constant.ConstJobStatus;
import com.airxiechao.j20.detection.api.pojo.event.EventType;
import com.airxiechao.j20.detection.api.pojo.rule.Rule;
import com.airxiechao.j20.detection.api.pojo.rule.RuleAggregation;
import com.airxiechao.j20.detection.api.pojo.rule.RuleFrequency;
import com.airxiechao.j20.detection.api.pojo.rule.RuleOutput;
import com.airxiechao.j20.detection.api.pojo.task.Task;
import com.airxiechao.j20.detection.api.service.IEventTypeService;
import com.airxiechao.j20.detection.api.service.IRuleService;
import com.airxiechao.j20.detection.api.service.ITaskService;
import com.airxiechao.j20.detection.db.record.RuleRecord;
import com.airxiechao.j20.detection.db.reposiroty.IRuleRepository;
import com.airxiechao.j20.detection.job.manager.RuleJobManager;
import com.airxiechao.j20.detection.kafka.KafkaManager;
import com.airxiechao.j20.detection.minio.MinioManager;
import com.airxiechao.j20.detection.util.JobUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class RuleService implements IRuleService {

    private IRuleRepository ruleRepository = ApplicationContextUtil.getContext().getBean(IRuleRepository.class);

    @Override
    public PageVo<Rule> list(String name, String criteriaType, String outputEventTypeId, Integer pageNo, Integer pageSize, String orderBy, Boolean orderAsc) {
        if(StringUtils.isBlank(orderBy)){
            orderBy =  "createTime";
        }
        String orderType = (null != orderAsc && orderAsc) ? ConstOrderType.ASC : ConstOrderType.DESC;

        Sort sort;
        if(ConstOrderType.ASC.equalsIgnoreCase(orderType)){
            sort = Sort.by(orderBy).ascending();
        }else{
            sort = Sort.by(orderBy).descending();
        }

        if(null == pageNo){
            pageNo = 1;
        }
        if(null == pageSize){
            pageSize = 20;
        }

        PageRequest pageRequest = PageRequest.of(pageNo - 1, pageSize, sort);

        Page<RuleRecord> page = ruleRepository.findAll((Specification<RuleRecord>) (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if(StringUtils.isNotBlank(name)) {
                Expression<Boolean> contains = criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), String.format("%%%s%%", name.toLowerCase()));
                predicates.add(criteriaBuilder.and(criteriaBuilder.and(), contains));
            }
            if(StringUtils.isNotBlank(criteriaType)) {
                predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("criteriaType"), criteriaType)));
            }
//            if(StringUtils.isNotBlank(outputEventTypeId)) {
//                predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("outputEventTypeId"), outputEventTypeId)));
//            }
            if(StringUtils.isNotBlank(outputEventTypeId)) {
                Expression<Boolean> starting = criteriaBuilder.like(root.get("outputEventTypeId"), String.format("%s%%", outputEventTypeId));
                predicates.add(criteriaBuilder.and(criteriaBuilder.and(), starting));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }, pageRequest);

        List<Rule> list = page.stream().map(this::buildRule).collect(Collectors.toList());

        return new PageVo<>(page.getTotalElements(), list);
    }

    @Override
    public boolean hasId(String id) {
        return ruleRepository.existsById(id);
    }

    @Override
    public Rule get(String id) throws NotFoundException {
        Optional<RuleRecord> optRuleRecord = ruleRepository.findById(id);
        if(optRuleRecord.isEmpty()){
            throw new NotFoundException();
        }

        Rule rule = buildRule(optRuleRecord.get());
        return rule;
    }

    @Override
    public boolean exists(String id) {
        return ruleRepository.existsById(id);
    }

    @Override
    public boolean existsByName(String name) {
        return ruleRepository.existsByNameIgnoreCase(name);
    }

    @Override
    public Rule getByName(String name) throws NotFoundException {
        Optional<RuleRecord> optRuleRecord = ruleRepository.findByNameIgnoreCase(name);
        if(optRuleRecord.isEmpty()){
            throw new NotFoundException();
        }

        Rule rule = buildRule(optRuleRecord.get());
        return rule;
    }

    @Override
    public Rule add(Rule rule) throws Exception {
        // 检查规则
        checkRule(rule);

        // 补充规则
        fillRule(rule);

        // 添加规则
        Date now = new Date();
        rule.setCreateTime(now);
        rule.setLastUpdateTime(now);

        RuleRecord record = buildRuleRecord(rule);
        return buildRule(ruleRepository.save(record));
    }

    @Override
    public void update(Rule rule) throws Exception {
        // 检查规则
        checkRule(rule);

        // 补充规则
        fillRule(rule);

        // 修改规则
        Rule old = get(rule.getId());
        rule.setCreateTime(old.getCreateTime());
        rule.setLastUpdateTime(new Date());

        RuleRecord record = buildRuleRecord(rule);
        ruleRepository.save(record);
    }

    @Override
    public void delete(String id) throws Exception {
        ITaskService taskService = new TaskService();

        // 检查是否有规则正在使用
        List<Task> tasks = taskService.list(null);
        for (Task task : tasks) {
            List<Rule> rules = task.getRules();
            if(null != rules){
                for(Rule rule : rules){
                    if(id.equals(rule.getId())){
                        throw new Exception(String.format("规则正被任务[%s]使用，无法删除", task.getName()));
                    }
                }
            }
        }

        // 删除
        ruleRepository.deleteById(id);
    }

    @Override
    public List<JSONObject> test(String id, List<JSONObject> input) throws Exception {
        Rule rule = get(id);
        String runId = UuidUtil.random();
        String workingDir = String.format("/rule/%s/%s", id, runId);

        log.info("开始测试规则[{}#{}]，工作路径：{}", rule.getId(), rule.getName(), workingDir);

        // 1. 将 input 上传到 minio
        String inputPath = String.format("%s/input", workingDir);
        String inputContent = input.stream().map(i -> i.toJSONString()).collect(Collectors.joining("\n"));
        try(InputStream inputStream = new ByteArrayInputStream(inputContent.getBytes(StandardCharsets.UTF_8))){
            MinioManager.getInstance().getHelper().upload(inputPath, inputStream);
        }

        // 2. 创建结果 Kafka Topic
        String dstTopic = String.format("j20-detection-rule-test-%s", runId);
        try(KafkaAdmin kafkaAdmin = KafkaManager.getInstance().getAdmin()) {
            kafkaAdmin.createTopic(dstTopic, 1, 1);
        }

        try{
            // 3. 启动测试任务
            log.info("启动测试规则[{}]任务", workingDir);
            String jobId = RuleJobManager.getInstance().run(rule, dstTopic, workingDir);

            // 4. 等待任务结束
            while (true){
                String state = RuleJobManager.getInstance().getState(jobId);
                if(JobUtil.isStopped(state)){
                    log.info("测试规则[{}]任务结束，状态：{}", workingDir, state);
                    if(!ConstJobStatus.FINISHED.equals(state)){
                        throw new Exception("测试规则任务发生错误");
                    }

                    break;
                }

                Thread.sleep(1000);
            }

            // 5. 读取结果
            List<JSONObject> output = new ArrayList<>();
            try(KafkaClient client = KafkaManager.getInstance().getClient(dstTopic, this.getClass().getName())){
                client.consumeUtilEmpty(r -> {
                    String out = r.value();
                    output.add(JSON.parseObject(out));
                });
            }

            log.info("测试规则[{}]任务输出条数：{}", workingDir, output.size());

            return output;

        } finally {
            // 6. 清理 minio
            MinioManager.getInstance().getHelper().remove(inputPath);

            // 7. 清理结果 Kafka Topic
            try(KafkaAdmin kafkaAdmin = KafkaManager.getInstance().getAdmin()) {
                kafkaAdmin.deleteTopic(dstTopic);
            }
        }
    }

    private void checkRule(Rule rule) throws Exception {
        RuleOutput output = rule.getOutput();
        if(null == output){
            throw new Exception("没有设置输出");
        }
    }

    private void fillRule(Rule rule) throws Exception {
        IEventTypeService eventTypeService = new EventTypeService();

        // 补充事件输出
        RuleOutput output = rule.getOutput();
        if(null != output){
            String eventTypeId = output.getEventTypeId();
            if(StringUtils.isNotBlank(eventTypeId))
                try {
                    EventType eventType = eventTypeService.get(eventTypeId);
                    output.setEventTypeName(eventType.getName());
                    output.setLevel(eventType.getLevel());
                } catch (NotFoundException e) {
                    throw new Exception("输出事件类型不存在");
                }
        }
    }

    private Rule buildRule(RuleRecord record){
        Rule rule = new Rule(
                record.getId(),
                record.getName(),
                record.getDescription(),
                record.getProtocol(),
                record.getCriteriaType(),
                JSON.parseObject(record.getCriteria()),
                JSON.to(RuleOutput.class, record.getOutput()),
                JSON.to(RuleFrequency.class, record.getFrequency()),
                JSON.to(RuleAggregation.class, record.getAggregation()),
                record.getCreateTime(),
                record.getLastUpdateTime()
        );

        return rule;
    }

    private RuleRecord buildRuleRecord(Rule rule){
        RuleRecord record = new RuleRecord();
        record.setId(null != rule.getId() ? rule.getId() : UuidUtil.random());
        record.setName(rule.getName());
        record.setDescription(rule.getDescription());
        record.setCriteriaType(rule.getCriteriaType());
        record.setProtocol(rule.getProtocol());
        record.setCriteria(JSON.toJSONString(rule.getCriteria()));
        record.setOutput(JSON.toJSONString(rule.getOutput()));
        record.setFrequency(JSON.toJSONString(rule.getFrequency()));
        record.setAggregation(JSON.toJSONString(rule.getAggregation()));

        RuleOutput ruleOutput = rule.getOutput();
        if(null != ruleOutput) {
            record.setOutputEventTypeId(ruleOutput.getEventTypeId());
            record.setOutputEventTypeName(ruleOutput.getEventTypeName());
        }

        record.setCreateTime(rule.getCreateTime());
        record.setLastUpdateTime(rule.getLastUpdateTime());

        return record;
    }
}
