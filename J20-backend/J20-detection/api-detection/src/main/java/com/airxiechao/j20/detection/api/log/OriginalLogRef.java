package com.airxiechao.j20.detection.api.log;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 原始日志的引用
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OriginalLogRef {
    /**
     * 原始日志ID
     */
    private String id;
}
