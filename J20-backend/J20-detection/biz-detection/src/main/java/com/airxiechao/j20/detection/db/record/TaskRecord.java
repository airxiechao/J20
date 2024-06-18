package com.airxiechao.j20.detection.db.record;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

/**
 * 任务记录
 */
@Data
@Entity(name = "t_detection_task")
public class TaskRecord {
    /**
     * ID
     */
    @Id
    private String id;

    /**
     * 名称
     */
    @Column(unique = true)
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 输入数据源 ID
     */
    private String srcDataSourceId;

    /**
     * 输入起始位置
     */
    private String startingOffsetStrategy;

    /**
     * 规则配置
     */
    @Column(columnDefinition = "TEXT")
    private String rules;

    /**
     * 任务实列 ID
     */
    private String jobId;

    /**
     * 任务实列状态
     */
    private String jobStatus;

    /**
     * 任务实例日志输入数
     */
    private Long jobNumLogIn;

    /**
     * 任务实例事件输出数
     */
    private Long jobNumEventOut;

    /**
     * 任务实例最后更新时间
     */
    private Date jobLastUpdateTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 最后修改时间
     */
    private Date lastUpdateTime;
}
