package com.airxiechao.j20.common.util;

import lombok.Getter;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Spring 辅助类
 */
@Component
public class ApplicationContextUtil implements ApplicationContextAware {

    /**
     * Spring 上下文
     */
    @Getter
    private static ApplicationContext context;

    /**
     * 设置 Spring 上下文
     * @param applicationContext 上下文
     * @throws BeansException 设置异常
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApplicationContextUtil.context = applicationContext;
    }
}
