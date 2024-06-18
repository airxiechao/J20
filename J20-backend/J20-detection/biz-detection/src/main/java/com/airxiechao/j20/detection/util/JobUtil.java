package com.airxiechao.j20.detection.util;

import com.airxiechao.j20.detection.api.pojo.constant.ConstJobStatus;

import java.util.List;

/**
 * 任务辅助类
 */
public class JobUtil {
    /**
     * 任务是否是停止状态
     * @param status 任务状态
     * @return 是否是停止状态
     */
    public static boolean isStopped(String status){
        return List.of(
                ConstJobStatus.NOT_CREATED,
                ConstJobStatus.CANCELED,
                ConstJobStatus.FAILED,
                ConstJobStatus.FINISHED
        ).contains(status);
    }
}
