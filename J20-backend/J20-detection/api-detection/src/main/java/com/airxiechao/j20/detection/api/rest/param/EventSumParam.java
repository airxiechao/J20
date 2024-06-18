package com.airxiechao.j20.detection.api.rest.param;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@Data
public class EventSumParam {
    private Date beginTime;
    private Date endTime;
    private String level;
    @NotBlank
    private String eventTypeId;
    @NotBlank
    private String sumField;
}
