package com.airxiechao.j20.detection.api.pojo.task;

import com.airxiechao.j20.detection.api.pojo.rule.Rule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 任务描述对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Task implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    private String id;

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 输入数据源ID
     */
    private String srcDataSourceId;

    /**
     * 输入源初值读取位置
     */
    private String startingOffsetStrategy;

    /**
     * 任务包含的规则列表
     */
    private List<Rule> rules;

    /**
     * Flink任务实例ID
     */
    private String jobId;

    /**
     * Flink任务状态
     */
    private String jobStatus;

    /**
     * Flink任务日志读入数量
     */
    private Long jobNumLogIn;

    /**
     * Flink任务事件输出数量
     */
    private Long jobNumEventOut;

    /**
     * Flink任务最后更新时间
     */
    private Date jobLastUpdateTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 最后更新时间
     */
    private Date lastUpdateTime;

    public Task(String name, String description, String srcDataSourceId, String startingOffsetStrategy, List<Rule> rules) {
        this.name = name;
        this.description = description;
        this.srcDataSourceId = srcDataSourceId;
        this.startingOffsetStrategy = startingOffsetStrategy;
        this.rules = rules;
    }

    public Task(String id, String name, String description, String srcDataSourceId, String startingOffsetStrategy, List<Rule> rules) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.srcDataSourceId = srcDataSourceId;
        this.startingOffsetStrategy = startingOffsetStrategy;
        this.rules = rules;
    }
}
