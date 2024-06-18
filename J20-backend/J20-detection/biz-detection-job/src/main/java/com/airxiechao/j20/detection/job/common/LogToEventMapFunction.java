package com.airxiechao.j20.detection.job.common;

import com.airxiechao.j20.common.api.pojo.event.Event;
import com.airxiechao.j20.common.api.pojo.log.Log;
import com.airxiechao.j20.common.template.StringTemplate;
import com.airxiechao.j20.common.util.FieldUtil;
import com.airxiechao.j20.detection.api.pojo.config.JobConfig;
import com.airxiechao.j20.detection.api.pojo.event.SchemaField;
import com.airxiechao.j20.detection.api.pojo.rule.RuleOutput;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.functions.MapFunction;

import java.util.*;

/**
 * 日志转事件的实现
 */
@Slf4j
public class LogToEventMapFunction implements MapFunction<Log, Event> {

    private String taskId;
    private String taskName;
    private String ruleId;
    private String ruleName;
    private RuleOutput outputEvent;

    public LogToEventMapFunction(String taskId, String taskName, String ruleId, String ruleName,
                                 RuleOutput outputEvent, JobConfig config
    ) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.ruleId = ruleId;
        this.ruleName = ruleName;
        this.outputEvent = outputEvent;
    }

    @Override
    public Event map(Log value) throws Exception {
        Event event = new Event();

        event.setId(UUID.randomUUID().toString());
        event.setTimestamp(System.currentTimeMillis());

        event.setEventTypeId(outputEvent.getEventTypeId());
        event.setEventTypeName(outputEvent.getEventTypeName());
        event.setLevel(outputEvent.getLevel());
        event.setTaskId(taskId);
        event.setTaskName(taskName);
        event.setRuleId(ruleId);
        event.setRuleName(ruleName);

        List<Log> logs = new ArrayList<>();
        logs.add(value);
        event.setOriginalLog(logs);
        event.setFeature(value.getFeature());
        event.setWindowOut(value.getWindowOut());
        event.setWindowStart(value.getWindowStart());
        event.setWindowEnd(value.getWindowEnd());

        // 映射属性
        List<SchemaField> propertyFields = outputEvent.getPropertyFields();
        JSONObject property = new JSONObject();
        if(null != propertyFields && !propertyFields.isEmpty()){
            for (SchemaField field : propertyFields) {
                String propertyField = field.getKey();
                String valueField = field.getValue();

                Object propertyValue = renderFieldValue(valueField, value);

                FieldUtil.setFieldValue(property, propertyField, propertyValue);
            }
        }

        event.setProperty(property);

        event.setMessage(renderMessageValue(outputEvent.getMessageTemplate(), value, property));

        // 设置 ID
        String idTemplate = outputEvent.getIdTemplate();
        if (StringUtils.isNotBlank(idTemplate)) {
            try {
                event.setId(new StringTemplate(idTemplate).render(property));
            } catch (Exception e) {
                log.error("生成事件ID发生错误", e);
            }
        }

        log.info("发现事件 [{}]", event);

        return event;
    }

    /**
     * 渲染事件内容
     * @param messageTemplate 事件内容模板
     * @param value 原始日志
     * @param property 事件属性
     * @return 事件类容
     */
    private String renderMessageValue(String messageTemplate, Log value, JSONObject property){
        String content = "";
        if(StringUtils.isNotBlank(messageTemplate)) {
            try {
                JSONObject data = extendLog(value, property);
                content = FieldUtil.renderToValue(messageTemplate, data);
            }catch (Exception e){
                log.error("渲染日志到内容发生错误", e);
            }
        }

        return content;
    }

    /**
     * 渲染事件字段值
     * @param field 字段
     * @param value 原始日志
     * @return 事件字段值
     */
    private Object renderFieldValue(String field, Log value){
        Object fieldValue = null;
        if(StringUtils.isNotBlank(field)) {
            JSONObject data = extendLog(value, null);
            fieldValue = FieldUtil.renderToField(field, data, data);
        }
        
        return fieldValue;
    }

    /**
     * 扩展事件对象
     * @param value 原始日志
     * @param property 事件属性
     * @return 事件扩展对象
     */
    private JSONObject extendLog(Log value, JSONObject property){
        JSONObject data = value.getData();
        data.put("windowOut", value.getWindowOut());
        data.put("windowStart", value.getWindowStart());
        data.put("windowEnd", value.getWindowEnd());
        if(null != property) {
            data.putAll(property);
        }

        return data;
    }
}
