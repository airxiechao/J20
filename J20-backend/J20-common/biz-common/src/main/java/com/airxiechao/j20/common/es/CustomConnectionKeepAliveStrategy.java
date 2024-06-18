package com.airxiechao.j20.common.es;

import org.apache.http.HttpResponse;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.protocol.HttpContext;

import java.util.concurrent.TimeUnit;

/**
 * ES客户端的自定义的 keep-alive 实现。默认保持分钟数 {@value #MAX_KEEP_ALIVE_MINUTES}
 */
public class CustomConnectionKeepAliveStrategy extends DefaultConnectionKeepAliveStrategy {

    /**
     * 静态实例
     */
    public static final CustomConnectionKeepAliveStrategy INSTANCE = new CustomConnectionKeepAliveStrategy();

    private CustomConnectionKeepAliveStrategy(){
        super();
    }

    /**
     * 默认 keep-alive 分钟数 {@value #MAX_KEEP_ALIVE_MINUTES}
     */
    private static final long MAX_KEEP_ALIVE_MINUTES = 5;

    @Override
    public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
        long keepAliveDuration =  super.getKeepAliveDuration(response, context);

        if(keepAliveDuration < 0){
            keepAliveDuration = TimeUnit.MINUTES.toMillis(MAX_KEEP_ALIVE_MINUTES);
        }

        return keepAliveDuration;
    }
}
