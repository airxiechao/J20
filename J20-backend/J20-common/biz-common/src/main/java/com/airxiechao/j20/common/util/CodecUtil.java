package com.airxiechao.j20.common.util;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * 编码辅助类
 */
public class CodecUtil {
    /**
     * 计算 MD5 编码
     * @param message 消息
     * @return MD5编码
     */
    public static String md5(String message){
        return DigestUtils.md5Hex(message);
    }
}
