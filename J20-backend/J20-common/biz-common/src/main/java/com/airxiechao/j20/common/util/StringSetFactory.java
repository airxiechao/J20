package com.airxiechao.j20.common.util;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 字符串集合工厂的实现
 */
public class StringSetFactory {
    private static StringSetFactory instance = new StringSetFactory();
    public static StringSetFactory getInstance(){
        return instance;
    }

    /**
     * 字符串集合池
     */
    private Map<String, Set<String>> map = new ConcurrentHashMap<>();

    /**
     * 获取内容的字符串集合
     * @param value 内容
     * @return 字符串集合
     */
    public Set<String> get(String value){
        Set<String> set = map.get(value);
        if(null == set){
            set = splitToSet(value);
            map.put(value, set);
        }

        return set;
    }

    /**
     * 将内容分割为集合
     * @param value 内容
     * @return 字符串集合
     */
    private static Set<String> splitToSet(String value){
        Set<String> set = new HashSet<>();
        String[] lines = value.split(",");
        for (String line : lines) {
            set.add(line.trim());
        }
        return set;
    }
}
