package com.airxiechao.j20.detection.db.record;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * 协议记录
 */
@Data
@Entity(name = "t_detection_protocol")
public class ProtocolRecord {
    /**
     * ID
     */
    @Id
    private String code;

    /**
     * 字段格式
     */
    @Column(columnDefinition = "TEXT")
    private String fieldSchema;
}
