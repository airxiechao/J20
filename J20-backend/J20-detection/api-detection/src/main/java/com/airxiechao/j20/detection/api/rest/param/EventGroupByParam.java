package com.airxiechao.j20.detection.api.rest.param;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@Data
public class EventGroupByParam {
    private Date beginTime;
    private Date endTime;
    private String level;
    private String eventTypeId;
    @NotBlank
    private String groupByField;
    private String sumField;
}
