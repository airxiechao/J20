package com.airxiechao.j20.common.api.pojo.event;

import com.airxiechao.j20.common.api.pojo.constant.ConstEventFieldType;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 事件对象
 */
@Data
public class Event {
    /**
     * 事件ID
     */
    @EventField(fieldName = "事件ID", fieldType = ConstEventFieldType.STRING) @ExportField("事件ID")
    private String id;

    /**
     * 事件时间戳
     */
    @EventField(fieldName = "事件时间戳", fieldType = ConstEventFieldType.TIMESTAMP)
    @ExportField(value = "事件时间戳", template = "{{#timestamp2string}}value{{/timestamp2string}}")
    private long timestamp;

    /**
     * 事件类型ID
     */
    @EventField(fieldName = "事件类型ID", fieldType = ConstEventFieldType.STRING) @ExportField("事件类型ID")
    private String eventTypeId;

    /**
     * 事件类型名称
     */
    @EventField(fieldName = "事件类型名称", fieldType = ConstEventFieldType.STRING) @ExportField("事件类型名称")
    private String eventTypeName;

    /**
     * 事件内容
     */
    @EventField(fieldName = "事件内容", fieldType = ConstEventFieldType.STRING) @ExportField("事件内容")
    private String message;

    /**
     * 事件级别
     */
    @EventField(fieldName = "事件级别", fieldType = ConstEventFieldType.STRING)
    @ExportField(value = "事件级别", template = "{{#constvalue2name}}value com.airxiechao.j20.common.api.pojo.constant.ConstEventLevel{{/constvalue2name}}")
    private String level;

    /**
     * 任务ID
     */
    @EventField(fieldName = "任务ID", fieldType = ConstEventFieldType.STRING) @ExportField("任务ID")
    private String taskId;

    /**
     * 任务名称
     */
    @EventField(fieldName = "任务名称", fieldType = ConstEventFieldType.STRING) @ExportField("任务名称")
    private String taskName;

    /**
     * 规则ID
     */
    @EventField(fieldName = "规则ID", fieldType = ConstEventFieldType.STRING) @ExportField("规则ID")
    private String ruleId;

    /**
     * 规则名称
     */
    @EventField(fieldName = "规则名称", fieldType = ConstEventFieldType.STRING) @ExportField("规则名称")
    private String ruleName;

    /**
     * 事件来源日志
     */
    @ExportField(value = "事件来源日志", template = "{{#object2jsonstring}}value{{/object2jsonstring}}")
    private Object originalLog;

    /**
     * 事件特征
     */
    @EventField(fieldName = "事件特征", fieldType = ConstEventFieldType.STRING) @ExportField("事件特征")
    private String feature;


    /**
     * 属性字段
     */
    @ExportField(value = "事件属性", template = "{{#object2jsonstring}}value{{/object2jsonstring}}")
    private JSONObject property;

    /**
     * 所在窗口输出
     */
    private Double windowOut;

    /**
     * 所在窗口开始时间戳
     */
    private Long windowStart;

    /**
     * 所在窗口结束时间戳
     */
    private Long windowEnd;

    /**
     * 转字符串描述
     * @return 描述
     */
    @Override
    public String toString() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return String.format("(事件ID:%s, 时间:%s, 类型:%s#%s, 级别:%s, 任务:%s#%s, 规则:%s#%s, 内容:%s, 特征值:%s, 属性:%s)",
                id, simpleDateFormat.format(new Date(timestamp)), eventTypeId, eventTypeName, level, taskId, taskName,
                ruleId, ruleName, message, feature, JSON.toJSONString(property)
        );
    }

    /**
     * 平铺事件。将数据字段 property 内的字段移到第一层
     * @return 平铺后的JSON对象
     */
    public JSONObject flat(){
        JSONObject jsonObject = (JSONObject) JSON.toJSON(this);

        JSONObject extendProperty = jsonObject.getJSONObject("property");
        jsonObject.remove("property");

        if(null != extendProperty){
            jsonObject.putAll(extendProperty);
        }

        return jsonObject;
    }
}
