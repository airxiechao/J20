package com.airxiechao.j20.common.api.pojo.log;

import com.alibaba.fastjson2.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 原始日志
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Log {

    /**
     * 数据内容
     */
    private JSONObject data;

    /**
     * 时间戳
     */
    private Long timestamp;

    /**
     * 特征描述
     */
    private String feature;

    /**
     * 所属窗口的计数输出
     */
    private Double windowOut;

    /**
     * 所属窗口的开始时间戳
     */
    private Long windowStart;

    /**
     * 所属窗口的结束时间戳
     */
    private Long windowEnd;

    /**
     * 构造函数
     * @param data 内容
     * @param timestamp 时间戳
     */
    public Log(JSONObject data, long timestamp) {
        this.data = data;
        this.timestamp = timestamp;
    }

    /**
     * 转字符串描述
     * @return 描述
     */
    @Override
    public String toString() {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return String.format("(timestamp=%s, log=%s, feature=%s)",
                sf.format(new Date(timestamp)), data.toJSONString(), feature);
    }

    /**
     * 添加特征描述
     * @param value 特征描述
     */
    public synchronized void addFeature(String value){
        if(StringUtils.isBlank(feature)) {
            feature = value;
        }else{
            feature = String.format("%s,%s", feature, value);
        }
    }
}
