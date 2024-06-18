package com.airxiechao.j20.common.api.pojo.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 事件字段的注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface EventField {
    /**
     * 字段名称
     * @return 字段名称
     */
    String fieldName();

    /**
     * 字段类型
     * @return 字段类型
     */
    String fieldType();
}


