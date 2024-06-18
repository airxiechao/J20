package com.airxiechao.j20.common.util;

import com.airxiechao.j20.common.api.pojo.log.Log;
import com.alibaba.fastjson2.JSONObject;

import java.util.*;

/**
 * 日志辅助类的额实现
 */
public class LogUtil {

    private static final String DATA_FIELD = "data";
    private static final String PROPERTY_FIELD = "property";

    /**
     * 解析原始日志。将 data、property 字段内的字段放入顶层。其他子对象的字段使用下划线连接键的方法放入顶层。
     * @param value 日志内容
     * @param timestamp 时间戳
     * @return 日志
     */
    public static Log parseLog(String value, long timestamp){
        JSONObject jsonObject = JSONObject.parseObject(value);
        JSONObject data = jsonObject.getJSONObject(DATA_FIELD);
        jsonObject.remove(DATA_FIELD);
        JSONObject property = jsonObject.getJSONObject(PROPERTY_FIELD);
        jsonObject.remove(PROPERTY_FIELD);

        if(null != data){
            jsonObject.putAll(data);
        }
        if(null != property){
            jsonObject.putAll(property);
        }
        return new Log(jsonObject, timestamp);
    }

    /**
     * 获取日志的 key。用于日志分组
     * @param value 日志
     * @param fields 分组字段
     * @return 日志的 key
     */
    public static String getKey(Log value, String[] fields) {
        if(null == fields || fields.length == 0){
            return "__all__";
        }

        List<String> keys = new ArrayList<>();
        for (String field : fields) {
            if(FieldUtil.logFieldIsNotNull(field, value)) {
                keys.add(FieldUtil.renderLogToField(field, value).toString());
            }else{
                keys.add(null);
            }
        }
        return String.join("_", keys);
    }

}
