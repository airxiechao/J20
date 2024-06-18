package com.airxiechao.j20.probe.network.api.pojo.protocol;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 协议解析器注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Protocol {
    /**
     * 协议编码
     * @return 协议编码
     */
    String name();

    /**
     * 外部协议编码
     * @return 外部协议编码
     */
    String outer();
}
