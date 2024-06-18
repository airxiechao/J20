package com.airxiechao.j20.detection.api.pojo.job;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Flink中任务规则类型的注解。每种规则类型的实现都需要添加该注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface TaskJob {
    /**
     * 任务类型
     * @return 任务类型
     */
    String value();
}

