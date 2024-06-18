package com.airxiechao.j20.common.util;

import java.util.UUID;

/**
 * UUID 辅助类
 */
public class UuidUtil {
    /**
     * 生成随机 UUID
     * @return UUID
     */
    public static String random(){
        return UUID.randomUUID().toString();
    }
}
