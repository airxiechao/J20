package com.airxiechao.j20.probe.network.util;

import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.introspector.PropertyUtils;

import java.io.FileInputStream;
import java.io.InputStream;

/**
 * YAML 辅助类
 */
public class YamlUtil {

    /**
     * 加载 YAML 文件
     * @param configFile 配置文件路径
     * @param cls 配置对象类型类
     * @return 配置对象
     * @param <T> 配置对象类型
     * @throws Exception 加载异常
     */
    public static <T> T load(String configFile, Class<T> cls) throws Exception {
        Constructor constructor = new Constructor(new LoaderOptions());
        PropertyUtils propertyUtils = constructor.getPropertyUtils();
        propertyUtils.setSkipMissingProperties(true);
        constructor.setPropertyUtils(propertyUtils);
        Yaml yaml = new Yaml(constructor);

        try(InputStream inputStream = new FileInputStream(configFile)){
            return yaml.loadAs(inputStream, cls);
        }
    }
}
