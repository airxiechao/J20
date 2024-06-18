package com.airxiechao.j20.common.api.express;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表达式操作符的注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ExpressOperator {
    /**
     * 操作符名称
     * @return 操作符名称
     */
    String value();
}


