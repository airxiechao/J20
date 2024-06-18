package com.airxiechao.j20.common.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 正则表达式工厂的实现
 */
public class RegexFactory {
    private static RegexFactory instance = new RegexFactory();
    public static RegexFactory getInstance(){
        return instance;
    }

    /**
     * 正则表达式池
     */
    private Map<String, Pattern> patterns = new HashMap<>();

    /**
     * 获取正则表达式
     * @param regex 正则表达式内容
     * @return 正则表达式
     */
    public Pattern get(String regex){
        if(!patterns.containsKey(regex)){
            Pattern pattern = Pattern.compile(regex);
            patterns.put(regex, pattern);
        }

        return patterns.get(regex);

    }
}
