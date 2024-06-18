package com.airxiechao.j20.detection.rest;

import com.airxiechao.j20.common.api.pojo.constant.ConstRespCode;
import com.airxiechao.j20.common.api.pojo.rest.PageData;
import com.airxiechao.j20.common.api.pojo.rest.Resp;
import com.airxiechao.j20.detection.api.pojo.event.EventType;
import com.airxiechao.j20.detection.api.pojo.event.EventTypeNumTreeNodeVo;
import com.airxiechao.j20.detection.api.pojo.event.SchemaField;
import com.airxiechao.j20.detection.api.rest.IEventTypeController;
import com.airxiechao.j20.detection.api.rest.param.*;
import com.airxiechao.j20.detection.api.service.IEventTypeService;
import com.airxiechao.j20.detection.service.EventTypeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.annotation.RequestScope;

import java.util.List;

@Slf4j
@RestController
@RequestScope
public class EventTypeController implements IEventTypeController {

    private IEventTypeService eventTypeService = new EventTypeService();

    @Override
    public Resp<EventType> add(EventTypeAddParam param) {
        log.info("创建事件类型：{}", param);

        try {
            if(eventTypeService.existsByName(param.getName())){
                throw new Exception("事件类型名称已存在");
            }

            normalizeFieldSchema(param.getFieldSchema());

            EventType eventType = new EventType(param.getName(), param.getLevel(), param.getParentId(), param.getFieldSchema());
            eventType = eventTypeService.add(eventType);
            return new Resp<>(ConstRespCode.OK, null, eventType);
        }catch (Exception e){
            log.error("创建事件类型发生错误", e);
            return new Resp<>(ConstRespCode.ERROR, e.getMessage(), null);
        }
    }

    @Override
    public Resp<EventType> update(EventTypeUpdateParam param) {
        log.info("修改事件类型：{}", param);

        try {
            EventType old = eventTypeService.get(param.getId());
            if(!param.getName().equals(old.getName())){
                if(eventTypeService.existsByName(param.getName())){
                    throw new Exception("事件类型名称已存在");
                }
            }

            normalizeFieldSchema(param.getFieldSchema());

            EventType eventType = new EventType(param.getId(), param.getName(),param.getLevel(), old.getParentId(), param.getFieldSchema());
            eventTypeService.update(eventType);
            return new Resp<>(ConstRespCode.OK, null, null);
        }catch (Exception e){
            log.error("修改事件类型发生错误", e);
            return new Resp<>(ConstRespCode.ERROR, e.getMessage(), null);
        }
    }

    @Override
    public Resp<EventType> get(EventTypeGetParam param) {
        log.info("查询事件类型：{}", param);

        try {
            EventType eventType = eventTypeService.get(param.getId());
            return new Resp<>(ConstRespCode.OK, null, eventType);
        }catch (Exception e){
            log.error("查询事件类型发生错误", e);
            return new Resp<>(ConstRespCode.ERROR, e.getMessage(), null);
        }
    }

    @Override
    public Resp<PageData<EventTypeNumTreeNodeVo>> list(EventTypeListParam param) {
        log.info("查询事件类型树：{}", param);

        try {
            String name = null != param ? param.getName() : null;
            List<EventTypeNumTreeNodeVo> treeNodes = eventTypeService.listAsTree(name);

            PageData<EventTypeNumTreeNodeVo> data = new PageData<>(null, null, null, treeNodes);
            return new Resp<>(ConstRespCode.OK, null, data);
        }catch (Exception e){
            log.error("查询事件类型树发生错误", e);
            return new Resp<>(ConstRespCode.ERROR, e.getMessage(), null);
        }
    }

    @Override
    public Resp delete(EventTypeDeleteParam param) {
        log.info("删除事件类型：{}", param);

        try {
            for (String id : param.getId().split(",")) {
                if(eventTypeService.exists(id)){
                    eventTypeService.delete(id);
                }
            }

            return new Resp<>(ConstRespCode.OK, null, null);
        }catch (Exception e){
            log.error("删除事件类型发生错误", e);
            return new Resp<>(ConstRespCode.ERROR, e.getMessage(), null);
        }
    }

    private void normalizeFieldSchema(List<SchemaField> fieldSchema){
        if(null != fieldSchema){
            fieldSchema.removeIf(field -> StringUtils.isBlank(field.getKey()) || StringUtils.isBlank(field.getValue()));
        }
    }
}
