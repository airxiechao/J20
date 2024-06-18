package com.airxiechao.j20.common.api.pojo.log;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 序列日志
 */
@Data
public class SeriesLog {
    /**
     * 包含的日志列表
     */
    private List<Log> logs = new ArrayList<>();

    /**
     * 默认构造函数
     */
    public SeriesLog() {
    }

    /**
     * 复制构造函数
     * @param s 另一个序列日志
     */
    public SeriesLog(SeriesLog s) {
        logs.addAll(s.logs);
    }

    /**
     * 平铺日志。遍历所有日志，将日志字段放入一个JSON对象，再添加 logs 字段表示所有日志
     * @return
     */
    public JSONObject flat() {
        JSONObject target = new JSONObject();
        List<JSONObject> list = new ArrayList<>();

        for (Log log : logs) {
            target.putAll(JSON.parseObject(JSON.toJSONString(log.getData())));
            list.add(JSON.parseObject(JSON.toJSONString(log.getData())));
        }

        target.put("logs", list);

        return target;
    }

    /**
     * 添加日志
     * @param log
     */
    public void append(Log log){
        logs.add(log);
    }

    /**
     * 返回索引位置的日志
     * @param i 索引位置
     * @return 日志，如果存在。null，如果不存在
     */
    public Log log(int i){
        return logs.get(i);
    }

    /**
     * 返回最后一个日志
     * @return 日志，如果存在。null，如果不存在
     */
    public Log last(){
        if(!logs.isEmpty()){
            return logs.get(logs.size() - 1);
        }else{
            return null;
        }
    }

    /**
     * 返回特征描述
     * @return 特征描述
     */
    public String features(){
        return logs.stream().map(Log::getFeature).collect(Collectors.joining(";"));
    }
}
