package com.airxiechao.j20.detection.job.condition;

import com.airxiechao.j20.common.api.pojo.log.Log;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 尽早输出窗口状态
 */
@Data
public class StatisticsEagerState implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 是否触发
     */
    private boolean fired = false;

    /**
     * 日志列表
     */
    private List<Log> list = new ArrayList<>();
}
