package com.airxiechao.j20.detection.api.pojo.exception;

/**
 * 启动任务失败异常
 */
public class TaskJobFailedException extends Exception {
    public TaskJobFailedException() {
    }

    public TaskJobFailedException(String message) {
        super(message);
    }

    public TaskJobFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public TaskJobFailedException(Throwable cause) {
        super(cause);
    }

    public TaskJobFailedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
