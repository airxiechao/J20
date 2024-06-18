package com.airxiechao.j20.common.api.pojo.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 导出字段的注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ExportField {
    /**
     * 导出名称
     * @return 导出名称
     */
    String value();

    /**
     * 导出模板
     * @return 导出模板
     */
    String template() default "";
}


