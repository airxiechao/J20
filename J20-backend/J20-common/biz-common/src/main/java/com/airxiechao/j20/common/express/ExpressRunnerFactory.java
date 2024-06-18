package com.airxiechao.j20.common.express;

import com.airxiechao.j20.common.api.express.ExpressFunction;
import com.airxiechao.j20.common.api.express.ExpressOperator;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.instruction.op.OperatorBase;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.lang.reflect.Method;
import java.util.Set;


/**
 * 表达式执行器的工厂实现
 */
@Slf4j
public class ExpressRunnerFactory {
    @Getter
    private static ExpressRunnerFactory instance = new ExpressRunnerFactory();

    @Getter
    private ExpressRunner runner = new ExpressRunner();

    public ExpressRunnerFactory() {
        String pkg = this.getClass().getPackage().getName().split("\\.")[0];

        // 扫描扩展函数
        Reflections reflections = new Reflections(pkg, Scanners.MethodsAnnotated, Scanners.TypesAnnotated);
        Set<Method> methods = reflections.getMethodsAnnotatedWith(ExpressFunction.class);
        for (Method method : methods) {
            String methodName = method.getName();
            String className = method.getDeclaringClass().getName();

            try {
                log.info("注册表达式函数[{}:{}.{}]", methodName, className, methodName);
                runner.addFunctionOfClassMethod(methodName, className, methodName, method.getParameterTypes(), null);
            } catch (Exception e) {
                log.error("注册表达式函数[{}:{}.{}]发生错误", methodName, className, methodName, e);
            }
        }

        // 扫描扩展操作符
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(ExpressOperator.class);
        for (Class<?> cls : classes) {
            if(!OperatorBase.class.isAssignableFrom(cls)){
                continue;
            }

            ExpressOperator ann = cls.getAnnotation(ExpressOperator.class);
            String name = ann.value();
            String className = cls.getName();

            try {
                log.info("注册表达式操作符[{}:{}]", name, className);
                runner.addFunction(name, (OperatorBase)cls.getDeclaredConstructor().newInstance());
            } catch (Exception e) {
                log.error("注册表达式操作符[{}:{}]发生错误", name, className, e);
            }
        }
    }

}
