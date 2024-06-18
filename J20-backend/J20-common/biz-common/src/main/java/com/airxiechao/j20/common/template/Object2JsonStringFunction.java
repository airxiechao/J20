package com.airxiechao.j20.common.template;

import com.airxiechao.j20.common.api.template.StringTemplateFunction;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import com.github.mustachejava.TemplateFunction;

import java.util.Map;

/**
 * 对象转字符串的标签函数。
 *
 * 使用方法：
 * {{#object2jsonstring}}字段名 [是否格式化]{{/object2jsonstring}}
 *
 * 比如：
 * {{#object2jsonstring}}value{{/object2jsonstring}} 或
 * {{#object2jsonstring}}value true{{/object2jsonstring}}
 */
@StringTemplateFunction("object2jsonstring")
public class Object2JsonStringFunction implements TemplateFunction {
    private Map<String, Object> scopes;

    public Object2JsonStringFunction(Map<String, Object> scopes) {
        this.scopes = scopes;
    }

    /**
     * 执行标签函数
     * @param s 标签内的内容
     * @return 执行结果
     */
    @Override
    public String apply(String s) {
        s = s.trim();

        String field = s;
        boolean pretty = false;

        int sep = s.indexOf(" ");
        if(sep > 0){
            field = s.substring(0, sep);
            pretty = Boolean.valueOf(s.substring(sep + 1).trim());
        }

        Object value = scopes.get(field);
        if(null == value){
            return "";
        }

        if(pretty){
            return JSON.toJSONString(value, JSONWriter.Feature.PrettyFormat);
        }else{
            return JSON.toJSONString(value);
        }
    }
}
