package com.airxiechao.j20.detection.rest;

import com.airxiechao.j20.common.api.pojo.constant.ConstRespCode;
import com.airxiechao.j20.common.api.pojo.event.Event;
import com.airxiechao.j20.common.api.pojo.rest.PageData;
import com.airxiechao.j20.common.api.pojo.rest.Resp;
import com.airxiechao.j20.common.api.pojo.vo.PageVo;
import com.airxiechao.j20.common.api.pojo.vo.TimeRangeVo;
import com.airxiechao.j20.common.util.TimeUtil;
import com.airxiechao.j20.detection.api.rest.IEventController;
import com.airxiechao.j20.detection.api.rest.param.EventDeleteParam;
import com.airxiechao.j20.detection.api.rest.param.EventGetParam;
import com.airxiechao.j20.detection.api.rest.param.EventListParam;
import com.airxiechao.j20.detection.api.service.IEventService;
import com.airxiechao.j20.detection.service.EventService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.annotation.RequestScope;

@Slf4j
@RestController
@RequestScope
public class EventController implements IEventController {

    private IEventService eventService = new EventService();

    @Override
    public Resp<PageData<Event>> list(EventListParam param) {
        log.info("查询事件列表：{}", param);

        try {
            TimeRangeVo range = TimeUtil.rangeOfMonth(param.getBeginTime(), param.getEndTime());

            PageVo<Event> page;
            if(StringUtils.isBlank(param.getEventTypeId()) || StringUtils.isBlank(param.getQuery())){
                page = eventService.list(
                        range.getBegin(), range.getEnd(),
                        param.getEventTypeId(), null, null, null, param.getLevel(),
                        param.getCurrent(), param.getSize(), param.getOrderBy(), param.getOrderAsc());
            }else{
                String query = param.getQuery();
                if(StringUtils.isNotBlank(param.getLevel())){
                    query = String.format("(%s) AND (event.level:%s)", query, param.getLevel());
                }
                page = eventService.listByProperty(
                        range.getBegin(), range.getEnd(),
                        param.getEventTypeId(),
                        query,
                        param.getCurrent(), param.getSize(), param.getOrderBy(), param.getOrderAsc()
                );
            }

            PageData<Event> data = new PageData<>(param.getCurrent(), param.getSize(), page.getTotal(), page.getPage());
            return new Resp<>(ConstRespCode.OK, null, data);
        }catch (Exception e){
            log.error("查询事件列表发生错误", e);
            return new Resp<>(ConstRespCode.ERROR, e.getMessage(), null);
        }
    }

    @Override
    public Resp<Event> get(EventGetParam param) {
        log.info("查询事件：{}", param);

        try {
            Event event = eventService.get(param.getId(), param.getTimestamp());
            return new Resp<>(ConstRespCode.OK, null, event);
        }catch (Exception e){
            log.error("查询事件发生错误", e);
            return new Resp<>(ConstRespCode.ERROR, e.getMessage(), null);
        }
    }

    @Override
    public Resp delete(EventDeleteParam param) {
        log.info("删除事件：{}", param);

        try {
            eventService.delete(param.getId(), param.getTimestamp());
            return new Resp<>(ConstRespCode.OK, null, null);
        }catch (Exception e){
            log.error("删除事件发生错误", e);
            return new Resp<>(ConstRespCode.ERROR, e.getMessage(), null);
        }
    }
}
