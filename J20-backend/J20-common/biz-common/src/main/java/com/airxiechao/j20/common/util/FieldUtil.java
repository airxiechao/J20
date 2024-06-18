package com.airxiechao.j20.common.util;

import com.airxiechao.j20.common.api.pojo.log.Log;
import com.airxiechao.j20.common.api.pojo.log.SeriesLog;
import com.airxiechao.j20.common.express.ExpressTemplate;
import com.airxiechao.j20.common.template.StringTemplate;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONPath;
import lombok.experimental.UtilityClass;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字段取值辅助类的实现
 */
@UtilityClass
public class FieldUtil {

    /**
     * 判断是否是字符串模板的正则表达式
     */
    private static Pattern fieldRefPattern = Pattern.compile(".*\\{\\{(.*)}}.*");

    /**
     * 判断是否是表达式的正则表达式
     */
    private static Pattern fieldExpressPattern = Pattern.compile("\\$\\{(.+)}", Pattern.DOTALL);

    /**
     * 通过日志渲染字段的值
     *
     * @param field 字段
     * @param log   日志
     * @return 渲染结果
     */
    public static Object renderLogToField(String field, Log log) {
        return renderToField(field, log.getData(), log.getData());
    }

    public static double renderLogToDoubleField(String field, Log log){
        try{
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("value", renderLogToField(field, log));
            return jsonObject.getDoubleValue("value");
        }catch (Exception e){
            return 0;
        }
    }

    public static Long renderLogToLongField(String field, Log log){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("value", FieldUtil.renderLogToField(field, log));

        return jsonObject.getLong("value");
    }

    /**
     * 通过日志渲染字段的值是否不为空
     *
     * @param field 字段
     * @param log   日志
     * @return 是否不为空
     */
    public static boolean logFieldIsNotNull(String field, Log log){
        return null != renderLogToField(field, log);
    }

    /**
     * 通过序列日志渲染字段的值
     *
     * @param field     字段
     * @param seriesLog 序列日志
     * @return 渲染结果
     */
    public static Object renderSeriesLogToField(String field, SeriesLog seriesLog) {
        return renderToField(field, seriesLog.flat(), seriesLog.flat());
    }

    /**
     * 渲染字段的值
     *
     * @param field          字段
     * @param selfJSONObject 自身JSON对象（日志对象）
     * @param refJSONObject  引用JSON对象
     * @return 渲染结果
     */
    public static Object renderToField(String field, JSONObject selfJSONObject, JSONObject refJSONObject) {
        Object objValue;
        Matcher expressMatcher = fieldExpressPattern.matcher(field.trim());
        if (expressMatcher.matches()) {
            // 表达式
            String express = expressMatcher.group(1);
            try {
                objValue = new ExpressTemplate(express, refJSONObject, false).execute();
            } catch (Exception e) {
                objValue = null;
            }
        } else if (fieldRefPattern.matcher(field).matches()) {
            // 引用
            objValue = new StringTemplate(field).render(refJSONObject);
        } else {
            // 字段映射
            objValue = JSONPath.eval(selfJSONObject, "$."+field);
        }
        return objValue;
    }

    /**
     * 使用日志渲染内容
     *
     * @param value 内容
     * @param log   日志
     * @return 渲染结果
     */
    public static String renderLogToValue(String value, Log log) {
        return renderToValue(value, log.getData());
    }

    /**
     * 使用序列日志渲染内容
     *
     * @param value     内容
     * @param seriesLog 日志
     * @return 渲染结果
     */
    public static String renderSeriesLogToValue(String value, SeriesLog seriesLog) {
        return renderToValue(value, seriesLog.flat());
    }

    /**
     * 渲染内容的值
     *
     * @param value      内容
     * @param jsonObject JSON对象
     * @return 渲染结果
     */
    public static String renderToValue(String value, JSONObject jsonObject) {
        String objValue = value;

        Matcher expressMatcher = fieldExpressPattern.matcher(value.trim());
        if (expressMatcher.matches()) {
            // 表达式
            String express = expressMatcher.group(1);
            try {
                objValue = new ExpressTemplate(express, jsonObject, false).execute().toString();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if (fieldRefPattern.matcher(value).matches()) {
            // 引用
            objValue = new StringTemplate(value).render(jsonObject);
        }

        return objValue;
    }

    /**
     * 设置对象的字段值
     * @param obj 对象
     * @param field 字段
     * @param value 值
     */
    public static void setFieldValue(Object obj, String field, Object value){
        JSONPath.set(obj, field, value);
    }

}
