package com.airxiechao.j20.detection.api.pojo.rule;

import lombok.Data;

import java.io.Serializable;

/**
 * 规则中的滑动窗口描述对象
 */
@Data
public class RuleSlideWindow implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 窗口大小
     */
    private Long size;

    /**
     * 窗口大小的单位
     */
    private String sizeUnit;

    /**
     * 滑动步长的大小
     */
    private Long slide;

    /**
     * 滑动步长的单位
     */
    private String slideUnit;
}
