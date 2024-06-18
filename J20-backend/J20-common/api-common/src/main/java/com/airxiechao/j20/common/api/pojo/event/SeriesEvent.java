package com.airxiechao.j20.common.api.pojo.event;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 序列事件对象
 */
@Data
public class SeriesEvent {
    /**
     * 包含的事件列表
     */
    private List<Event> events = new ArrayList<>();

    /**
     * 代表性事件的索引位置
     */
    private int targetIdx = 0;

    /**
     * 默认构造函数
     */
    public SeriesEvent() {
    }

    /**
     * 复制构造函数
     * @param s 另一个序列事件
     */
    public SeriesEvent(SeriesEvent s) {
        events.addAll(s.events);
    }

    /**
     * 平铺序列事件对象。遍历所有事件，将字段依次放入一个JSON对象，再添加 events 字段表示全部事件
     * @return 平铺后的JSON对象
     */
    public JSONObject flat() {
        List<JSONObject> list = new ArrayList<>();
        JSONObject target = null;

        for (int i = 0; i < events.size(); i++) {
            Event e = events.get(i);
            JSONObject flat = e.flat();
            list.add(flat);
            if(i == targetIdx){
                target = flat;
            }
        }

        if(null == target){
            target = new JSONObject();
        }

        target.put("events", events.stream().map(e -> e.toString()).collect(Collectors.joining(",")));

        return target;
    }

    /**
     * 添加一个事件
     * @param event 事件
     */
    public void append(Event event){
        events.add(event);
    }

    /**
     * 设置代表性事件的索引位置
     * @param targetIdx 索引位置
     */
    public void setTargetIdx(int targetIdx) {
        this.targetIdx = targetIdx;
    }

    /**
     * 获取索引位置的事件
     * @param i 索引位置
     * @return 事件，如果存在。null，如果不存在
     */
    public Event get(int i){
        return events.get(i);
    }

    /**
     * 获取第一个事件
     * @return 事件，如果存在。null，如果不存在
     */
    public Event getFirst(){
        if(!events.isEmpty()){
            return events.get(0);
        }else{
            return null;
        }
    }

    /**
     * 获取最后一个事件
     * @return 事件，如果存在。null，如果不存在
     */
    public Event getLast(){
        if(!events.isEmpty()){
            return events.get(events.size() - 1);
        }else{
            return null;
        }
    }

    /**
     * 获取代表性事件
     * @return 事件，如果存在。null，如果不存在
     */
    public Event getTarget(){
        if(events.size() > targetIdx){
            return events.get(targetIdx);
        }else{
            return null;
        }
    }

}
