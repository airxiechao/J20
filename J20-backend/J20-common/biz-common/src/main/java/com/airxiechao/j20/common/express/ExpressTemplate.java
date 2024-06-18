package com.airxiechao.j20.common.express;

import com.ql.util.express.DefaultContext;
import lombok.extern.slf4j.Slf4j;

/**
 * 表达式对象的实现
 */
@Slf4j
public class ExpressTemplate {

    /**
     * 表达式内容
     */
    private String express;

    /**
     * 表达式的上下文对象
     */
    private Object value;

    /**
     * 发生错误时，是否降级处理（返回 value）
     */
    private boolean degraded;

    /**
     * 构造表达式
     * @param express 表达式内容
     * @param value 表达式上下文对象
     * @param degraded 发生错误时，是否降级处理
     */
    public ExpressTemplate(String express, Object value, boolean degraded) {
        this.express = express;
        this.value = value;
        this.degraded = degraded;
    }

    /**
     * 执行表达式
     * @return 执行结果
     * @throws Exception 执行异常
     */
    public Object execute() throws Exception {
        DefaultContext<String, Object> context = new DefaultContext<>();
        context.put("value", value);

        try {
            return ExpressRunnerFactory.getInstance().getRunner().execute(express, context, null, true, false);
        }catch (Exception e){
            log.error("表达式[{}]当值为[{}]时执行发生错误", express, value);

            if(degraded){
                return value;
            }else{
                throw e;
            }
        }

    }
}
