package com.airxiechao.j20.detection.util;

import com.airxiechao.j20.common.api.pojo.log.Log;
import com.airxiechao.j20.common.util.FieldUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 排序辅助类的实现
 */
public class SortUtil {

    /**
     * 对日志序列进行排序
     * @param list 日志序列
     * @param sortField 排序字段
     * @return 已排序日志序列
     */
    public static List<Log> sortLogs(List<Log> list, String sortField){
        if(StringUtils.isBlank(sortField)) {
            return list.stream()
                    .sorted(Comparator.comparingLong(Log::getTimestamp))
                    .collect(Collectors.toList());
        }else{
            return list.stream()
                    .filter(log -> FieldUtil.logFieldIsNotNull(sortField, log))
                    .sorted(Comparator.comparingLong(log -> FieldUtil.renderLogToLongField(sortField, log)))
                    .collect(Collectors.toList());
        }
    }

    /**
     * 获取日志的排序值
     * @param log 日志
     * @param sortField 排序字段
     * @return 排序值
     */
    public static Long getLogSortValue(Log log, String sortField){
        if(StringUtils.isBlank(sortField)) {
            return log.getTimestamp();
        }else{
            return FieldUtil.renderLogToLongField(sortField, log);
        }
    }

}
