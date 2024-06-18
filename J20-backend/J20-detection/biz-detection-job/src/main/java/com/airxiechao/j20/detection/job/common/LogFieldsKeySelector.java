package com.airxiechao.j20.detection.job.common;

import com.airxiechao.j20.common.api.pojo.log.Log;
import com.airxiechao.j20.common.util.LogUtil;
import org.apache.flink.api.java.functions.KeySelector;

/**
 * 日志分组的键值生成器
 */
public class LogFieldsKeySelector implements KeySelector<Log, Object> {

    private String[] fields;

    public LogFieldsKeySelector(String[] fields){
        this.fields = fields;
    }

    @Override
    public Object getKey(Log value) throws Exception {
        return LogUtil.getKey(value, fields);
    }
}
