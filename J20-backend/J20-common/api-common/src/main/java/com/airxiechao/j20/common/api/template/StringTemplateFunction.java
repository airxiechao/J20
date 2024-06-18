package com.airxiechao.j20.common.api.template;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 字符串模板的标签函数注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface StringTemplateFunction {
    /**
     * 标签函数名称
     * @return 标签函数名称
     */
    String value();
}
