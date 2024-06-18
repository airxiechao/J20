package com.airxiechao.j20.detection.api.rest;

import com.airxiechao.j20.common.api.pojo.rest.PageData;
import com.airxiechao.j20.common.api.pojo.rest.Resp;
import com.airxiechao.j20.detection.api.pojo.event.EventType;
import com.airxiechao.j20.detection.api.pojo.event.EventTypeNumTreeNodeVo;
import com.airxiechao.j20.detection.api.rest.param.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * 事件类型请求接口
 */
public interface IEventTypeController {

    /**
     * 添加事件类型
     * @param param 参数
     * @return 新增事件类型
     */
    @PostMapping("/api/detection/event/type/add")
    Resp<EventType> add(@RequestBody @Valid EventTypeAddParam param);

    /**
     * 修改事件类型
     * @param param 参数
     * @return 响应
     */
    @PostMapping("/api/detection/event/type/update")
    Resp update(@RequestBody @Valid EventTypeUpdateParam param);

    /**
     * 查询事件类型
     * @param param 参数
     * @return 事件类型
     */
    @PostMapping("/api/detection/event/type/get")
    Resp<EventType> get(@RequestBody @Valid EventTypeGetParam param);

    /**
     * 查询事件类型列表
     * @param param 参数
     * @return 事件类型列表
     */
    @PostMapping("/api/detection/event/type/list")
    Resp<PageData<EventTypeNumTreeNodeVo>> list(@RequestBody(required = false) @Valid EventTypeListParam param);

    /**
     * 删除事件类型
     * @param param 参数
     * @return 响应
     */
    @PostMapping("/api/detection/event/type/delete")
    Resp delete(@RequestBody @Valid EventTypeDeleteParam param);

}
