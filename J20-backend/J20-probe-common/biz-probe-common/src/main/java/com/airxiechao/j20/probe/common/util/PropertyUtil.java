package com.airxiechao.j20.probe.common.util;

/**
 * 参数配置辅助类实现
 * 获取环境变量或-D参数，-D参数名是环境变量名经过大写转小写，下划线转点号后的名称
 */
public class PropertyUtil {

    /**
     * 获取环境变量或-D参数
     * @param name 参数名
     * @return 参数值
     */
    public static String get(String name){
        return get(name, null);
    }

    /**
     * 获取环境变量或-D参数，带默认值
     * @param name 参数名
     * @param def 默认值
     * @return 参数值
     */
    public static String get(String name, String def){
        // 先获取环境变量
        if(System.getenv().containsKey(name)){
            String env = System.getenv(name);
            return env;
        }

        // 再获取-D参数，-D参数名是环境变量名经过大写转小写，下划线转点号后的名称
        name = name.toLowerCase().replaceAll("_", ".");
        if(System.getProperties().containsKey(name)){
            String prop = System.getProperty(name);
            return prop;
        }

        return def;
    }
}
