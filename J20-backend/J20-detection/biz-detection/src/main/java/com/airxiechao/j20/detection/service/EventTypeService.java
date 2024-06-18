package com.airxiechao.j20.detection.service;

import com.airxiechao.j20.common.api.pojo.exception.NotFoundException;
import com.airxiechao.j20.common.api.pojo.statistics.Group;
import com.airxiechao.j20.common.api.pojo.vo.TimeRangeVo;
import com.airxiechao.j20.common.util.ApplicationContextUtil;
import com.airxiechao.j20.common.util.TimeUtil;
import com.airxiechao.j20.detection.api.pojo.event.EventType;
import com.airxiechao.j20.detection.api.pojo.event.EventTypeNumTreeNodeVo;
import com.airxiechao.j20.detection.api.service.IEventStatisticsService;
import com.airxiechao.j20.detection.api.service.IEventTypeService;
import com.airxiechao.j20.detection.cache.ServiceCacheFactory;
import com.airxiechao.j20.detection.db.record.EventTypeRecord;
import com.airxiechao.j20.detection.db.reposiroty.IEventTypeRepository;
import com.airxiechao.j20.detection.util.EventUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class EventTypeService implements IEventTypeService {
    private static final String eventTypePrefixes = "abcdefghijklmnopqrstuvwxyz";
    private static final int eventTypeNumberMaxLength = 3;

    private IEventTypeRepository eventTypeRepository = ApplicationContextUtil.getContext().getBean(IEventTypeRepository.class);

    @Override
    public EventType get(String id) throws NotFoundException {
        Optional<EventTypeRecord> opt = eventTypeRepository.findById(id);
        if(opt.isEmpty()){
            throw new NotFoundException();
        }

        return buildEventType(opt.get());
    }

    @Override
    public boolean exists(String id) {
        return eventTypeRepository.existsById(id);
    }

    @Override
    public boolean existsByName(String name) {
        return eventTypeRepository.existsByNameIgnoreCase(name);
    }

    public List<EventTypeNumTreeNodeVo> listAsTree(String name) {
        // 所有节点
        Map<String, EventTypeNumTreeNodeVo> allMap = new LinkedHashMap<>();
        eventTypeRepository.findAll().forEach(t -> {
            EventTypeNumTreeNodeVo eventTypeNumVo = buildEventTypeNumTreeNodeVo(buildEventType(t), null, new ArrayList<>());
            allMap.put(t.getId(), eventTypeNumVo);
        });

        // 构造树形结构
        allMap.forEach((key, value) -> {
            String parentId = value.getParentId();
            if(StringUtils.isNotBlank(parentId)){
                EventTypeNumTreeNodeVo node = allMap.get(parentId);
                if(null != node){
                    node.getChildren().add(value);
                }
            }
        });

        // 筛选节点
        Iterator<Map.Entry<String, EventTypeNumTreeNodeVo>> it = allMap.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry<String,EventTypeNumTreeNodeVo> entry = it.next();
            EventTypeNumTreeNodeVo value = entry.getValue();

            if(StringUtils.isNotBlank(name) && !containName(value, name)){
                it.remove();
            }
        }

        // 清除树形结构
        allMap.forEach((key, value) -> {
            value.getChildren().clear();
        });

        // 重新构造树形结构
        allMap.forEach((key, value) -> {
            String parentId = value.getParentId();
            if(StringUtils.isNotBlank(parentId)){
                EventTypeNumTreeNodeVo node = allMap.get(parentId);
                if(null != node){
                    node.getChildren().add(value);
                }
            }
        });

        // 只保留顶层节点
        List<EventTypeNumTreeNodeVo> topList = allMap.values().stream()
                .filter(value -> StringUtils.isBlank(value.getParentId()))
                .collect(Collectors.toList());

        // 统计事件数量
        TimeRangeVo range = TimeUtil.rangeOfMonth(null, null);
        List<Group> counts = ServiceCacheFactory.getInstance().get((IEventStatisticsService)new EventStatisticsService())
                .groupBy(range.getBegin(), range.getEnd(), null, EventUtil.buildSearchEventIndex(range.getBegin(), range.getEnd()), "eventTypeId.keyword");

        Map<String, Double> countMap = counts.stream().collect(Collectors.toMap(Group::getKey, Group::getValue));
        for (EventTypeNumTreeNodeVo node : topList) {
            countEvent(node, countMap);
        }

        return topList;

    }

    @Override
    public List<EventType> listAsList(String name) {
        List<EventTypeRecord> list = eventTypeRepository.findByNameContainingIgnoreCase(name);

        return list.stream()
                .map(t -> buildEventType(t))
                .collect(Collectors.toList());
    }

    private boolean containName(EventTypeNumTreeNodeVo node, String name){
        if(node.getName().contains(name)){
            return true;
        }else{
            for (EventTypeNumTreeNodeVo child : node.getChildren()) {
                if(containName(child, name)){
                    return true;
                }
            }
            return false;
        }
    }

    private void countEvent(EventTypeNumTreeNodeVo treeNodeVo, Map<String, Double> counts) {
        if (null != treeNodeVo.getNumEvent()){
            return;
        }

        Double numEvent = counts.get(treeNodeVo.getId());
        if(null == numEvent){
            numEvent = 0.0;
        }

        List<EventTypeNumTreeNodeVo> children = treeNodeVo.getChildren();
        if(null != children){
            for (EventTypeNumTreeNodeVo child : children) {
                countEvent(child, counts);
                numEvent += child.getNumEvent();
            }
        }

        treeNodeVo.setNumEvent(numEvent);
    }

    @Override
    public EventType add(EventType eventType) {
        EventTypeRecord record = buildEventTypeRecord(eventType);
        return buildEventType(eventTypeRepository.save(record));
    }

    @Override
    public void update(EventType eventType) {
        EventTypeRecord record = buildEventTypeRecord(eventType);
        eventTypeRepository.save(record);
    }

    @Override
    public void delete(String id) {
        eventTypeRepository.deleteByIdStartingWith(id);
    }

    private EventType buildEventType(EventTypeRecord record){
        return new EventType(
                record.getId(),
                record.getName(),
                record.getLevel(),
                record.getParentId(),
                JSON.parseObject(record.getFieldSchema(), new TypeReference<>(){})
        );
    }

    private EventTypeNumTreeNodeVo buildEventTypeNumTreeNodeVo(EventType t, Double numEvent, List<EventTypeNumTreeNodeVo> children) {
        return new EventTypeNumTreeNodeVo(t.getId(), t.getName(), t.getLevel(), t.getParentId(), t.getFieldSchema(), numEvent, children);
    }

    private EventTypeRecord buildEventTypeRecord(EventType eventType) {
        EventTypeRecord record = new EventTypeRecord();
        record.setId(null != eventType.getId() ? eventType.getId() : buildEventTypeId(eventType.getParentId()));
        record.setName(eventType.getName());
        record.setLevel(eventType.getLevel());
        record.setParentId(eventType.getParentId());
        record.setFieldSchema(JSON.toJSONString(eventType.getFieldSchema()));

        return record;
    }

    private String buildEventTypeId(String parentId)  {
        String prefix = getEventTypeNextPrefix(parentId);
        String number = getEventTypeNextNumber(parentId);
        return String.format("%s%s%s", null != parentId ? parentId : "", prefix, number);
    }

    private String getEventTypeNextPrefix(String parentId){
        String parentPrefix = null;
        if(null != parentId){
            parentPrefix = parentId.charAt(parentId.length() - eventTypeNumberMaxLength - 1) + "";
        }

        if(null == parentPrefix){
            return eventTypePrefixes.charAt(0) + "";
        }

        int level = eventTypePrefixes.indexOf(parentPrefix) + 1;
        return eventTypePrefixes.charAt(level) + "";
    }

    private String getEventTypeNextNumber(String parentId) {
        String number = String.format("%0"+ eventTypeNumberMaxLength +"d", getEventTypeMaxNumber(parentId) + 1);

        if(number.length() > eventTypeNumberMaxLength){
            throw new RuntimeException("该事件类型的子类型编码长度超过最大值");
        }

        return number;
    }

    private int getEventTypeMaxNumber(String parentId) {
        List<EventTypeRecord> list = eventTypeRepository.findByParentId(parentId);

        int maxNumber = 0;
        for (EventTypeRecord type : list) {
            String typeId = type.getId();
            if(StringUtils.isNotBlank(parentId)){
                typeId = typeId.substring(parentId.length());
            }
            int number = Integer.valueOf(typeId.substring(1));
            if(number > maxNumber){
                maxNumber = number;
            }
        }

        return maxNumber;
    }
}
