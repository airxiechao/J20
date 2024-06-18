package com.airxiechao.j20.detection.api.rest.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class EventGetParam {
    @NotNull
    private String id;
    @NotNull
    private Long timestamp;
}
