package com.airxiechao.j20.common.template;

import com.airxiechao.j20.common.api.pojo.constant.ConstName;
import com.airxiechao.j20.common.api.template.StringTemplateFunction;
import com.github.mustachejava.TemplateFunction;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

/**
 * 常量值转名称的标签函数。
 * 使用方法：
 * {{#constvalue2name}}字段名 常量类名{{/constvalue2name}}
 * 比如：
 * {{#constvalue2name}}value com.airxiechao.j20.common.api.pojo.constant.ConstEventLevel{{/constvalue2name}}
 */
@Slf4j
@StringTemplateFunction("constvalue2name")
public class ConstValue2NameFunction implements TemplateFunction {
    private Map<String, Object> scopes;

    public ConstValue2NameFunction(Map<String, Object> scopes) {
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

        String field = null;
        String className = null;

        int sep = s.indexOf(" ");
        if(sep > 0){
            field = s.substring(0, sep);
            className = s.substring(sep + 1).trim();
        }

        if(StringUtils.isBlank(field) || StringUtils.isBlank(className) ){
            return "";
        }

        Object fieldValue = scopes.get(field);
        String constName = String.valueOf(fieldValue);

        try {
            Class<?> cls = Class.forName(className);
            for (Field f : cls.getDeclaredFields()) {
                f.setAccessible(true);
                ConstName anno = f.getAnnotation(ConstName.class);
                if(Modifier.isStatic(f.getModifiers()) && null != anno){
                    Object constValue = f.get(null);
                    if(constValue.equals(fieldValue) || String.valueOf(fieldValue).equals(String.valueOf(constValue))){
                        constName = anno.value();
                        break;
                    }
                }
            }
        } catch (Exception e) {
            log.error("常量值转名称的标签函数执行发生错误", e);
        }

        return constName;
    }
}
