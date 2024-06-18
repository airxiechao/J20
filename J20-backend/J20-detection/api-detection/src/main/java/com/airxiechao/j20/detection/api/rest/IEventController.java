package com.airxiechao.j20.detection.api.rest;

import com.airxiechao.j20.common.api.pojo.event.Event;
import com.airxiechao.j20.common.api.pojo.rest.PageData;
import com.airxiechao.j20.common.api.pojo.rest.Resp;
import com.airxiechao.j20.detection.api.rest.param.EventGetParam;
import com.airxiechao.j20.detection.api.rest.param.EventListParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * 事件请求接口
 */
public interface IEventController {
    /**
     * 查询事件列表
     * @param param 参数
     * @return 事件列表
     */
    @PostMapping("/api/detection/event/list")
    Resp<PageData<Event>> list(@RequestBody @Valid EventListParam param);

    /**
     * 查询事件
     * @param param 参数
     * @return 事件
     */
    @PostMapping("/api/detection/event/get")
    Resp<Event> get(@RequestBody @Valid EventGetParam param);

}
