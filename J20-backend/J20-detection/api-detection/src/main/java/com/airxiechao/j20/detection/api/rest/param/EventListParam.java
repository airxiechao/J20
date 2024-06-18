package com.airxiechao.j20.detection.api.rest.param;

import com.airxiechao.j20.common.api.pojo.rest.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
public class EventListParam extends PageParam {
    private Date beginTime;
    private Date endTime;
    private String eventTypeId;
    private String level;
    private String query;
}
