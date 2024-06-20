package com.airxiechao.j20.detection.service;

import com.airxiechao.j20.common.api.pojo.event.Event;
import com.airxiechao.j20.common.api.pojo.exception.NotFoundException;
import com.airxiechao.j20.common.api.pojo.vo.PageVo;
import com.airxiechao.j20.common.es.EsClient;
import com.airxiechao.j20.common.util.UuidUtil;
import com.airxiechao.j20.detection.api.event.EventPropertyRef;
import com.airxiechao.j20.detection.api.log.OriginalLogRef;
import com.airxiechao.j20.detection.api.pojo.event.EventType;
import com.airxiechao.j20.detection.api.pojo.event.SchemaField;
import com.airxiechao.j20.detection.api.pojo.event.SchemaFieldValue;
import com.airxiechao.j20.detection.api.event.EventRef;
import com.airxiechao.j20.detection.api.service.IEventService;
import com.airxiechao.j20.detection.es.EsConfigFactory;
import com.airxiechao.j20.detection.es.EsManager;
import com.airxiechao.j20.detection.util.EventUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class EventService implements IEventService {

    private String eventIndexPrefix = EsConfigFactory.getInstance().get().getEventIndexPrefix();
    private String eventOriginalLogIndexPrefix = EsConfigFactory.getInstance().get().getEventOriginalLogIndexPrefix();
    private String eventPropertyIndexPrefix = EsConfigFactory.getInstance().get().getEventPropertyIndexPrefix();

    @Override
    public PageVo<Event> list(
            Date beginTime, Date endTime, String eventTypeId, String taskId, String ruleId, String content, String level,
            Integer pageNo, Integer pageSize, String orderBy, Boolean orderAsc
    ) throws Exception {
        Map<String, Object> mustTerm = new HashMap<>();
        Map<String, Object> mustMatch = new HashMap<>();
        Map<String, Object> mustRange = new HashMap<>();
        Map<String, Object> mustPrefix = new HashMap<>();

        List<Pair<String, Object>> shouldTerm = new ArrayList<>();
        List<Pair<String, Object>> shouldMatch = new ArrayList<>();
        List<Pair<String, Object>> shouldRange = new ArrayList<>();
        List<Pair<String, Object>> shouldPrefix = new ArrayList<>();

        Map<String, Object> timestampRange = new HashMap<>();
        timestampRange.put("gte", beginTime.getTime());
        timestampRange.put("lt", endTime.getTime());
        mustRange.put("timestamp", timestampRange);

        if (StringUtils.isNotBlank(eventTypeId)) {
            String[] eventTypes = eventTypeId.split(",");
            if (eventTypes.length == 1) {
                mustPrefix.put("eventTypeId.keyword", eventTypeId);
            } else {
                for (String eventType : eventTypes) {
                    shouldTerm.add(Pair.of("eventTypeId.keyword", eventType.trim()));
                }
            }
        }
        if (StringUtils.isNotBlank(taskId)) {
            mustTerm.put("taskId.keyword", taskId);
        }
        if (StringUtils.isNotBlank(ruleId)) {
            mustTerm.put("ruleId.keyword", ruleId);
        }
        if (StringUtils.isNotBlank(content)) {
            mustMatch.put("content", content);
        }
        if (StringUtils.isNotBlank(level)) {
            mustTerm.put("level.keyword", level);
        }

        if (null == orderBy) {
            orderBy = "timestamp";
            orderAsc = false;
        }
        if (null == pageNo || null == pageSize) {
            pageNo = 1;
            pageSize = 1000;
        }

        PageVo<Event> list = EsManager.getInstance().getClient().search(
                EventUtil.buildSearchEventIndex(beginTime, endTime),
                mustTerm, mustMatch, mustRange, mustPrefix,
                shouldTerm, shouldMatch, shouldRange, shouldPrefix,
                pageNo, pageSize, orderBy, orderAsc, Event.class);

        return list;
    }

    @Override
    public PageVo<Event> listByProperty(
            Date beginTime, Date endTime, String eventTypeId, String query,
            Integer pageNo, Integer pageSize, String orderBy, Boolean orderAsc
    ) throws Exception {
        if(StringUtils.isBlank(query)){
            query = String.format("timestamp:[%d TO %d]", beginTime.getTime(), endTime.getTime());
        }else{
            query = String.format("(%s) AND (timestamp:[%d TO %d])", query, beginTime.getTime(), endTime.getTime());
        }

        if (null == orderBy) {
            orderBy = "timestamp";
            orderAsc = false;
        }
        if (null == pageNo || null == pageSize) {
            pageNo = 1;
            pageSize = 1000;
        }

        PageVo<JSONObject> propertyList = EsManager.getInstance().getClient().searchByQuery(
                EventUtil.buildSearchEventPropertyIndex(beginTime, endTime, eventTypeId),
                query,
                pageNo, pageSize, orderBy, orderAsc, JSONObject.class);

        PageVo<Event> list = new PageVo<>(
                propertyList.getTotal(),
                propertyList.getPage().stream().map(p -> {
                    if(p.containsKey("event")){
                        return p.getJSONObject("event").toJavaObject(Event.class);
                    }else{
                        Event event = new Event();
                        event.setTimestamp(p.getLong("timestamp"));
                        return event;
                    }
                }).collect(Collectors.toList())
        );

        return list;
    }

    @Override
    public Event get(String id, Long timestamp) throws Exception {
        Event event = EsManager.getInstance().getClient().searchById(
                EventUtil.buildSearchEventIndex(new Date(timestamp), new Date(timestamp)), id, Event.class);

        fillOriginalLog(event);
        fillProperty(event);

        return event;
    }

    @Override
    public void bulkAdd(List<Event> events) throws Exception {
        if (events.isEmpty()) {
            return;
        }

        List<String> indexes = new ArrayList<>();
        List<Map<String, Object>> list = new ArrayList<>();

        for (Event event : events) {
            Long eventTimestamp = event.getTimestamp();

            // 事件的原始日志
            Object originalLog = event.getOriginalLog();
            List<OriginalLogRef> originalLogRefs = null;
            if (originalLog instanceof JSONArray) {
                originalLogRefs = new ArrayList<>();

                JSONArray logs = (JSONArray) originalLog;
                for (int i = 0; i < logs.size(); i++) {
                    String logIndex = buildAddEventOriginalLogIndex(eventTimestamp);
                    JSONObject log = logs.getJSONObject(i);

                    String logId = UuidUtil.random();
                    log.put("id", logId);

                    indexes.add(logIndex);
                    list.add(log);

                    OriginalLogRef ref = new OriginalLogRef(logId);
                    originalLogRefs.add(ref);
                }
            }

            // 事件的属性
            EventPropertyRef propertyRef = null;
            JSONObject property = event.getProperty();
            if (null != property) {
                String propertyIndex = buildAddEventPropertyIndex(event.getEventTypeId(), eventTimestamp);

                String propertyId = UuidUtil.random();
                property.put("id", propertyId);
                property.put("timestamp", eventTimestamp);
                property.put("event", buildEventRef(event));

                indexes.add(propertyIndex);
                list.add(property);

                propertyRef = new EventPropertyRef(propertyId);
            }

            // 事件
            String eventIndex = buildAddEventIndex(eventTimestamp);

            JSONObject eventJson = (JSONObject) JSON.toJSON(event);
            if (null != originalLogRefs) {
                eventJson.put("originalLog", originalLogRefs);
            }
            if (null != propertyRef) {
                eventJson.put("property", propertyRef);
            }

            indexes.add(eventIndex);
            list.add(eventJson);
        }

        EsManager.getInstance().getClient().bulkInsert(indexes, list, "id");
    }

    @Override
    public void deleteUntil(Date time) throws Exception {
        EsClient esClient = EsManager.getInstance().getClient();
        for (String prefix : List.of(eventIndexPrefix, eventOriginalLogIndexPrefix, eventPropertyIndexPrefix)) {
            // 删除索引
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM");
            Date beforeMonth = simpleDateFormat.parse(simpleDateFormat.format(time));

            Set<String> indices = esClient.listIndex(String.format("%s-", prefix));
            for (String index : indices) {
                try {
                    String[] tokens = index.split("-");
                    String strMonth = tokens[tokens.length - 1];
                    Date month = simpleDateFormat.parse(strMonth);
                    if (month.before(beforeMonth)) {
                        esClient.deleteIndex(index);
                    }
                } catch (Exception e) {
                    log.error("删除ES索引[{}]发生错误", index, e);
                }
            }

            // 删除数据
            Map<String, Object> mustTerm = new HashMap<>();
            Map<String, Object> mustMatch = new HashMap<>();
            Map<String, Object> mustRange = new HashMap<>();
            Map<String, Object> mustPrefix = new HashMap<>();

            List<Pair<String, Object>> shouldTerm = new ArrayList<>();
            List<Pair<String, Object>> shouldMatch = new ArrayList<>();
            List<Pair<String, Object>> shouldRange = new ArrayList<>();
            List<Pair<String, Object>> shouldPrefix = new ArrayList<>();

            Map<String, Object> timestampRange = new HashMap<>();
            timestampRange.put("lt", time.getTime());
            mustRange.put("timestamp", timestampRange);

            String index = String.format("%s-*", prefix);
            esClient.delete(
                    index,
                    mustTerm, mustMatch, mustRange, mustPrefix,
                    shouldTerm, shouldMatch, shouldRange, shouldPrefix);
        }
    }

    private EventRef buildEventRef(Event event){
        EventRef eventRef = new EventRef();
        eventRef.setId(event.getId());
        eventRef.setTimestamp(event.getTimestamp());
        eventRef.setEventTypeId(event.getEventTypeId());
        eventRef.setEventTypeName(event.getEventTypeName());
        eventRef.setMessage(event.getMessage());
        eventRef.setLevel(event.getLevel());
        eventRef.setTaskId(event.getTaskId());
        eventRef.setTaskName(event.getTaskName());
        eventRef.setRuleId(event.getRuleId());
        eventRef.setRuleName(event.getRuleName());

        return eventRef;
    }

    private void fillOriginalLog(Event event) throws Exception {
        Object originalLog = event.getOriginalLog();
        if (originalLog instanceof JSONArray) {
            List<JSONObject> list = new ArrayList<>();

            JSONArray refs = (JSONArray) originalLog;
            for (int i = 0; i < refs.size(); i++) {
                OriginalLogRef ref = refs.getJSONObject(i).toJavaObject(OriginalLogRef.class);
                if (StringUtils.isNotBlank(ref.getId())) {
                    JSONObject log = getLog(ref.getId(), event.getTimestamp());
                    list.add(log);
                } else {
                    list.add(refs.getJSONObject(i));
                }
            }

            originalLog = list;
        }

        event.setOriginalLog(originalLog);
    }

    private void fillProperty(Event event) throws Exception {
        JSONObject property = event.getProperty();
        if (null == property) {
            return;
        }

        EventPropertyRef ref = property.toJavaObject(EventPropertyRef.class);
        if (StringUtils.isNotBlank(ref.getId())) {
            property = getProperty(ref.getId(), event.getEventTypeId(), event.getTimestamp());
            if(null != property){
                property.remove("id");
                property.remove("timestamp");
                property.remove("event");
            }
        }

        // 富化属性字段
        try {
            fillPropertyFieldSchema(property, event.getEventTypeId());
        } catch (Exception e) {
            log.error("富化事件属性字段发生错误", e);
        }

        event.setProperty(property);
    }

    private void fillPropertyFieldSchema(JSONObject property, String eventTypeId) throws NotFoundException {
        if (null == property) {
            return;
        }

        EventTypeService eventTypeService = new EventTypeService();
        Map<String, SchemaField> fieldMap = new LinkedHashMap<>();
        if (eventTypeService.exists(eventTypeId)) {
            EventType eventType = eventTypeService.get(eventTypeId);
            List<SchemaField> fields = eventType.getFieldSchema();
            if (null != fields) {
                for (SchemaField field : fields) {
                    fieldMap.put(field.getKey(), field);
                }
            }
        }

        for (Map.Entry<String, Object> entry : property.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if(fieldMap.containsKey(key)){
                SchemaField schema = fieldMap.get(key);
                property.put(key, new SchemaFieldValue(schema.getKey(), schema.getValue(), value));
            }else{
                property.put(key, new SchemaFieldValue(key, key, value));
            }
        }
    }

    private JSONObject getLog(String id, Long timestamp) throws Exception {
        JSONObject log = EsManager.getInstance().getClient().get(
                buildAddEventOriginalLogIndex(timestamp), id, JSONObject.class);

        return log;
    }

    private JSONObject getProperty(String id, String eventTypeId, Long timestamp) throws Exception {
        JSONObject property = EsManager.getInstance().getClient().get(
                buildAddEventPropertyIndex(eventTypeId, timestamp), id, JSONObject.class);

        return property;
    }

    private String buildAddEventIndex(Long timestamp) throws Exception {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM");
        return String.format("%s-%s", eventIndexPrefix, simpleDateFormat.format(new Date(timestamp)));
    }

    private String buildAddEventOriginalLogIndex(Long timestamp) throws Exception {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM");
        return String.format("%s-%s", eventOriginalLogIndexPrefix, simpleDateFormat.format(new Date(timestamp)));
    }

    private String buildAddEventPropertyIndex(String eventTypeId, Long timestamp) throws Exception {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM");
        return String.format("%s-%s-%s", eventPropertyIndexPrefix, eventTypeId, simpleDateFormat.format(new Date(timestamp)));
    }


}
