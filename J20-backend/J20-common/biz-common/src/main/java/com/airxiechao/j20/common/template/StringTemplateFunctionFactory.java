package com.airxiechao.j20.common.template;

import com.airxiechao.j20.common.api.template.StringTemplateFunction;
import com.github.mustachejava.TemplateFunction;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 字符串标签函数的工厂实现
 */
@Slf4j
public class StringTemplateFunctionFactory {
    private static StringTemplateFunctionFactory instance = new StringTemplateFunctionFactory();
    public static StringTemplateFunctionFactory getInstance(){
        return instance;
    }

    /**
     * 字符串标签函数的类池
     */
    private List<Class<? extends TemplateFunction>> functions = new ArrayList<>();

    /**
     * 构造字符串标签函数的工厂。注册所有标签函数
     */
    public StringTemplateFunctionFactory() {
        String pkg = this.getClass().getPackage().getName();

        // 扫描标签函数
        Reflections reflections = new Reflections(pkg, Scanners.TypesAnnotated);
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(StringTemplateFunction.class);
        for (Class<?> cls : classes) {
            if(TemplateFunction.class.isAssignableFrom(cls)){
                log.info("注册模板标签函数[{}]", cls.getName());
                functions.add((Class<? extends TemplateFunction>)cls);
            }
        }
    }

    /**
     * 返回字符串标签函数的类列表
     * @return 标签函数的类列表
     */
    public List<Class<? extends TemplateFunction>> list(){
        return functions;
    }
}
